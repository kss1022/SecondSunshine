package com.example.secondsunshine;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.secondsunshine.Data.AppDataBase;
import com.example.secondsunshine.Data.AppExcutors;
import com.example.secondsunshine.Data.WeatherEntry;
import com.example.secondsunshine.Utility.NetworkUtil;

import java.net.URL;
import java.util.Date;

public class FragmentDetail extends Fragment {


    private static final String INSTANCE_WEATEHR_ID = "instanceWeatherId";

    private AppDataBase mDatabase;

    TextView mTextView_date;
    TextView mTextView_description;
    TextView mTextView_maxTemp;
    TextView mTextView_minTemp;
    Button mButton_changeData;

    Toast mToast;


    int mWeatherId;
    private static final int DEFAULT_WEATEHR_ID = -1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mTextView_date = rootView.findViewById(R.id.tv_detail_date);
        mTextView_description = rootView.findViewById(R.id.tv_detail_description);
        mTextView_maxTemp = rootView.findViewById(R.id.tv_detail_max_temp);
        mTextView_minTemp = rootView.findViewById(R.id.tv_detail_min_temp);
        mButton_changeData = rootView.findViewById(R.id.bt_detail_modify);

        mButton_changeData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionChange();
            }
        });

        mDatabase = AppDataBase.getInstance(getContext().getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_WEATEHR_ID)) {
            mWeatherId = savedInstanceState.getInt(INSTANCE_WEATEHR_ID, DEFAULT_WEATEHR_ID);
        }

        Intent intent = getActivity().getIntent();


        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mWeatherId = intent.getIntExtra(Intent.EXTRA_TEXT, DEFAULT_WEATEHR_ID);


            AddTaskViewModelfactory factroy = new AddTaskViewModelfactory(mDatabase, mWeatherId);
            final AddTaskViewModel viewModel = ViewModelProviders.of(this, factroy)
                    .get(AddTaskViewModel.class);


            viewModel.getWeather().observe(getActivity(), new Observer<WeatherEntry>() {
                @Override
                public void onChanged(WeatherEntry weatherEntry) {
                    viewModel.getWeather().removeObserver(this);
                    populateUi(weatherEntry);
                }
            });
        }

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.refresh, menu);
        inflater.inflate(R.menu.share, menu);
        inflater.inflate(R.menu.setting, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuId = item.getItemId();

        switch (menuId) {
            case R.id.action_refresh:
                actionRefresh();
                break;
            case R.id.action_share:
                actionShare();
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


    private void actionRefresh() {
        mToast = Toast.makeText(getActivity(), "action_search", Toast.LENGTH_LONG);
        mToast.show();

//        URL forecastURL = NetworkUtil.buildURL("Seoul", "metric", NetworkUtil.DAY_NUMBER);

//        new WeatherTask().execute(forecastURL);
    }


    private void actionShare() {
//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.putExtra(Intent.EXTRA_TEXT, mTextView.getText());
//        sendIntent.setType("text/plain");
//
//        Intent shareIntent = Intent.createChooser(sendIntent, null);
//        startActivity(shareIntent);
        mToast = Toast.makeText(getActivity(), "action_share", Toast.LENGTH_LONG);
        mToast.show();

        String mimeType = "text/plain";
        String title = "Learning How to Share";
        String textToShare = (mTextView_description.getText()).toString();


        ShareCompat.IntentBuilder.from(getActivity())
                .setChooserTitle(title)
                .setType(mimeType)
                .setText(textToShare)
                .startChooser();
    }


    private void actionChange() {
        long date = Long.parseLong( mTextView_date.getText().toString());
        String description = mTextView_description.getText().toString();
        double temp_max = Double.parseDouble(mTextView_maxTemp.getText().toString()) ;
        double temp_min = Double.parseDouble(mTextView_minTemp.getText().toString()) ;

        WeatherEntry weather = new WeatherEntry(
                date,
                description,
                111,
                temp_max,
                temp_min,
                65,
                65.2,
                new Date()
        );


        AppExcutors.getInstance().distIO().execute(new Runnable() {
            @Override
            public void run() {
                weather.setId(mWeatherId);
                mDatabase.weatherDao().updateWeather(weather);
            }
        });

    }


    private void populateUi(WeatherEntry weatherEntry) {
        long date = weatherEntry.getTime();
        String description = weatherEntry.getDescription();
        double temp_max = weatherEntry.getMax_temp();
        double temp_min = weatherEntry.getMin_temp();
        mTextView_date.setText(Long.toString(date));
        mTextView_description.setText(description);
        mTextView_maxTemp.setText(Double.toString(temp_max));
        mTextView_minTemp.setText(Double.toString(temp_min));
    }



}
