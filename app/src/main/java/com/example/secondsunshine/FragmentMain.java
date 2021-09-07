package com.example.secondsunshine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secondsunshine.Utility.NetworkUtil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class FragmentMain extends Fragment
        implements CustomAdapter.listItemClickLisener,
        LoaderManager.LoaderCallbacks<String> {

    private  static final String LOG_TAG = FragmentMain.class.getSimpleName();


    private static final int LOADER_ID = 22;
    private static final String FORECASST_URL_KEY = "search_query_key";


    RecyclerView mRecylcerView;
    RecyclerView.LayoutManager mLayoutManager;
    CustomAdapter mCustomAdapter;


    ProgressBar mProgressBar_LoadingBar;
    TextView    mTextView_TestLoaderData;

    Toast mToast;


    String[] mWeatherData = new String[NetworkUtil.DAY_NUMBER];

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
        mTextView_TestLoaderData = rootView.findViewById(R.id.test_loaderData);

        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecylcerView.setLayoutManager(mLayoutManager);
        mRecylcerView.setHasFixedSize(true);

        mCustomAdapter = new CustomAdapter(50, this);

        mRecylcerView.setAdapter(mCustomAdapter);

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.refresh, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_refresh:
                actionRefresh();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(mContext) {

            String deliverData;


            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                Log.d(LOG_TAG, "onStartLoading");
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
                String forecastURL = args.getString(FORECASST_URL_KEY);
                Log.d(LOG_TAG, "loadInBackground");

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
        Log.d(LOG_TAG, "onLoadFinished");

        //JSON 데이터를 통해 날씨 정보를 가져온다.
        if(data != null && !data.equals(""))
        {
            mWeatherData = NetworkUtil.getDataFromJson(data);
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
    public void onClickItem(int number) {

        if (mToast != null) {
            mToast.cancel();
        }
        String toastMesssage = "Click positon : " + number;

        mToast = Toast.makeText(mContext, toastMesssage, Toast.LENGTH_LONG);
        mToast.show();


        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, toastMesssage);
        startActivity(intent);

    }


    private void actionRefresh() {
        mToast = Toast.makeText(mContext, "action_search", Toast.LENGTH_LONG);
        mToast.show();


        //URL을 만들어서 Bundle에 넘겨준다
        URL forecastURL = NetworkUtil.buildURL(NetworkUtil.OPENWEATHERMAP_BASE_URL);

        Bundle bundle = new Bundle();
        bundle.putString(FORECASST_URL_KEY, forecastURL.toString());

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
    }
}
