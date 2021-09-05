package com.example.secondsunshine;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.content.AsyncTaskLoader;

import com.example.secondsunshine.Utility.NetworkUtil;

import java.io.IOException;
import java.net.URL;

public class FragmentMain extends Fragment {


    TextView mtextView;

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
        mtextView = rootView.findViewById(R.id.tv_mainfragment);
        mtextView.setText("프레그먼트 설정완료");


        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId)
        {
            case R.id.action_search:
                Toast.makeText(mContext, "action_search", Toast.LENGTH_LONG).show();

                 URL forecastURL = NetworkUtil.buildURL(NetworkUtil.OPENWEATHERMAP_BASE_URL);

                new WeatherTask().execute(forecastURL);


               break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }





}
