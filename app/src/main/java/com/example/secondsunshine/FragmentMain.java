package com.example.secondsunshine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secondsunshine.Data.AppDataBase;
import com.example.secondsunshine.Data.AppExcutors;
import com.example.secondsunshine.Data.WeatherEntry;
import com.example.secondsunshine.Sync.SyncUtils;
import com.example.secondsunshine.Sync.WeatherSyncTask;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FragmentMain extends Fragment
        implements CustomAdapter.listItemClickLisener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = FragmentMain.class.getSimpleName();
    public static final String CLICKED_POSITION = "clicked-position";


    RecyclerView mRecylcerView;
    RecyclerView.LayoutManager mLayoutManager;
    CustomAdapter mCustomAdapter;


    ProgressBar mProgressBar_LoadingBar;


    Toast mToast;


    AppDataBase mDatabase;
    Context mContext;


    boolean mDual;
    int mClickId;

    OnHeadlineSelectedListener mOnHeadlineSelectedListener;

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
                        List<WeatherEntry> weathers = mCustomAdapter.getWeathers();
                        mDatabase.weatherDao().deleteWeather(weathers.get(position));
                    }
                });
            }
        }).attachToRecyclerView(mRecylcerView);


        mCustomAdapter = new CustomAdapter(this);
        mRecylcerView.setAdapter(mCustomAdapter);

        mDatabase = AppDataBase.getInstance(mContext);


        //설정 데이터를 가져와 설절한다
        getSettingData();
        mProgressBar_LoadingBar.setVisibility(View.INVISIBLE);


        retrieveTask();

        //Service
        SyncUtils.initialize(mContext);


        View detailsFrame = getActivity().findViewById(R.id.detailfragment_container);
        mDual = (detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE);


        mCustomAdapter.setDaul(mDual);


        if (savedInstanceState != null) {
            mClickId = savedInstanceState.getInt(CLICKED_POSITION, 0);

           if(mDual) {
               mCustomAdapter.setClickPosition(mClickId);
               mRecylcerView.scrollToPosition(mClickId);
           }
        }


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
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();

        inflater.inflate(R.menu.refresh, menu);
        inflater.inflate(R.menu.setting, menu);
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
    public void onClickItem(int number) {

        if (mToast != null) {
            mToast.cancel();
        }
        String toastMesssage = "Click Apater Position : " + number;

        mToast = Toast.makeText(mContext, toastMesssage, Toast.LENGTH_LONG);
        mToast.show();


        mClickId = number;

        //듀얼 모드인 경우 WeatherData를 넘겨줌
        if (mDual) {
            if (mOnHeadlineSelectedListener != null && mCustomAdapter.getWeathers() != null) {
                mOnHeadlineSelectedListener.onItemSelected(mCustomAdapter.getWeathers().get(number));
            }
        } else {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, mCustomAdapter.getWeathers().get(number).getId());
            startActivity(intent);
        }
    }


    //뷰 설정해주기
    private void InitView(@NotNull View rootView) {
        mRecylcerView = rootView.findViewById(R.id.recyclerview_main);
        mProgressBar_LoadingBar = rootView.findViewById(R.id.pb_main);


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


        AsyncTask temp = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                WeatherSyncTask.syncWeather(mContext);
                return null;
            }
        };

        temp.execute();
    }


    //DB에서 Entry를 가져와 Adapter 데이터를 세팅해줌
    private void retrieveTask() {
        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        viewModel.getWeathers().observe(getActivity(), new Observer<List<WeatherEntry>>() {
            @Override
            public void onChanged(List<WeatherEntry> weatherEntries) {
                Log.d(LOG_TAG, "Update list of weathers from LiveData in ViewModel");
                mCustomAdapter.setWeatherEntryList(weatherEntries);

            }
        });

    }


    //설정한 정보들을 SharedPreferences통해 가져온다
    private void getSettingData() {
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(mContext);


        prefs.registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        actionRefresh();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CLICKED_POSITION, mClickId);
    }

    public interface OnHeadlineSelectedListener {
        void onItemSelected(WeatherEntry weatherEntry);
    }

    public void setOnHeadlineSelectedListener(OnHeadlineSelectedListener callback) {
        this.mOnHeadlineSelectedListener = callback;
    }


}
