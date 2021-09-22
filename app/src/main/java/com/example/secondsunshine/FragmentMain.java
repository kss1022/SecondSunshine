package com.example.secondsunshine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secondsunshine.Data.AppDataBase;
import com.example.secondsunshine.Data.AppExcutors;
import com.example.secondsunshine.Data.WeatherContract;
import com.example.secondsunshine.Data.WeatherEntry;
import com.example.secondsunshine.Utility.NetworkUtil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class FragmentMain extends Fragment
        implements CustomAdapter.listItemClickLisener,
        LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = FragmentMain.class.getSimpleName();


    private static final int LOADER_ID = 22;
    private static final String LOADER_BUNDLE_URL_KEY = "search_query_key";


    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_DESCRIPTION = 1;
    public static final int INDEX_WEATHER_MAX_TEMP = 2;
    public static final int INDEX_WEATHER_MIN_TEMP = 3;


    RecyclerView mRecylcerView;
    RecyclerView.LayoutManager mLayoutManager;
    CustomAdapter mCustomAdapter;


    ProgressBar mProgressBar_LoadingBar;

    RelativeLayout mRelativeLayout;

    TextView mTextView_TestLocationSetting;
    TextView mTextView_TestDaySetting;
    TextView mTextView_TestTemperatureSetting;

    Toast mToast;


    String mLocationSetting;
    int mDaySetting;
    String mTemperatrueSetting;

    AppDataBase mDatabase;
    Context mContext;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mContext = getActivity();

        InitView(rootView);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AppExcutors.getInstance().distIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAbsoluteAdapterPosition();
                        List<WeatherEntry> weathers = mCustomAdapter.getmWeathers();
                        mDatabase.weatherDao().deleteWeather(weathers.get(position));
                        retrieveTask();
                    }
                });
            }
        }).attachToRecyclerView(mRecylcerView);




        mCustomAdapter = new CustomAdapter(this);
        mRecylcerView.setAdapter(mCustomAdapter);

        mDatabase = AppDataBase.getInstance(mContext);


//      설정 데이터를 가져와 설절한다
        getSettingData();
        mProgressBar_LoadingBar.setVisibility(View.INVISIBLE);

//        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
        return rootView;
    }




    @Override
    public void onDestroy() {
        super.onDestroy();

//        PreferenceChangeListener 를  설정했으면 unregister 해줘야한다
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.refresh, menu);
        inflater.inflate(R.menu.setting, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_refresh:
                actionRefresh();
                break;
            case R.id.action_setting:
                Intent settingIntent = new Intent(getActivity(), SettingActicity.class);
                startActivity(settingIntent);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    
//    TODO 로더 설정해주기
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {

        switch (id) {
            case LOADER_ID:
                Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;
                /* Sort order: Ascending by date */
                String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
                /*
                 * A SELECTION in SQL declares which rows you'd like to return. In our case, we
                 * want all weather data from today onwards that is stored in our weather table.
                 * We created a handy method to do that in our WeatherEntry class.
                 */

                return new CursorLoader(mContext,
                        forecastQueryUri,
                        null,
                        null,
                        null,
                        sortOrder);
            default:
                throw new RuntimeException("Loader Not Implemented" + id);
        }


    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

//        mCustomAdapter.swapCursor(mCursor);

        if (data.getCount() != 0) {
            mProgressBar_LoadingBar.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
//        mCustomAdapter.swapCursor(null);
    }


    @Override
    public void onClickItem(int number) {

        if (mToast != null) {
            mToast.cancel();
        }
        String toastMesssage = "Click positon : " + number;

        mToast = Toast.makeText(mContext, toastMesssage, Toast.LENGTH_LONG);
        mToast.show();


        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, number);
        startActivity(intent);
    }


    //뷰 설정해주기
    private void InitView(View rootView) {
        mRecylcerView = rootView.findViewById(R.id.recyclerview_main);
        mProgressBar_LoadingBar = rootView.findViewById(R.id.pb_main);


        mRelativeLayout = rootView.findViewById(R.id.rl_testData);
        mTextView_TestLocationSetting = rootView.findViewById(R.id.tv_main_location);
        mTextView_TestDaySetting = rootView.findViewById(R.id.tv_main_day);
        mTextView_TestTemperatureSetting = rootView.findViewById(R.id.tv_main_temperature);

        mLayoutManager = new LinearLayoutManager(getActivity());


        mRecylcerView.setLayoutManager(mLayoutManager);
        mRecylcerView.setHasFixedSize(true);

        DividerItemDecoration decoration = new DividerItemDecoration(mContext.getApplicationContext(),
                DividerItemDecoration.VERTICAL);
        mRecylcerView.addItemDecoration(decoration);
    }

    // DB Test
    private void actionRefresh() {
        mToast = Toast.makeText(mContext, "action_search", Toast.LENGTH_LONG);
        mToast.show();


        WeatherEntry weatherEntry = new WeatherEntry(
                2000,
                "오늘의 날씨!!!",
                111,
                32.1,
                32.3,
                65,
                65.2,
                new Date()
        );

        AppExcutors.getInstance().distIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.weatherDao().deleteAllWeather();

                mDatabase.weatherDao().insertWeather(weatherEntry);
                mDatabase.weatherDao().insertWeather(weatherEntry);
                mDatabase.weatherDao().insertWeather(weatherEntry);
                mDatabase.weatherDao().insertWeather(weatherEntry);
                mDatabase.weatherDao().insertWeather(weatherEntry);
                mDatabase.weatherDao().insertWeather(weatherEntry);
                mDatabase.weatherDao().insertWeather(weatherEntry);
                mDatabase.weatherDao().insertWeather(weatherEntry);
                List<WeatherEntry> testData =  mDatabase.weatherDao().loadAllWeather();
                Log.d(LOG_TAG, testData.get(0).getDescription());

            }
        });

        retrieveTask();

    }


//DB에서 Entry를 가져와 Adapter 데이터를 세팅해줌
    private void retrieveTask() {
        AppExcutors.getInstance().distIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<WeatherEntry> testData =  mDatabase.weatherDao().
                        loadAllWeather();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCustomAdapter.setWeatherEntryList(testData);
                    }
                });
            }
        });
    }


    //설정한 정보들을 SharedPreferences통해 가져온다
    private void getSettingData() {
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(mContext);

        mLocationSetting = prefs.getString(mContext.getString(R.string.pref_location_key), "Default");
        mDaySetting = Integer.parseInt(prefs.getString(mContext.getString(R.string.pref_day_key), "14"));
        mTemperatrueSetting = prefs.getString(mContext.getString(R.string.pref_temperature_key), "Default");
        Boolean checkboxStatus = prefs.getBoolean(mContext.getString(R.string.pref_checkbox_key), getResources().getBoolean(R.bool.pref_show_base_default));

        mTextView_TestLocationSetting.setText(mLocationSetting);
        mTextView_TestDaySetting.setText(Integer.toString(mDaySetting));
        mTextView_TestTemperatureSetting.setText(mTemperatrueSetting);


        if (checkboxStatus) {
            mRelativeLayout.setVisibility(View.VISIBLE);
        } else {
            mRelativeLayout.setVisibility(View.INVISIBLE);
        }

        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_location_key))) {
            mLocationSetting = sharedPreferences.getString(key,
                    "default");
            mTextView_TestLocationSetting.setText(mLocationSetting);
        }

        if (key.equals(getString(R.string.pref_day_key))) {
            mDaySetting = Integer.parseInt(sharedPreferences.getString(key,
                    "default"));
            mTextView_TestDaySetting.setText(Integer.toString(mDaySetting));
        }

        if (key.equals(getString(R.string.pref_temperature_key))) {
            mTextView_TestTemperatureSetting.setText(sharedPreferences.getString(key,
                    "default"));
        }


        if (key.equals(getString(R.string.pref_checkbox_key))) {

            Boolean checkboxStatus = sharedPreferences.getBoolean(key,
                    getResources().getBoolean(R.bool.pref_show_base_default));

            if (checkboxStatus) {
                mRelativeLayout.setVisibility(View.VISIBLE);
            } else {
                mRelativeLayout.setVisibility(View.INVISIBLE);
            }
        }

        actionRefresh();
    }
}
