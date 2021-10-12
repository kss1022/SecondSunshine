package com.example.secondsunshine;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.secondsunshine.Data.AppDataBase;
import com.example.secondsunshine.Data.AppExcutors;
import com.example.secondsunshine.Data.WeatherEntry;
import com.example.secondsunshine.Utility.TimeUtil;
import com.example.secondsunshine.Utility.WeatherUtil;
import com.example.secondsunshine.databinding.FragmentDetailBinding;

import java.util.List;


public class FragmentDetail extends Fragment {
    public static final String INSTANCE_WEATEHR_ID = "instanceWeatherId";

    private AppDataBase mDatabase;
    FragmentDetailBinding mDataBinding;

    Toast mToast;

    int mWeatherId;
    private static final int DEFAULT_WEATEHR_ID = -1;

    boolean mDual;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

//        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);


        mDatabase = AppDataBase.getInstance(getContext().getApplicationContext());

        View detailsFrame = getActivity().findViewById(R.id.detailfragment_container);
        mDual = (detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE);


        if (mDual) {

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
        } else {
            // 알람을 통해 들어올떄 첫번쨰 위치의 날씨를 가져온다
            AddTaskViewModelfactory_position factroy = new AddTaskViewModelfactory_position(mDatabase);
            final AddTaskViewModel_position viewModel = ViewModelProviders.of(this, factroy)
                    .get(AddTaskViewModel_position.class);

            viewModel.getWeather().observe(getActivity(), new Observer<WeatherEntry>() {
                @Override
                public void onChanged(WeatherEntry weatherEntry) {
                    viewModel.getWeather().removeObserver(this);
                    populateUi(weatherEntry);
                }
            });
        }


        return mDataBinding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDataBinding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();

        inflater.inflate(R.menu.refresh, menu);
        inflater.inflate(R.menu.share, menu);
        inflater.inflate(R.menu.setting, menu);
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
    }


    private void actionShare() {
        mToast = Toast.makeText(getActivity(), "action_share", Toast.LENGTH_LONG);
        mToast.show();

        String mimeType = "text/plain";
        String title = "Learning How to Share";
        String textToShare = (mDataBinding.primaryInfo.detailTvWeatherInfo.getText()).toString();


        ShareCompat.IntentBuilder.from(getActivity())
                .setChooserTitle(title)
                .setType(mimeType)
                .setText(textToShare)
                .startChooser();
    }


    public void populateUi(WeatherEntry weatherEntry) {
        long date = weatherEntry.getTime();
        String data = TimeUtil.convertUtcToLocal(date);

        String description = weatherEntry.getDescription();
        double temp_max = weatherEntry.getMax_temp();
        double temp_min = weatherEntry.getMin_temp();
        int weather_id = weatherEntry.getWeather_id();

        double humadity = weatherEntry.getHumidity();
        double pressure = weatherEntry.getPressure();

        mDataBinding.primaryInfo.detailIvWeatherIcon.setImageResource(WeatherUtil.getLargeArtResourceIdForWeatherCondition(weather_id));
        mDataBinding.primaryInfo.detailTvDayInfo.setText(data);
        mDataBinding.primaryInfo.detailTvMaxTemp.setText(Double.toString(temp_max));
        mDataBinding.primaryInfo.detailtvMinTemp.setText(Double.toString(temp_min));
        mDataBinding.primaryInfo.detailTvWeatherInfo.setText(description);

        mDataBinding.extraDetail.extraHumadityKey.setText(Double.toString(humadity));
        mDataBinding.extraDetail.extraPressureKey.setText(Double.toString(pressure));

    }
}
