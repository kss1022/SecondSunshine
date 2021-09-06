package com.example.secondsunshine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secondsunshine.Utility.NetworkUtil;

import java.net.URL;

public class FragmentMain extends Fragment
        implements CustomAdapter.listItemClickLisener {

    RecyclerView mRecylcerView;
    RecyclerView.LayoutManager mLayoutManager;
    CustomAdapter mCustomAdapter;

    Toast mToast;

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

        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecylcerView.setLayoutManager(mLayoutManager);
        mRecylcerView.setHasFixedSize(true);

        mCustomAdapter = new CustomAdapter(50, this);

        mRecylcerView.setAdapter(mCustomAdapter);

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


    private void actionRefresh()
    {
        mToast = Toast.makeText(mContext, "action_search", Toast.LENGTH_LONG);
        mToast.show();

        URL forecastURL = NetworkUtil.buildURL(NetworkUtil.OPENWEATHERMAP_BASE_URL);

        new WeatherTask().execute(forecastURL);
    }

}
