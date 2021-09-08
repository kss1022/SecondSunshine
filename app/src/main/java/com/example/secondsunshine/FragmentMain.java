package com.example.secondsunshine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secondsunshine.Utility.NetworkUtil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class FragmentMain extends Fragment
        implements CustomAdapter.listItemClickLisener,
        LoaderManager.LoaderCallbacks<String> ,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private  static final String LOG_TAG = FragmentMain.class.getSimpleName();


    private static final int LOADER_ID = 22;
    private static final String LOADER_BUNDLE_URL_KEY = "search_query_key";
    private static final String SAVEDINSTANCE_KEY = "search_query_key";


    RecyclerView mRecylcerView;
    RecyclerView.LayoutManager mLayoutManager;
    CustomAdapter mCustomAdapter;


    ProgressBar mProgressBar_LoadingBar;

    RelativeLayout mRelativeLayout;
    TextView    mTextView_TestLoaderData;
    TextView    mTextView_TestLocationSetting;
    TextView    mTextView_TestDaySetting;
    TextView    mTextView_TestTemperatureSetting;

    Toast mToast;


    String mLocationSetting;
    int mDaySetting;
    String mTemperatrueSetting;

    String[] mWeatherData = new String[mDaySetting];


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mContext = getActivity();

        mRecylcerView = rootView.findViewById(R.id.recyclerview_main);
        mProgressBar_LoadingBar = rootView.findViewById(R.id.pb_main);



        mRelativeLayout = rootView.findViewById(R.id.rl_testData);
        mTextView_TestLoaderData = rootView.findViewById(R.id.test_loaderData);
        mTextView_TestLocationSetting = rootView.findViewById(R.id.tv_main_location);
        mTextView_TestDaySetting = rootView.findViewById(R.id.tv_main_day);
        mTextView_TestTemperatureSetting = rootView.findViewById(R.id.tv_main_temperature);


        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecylcerView.setLayoutManager(mLayoutManager);
        mRecylcerView.setHasFixedSize(true);

        mCustomAdapter = new CustomAdapter(this);

        mRecylcerView.setAdapter(mCustomAdapter);

        if(savedInstanceState!= null)
        {
            if(savedInstanceState.containsKey(SAVEDINSTANCE_KEY))
            {

                String savedString = savedInstanceState.getString(SAVEDINSTANCE_KEY);
                mTextView_TestLoaderData.setText(savedString);

                if(savedString == null || savedString.equals(""))
                {
                    mTextView_TestLoaderData.setText("왜 데이터가없을깡?");
                }
            }
        }

//      설정 데이터를 가져와 설절한다
        getSettingData();

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(SAVEDINSTANCE_KEY , (mTextView_TestLoaderData.getText()).toString());
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(mContext) {

            String deliverData;


            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args == null) {
                    return;
                }
                if(deliverData != null)
                {
                    deliverResult(deliverData);
                }
                else {
                    mProgressBar_LoadingBar.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }


            @Override
            public String loadInBackground() {
                //Bundle에서 받은 URL에서 JSON 데이터를 반환해준다.
                String forecastURL = args.getString(LOADER_BUNDLE_URL_KEY);

                if (forecastURL == null || TextUtils.isEmpty(forecastURL)) {
                    return null;
                } else {
                    try {
                        URL newURL = new URL(forecastURL);

                        return NetworkUtil.getDataFromURL(newURL);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }


            @Override
            public void deliverResult(@Nullable String data) {
                deliverData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        mProgressBar_LoadingBar.setVisibility(View.INVISIBLE);

        //JSON 데이터를 통해 날씨 정보를 가져온다.
        if(data != null && !data.equals(""))
        {
            mWeatherData = NetworkUtil.getDataFromJson(data, mDaySetting);
            mCustomAdapter.setData(mWeatherData);
            mTextView_TestLoaderData.setText(mWeatherData[0]);
        }
        else
        {
            if(mToast!= null)
            {
                mToast.cancel();
            }
            mToast = Toast.makeText(mContext, "No Data From URL", Toast.LENGTH_LONG);
            mToast.show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }


    @Override
    public void onClickItem(int number, String data){

        if (mToast != null) {
            mToast.cancel();
        }
        String toastMesssage = "Click positon : " + number;

        mToast = Toast.makeText(mContext, toastMesssage, Toast.LENGTH_LONG);
        mToast.show();


        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, data);
        startActivity(intent);
    }



    private void actionRefresh() {
        mToast = Toast.makeText(mContext, "action_search", Toast.LENGTH_LONG);
        mToast.show();


        //URL을 만들어서 Bundle에 넘겨준다


        URL forecastURL = NetworkUtil.buildURL(mLocationSetting, mTemperatrueSetting, mDaySetting);

        Bundle bundle = new Bundle();
        bundle.putString(LOADER_BUNDLE_URL_KEY, forecastURL.toString());

        LoaderManager loaderManager = LoaderManager.getInstance(this);
        Loader<String> forecastLoader = loaderManager.getLoader(LOADER_ID);

        if(forecastLoader == null)
        {
            loaderManager.initLoader(LOADER_ID, bundle, this);
        }
        else
        {
            loaderManager.restartLoader(LOADER_ID, bundle, this);
        }

        mCustomAdapter.setData(mWeatherData);
    }




    private  void getSettingData()
    {
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(mContext);

        mLocationSetting = prefs.getString(mContext.getString( R.string.pref_location_key), "Default");
        mDaySetting = Integer.parseInt( prefs.getString(mContext.getString( R.string.pref_day_key), "14"));
        mTemperatrueSetting = prefs.getString(mContext.getString( R.string.pref_temperature_key), "Default");
        Boolean checkboxStatus = prefs.getBoolean(mContext.getString( R.string.pref_checkbox_key), getResources().getBoolean(R.bool.pref_show_base_default));

        mTextView_TestLocationSetting.setText(mLocationSetting);
        mTextView_TestDaySetting.setText( Integer.toString(mDaySetting) );
        mTextView_TestTemperatureSetting.setText(mTemperatrueSetting);


        if(checkboxStatus)
        {
            mRelativeLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            mRelativeLayout.setVisibility(View.INVISIBLE);
        }

        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals( getString(R.string.pref_location_key)))
        {
            mLocationSetting = sharedPreferences.getString(key,
                    "default");
            mTextView_TestLocationSetting.setText(mLocationSetting);
        }

        if(key.equals( getString(R.string.pref_day_key)))
        {
            mDaySetting =  Integer.parseInt( sharedPreferences.getString(key,
                    "default"));
            mTextView_TestDaySetting.setText( Integer.toString(mDaySetting));
        }

        if(key.equals( getString(R.string.pref_temperature_key)))
        {
            mTextView_TestTemperatureSetting.setText(sharedPreferences.getString(key,
                    "default"));
        }


        if(key.equals( getString(R.string.pref_checkbox_key)))
        {

            Boolean checkboxStatus = sharedPreferences.getBoolean(key,
                    getResources().getBoolean(R.bool.pref_show_base_default));

            if(checkboxStatus)
            {
                mRelativeLayout.setVisibility(View.VISIBLE);
            }
            else
            {
                mRelativeLayout.setVisibility(View.INVISIBLE);
            }
        }

        actionRefresh();
    }
}
