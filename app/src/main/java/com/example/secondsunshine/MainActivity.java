package com.example.secondsunshine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.secondsunshine.Data.WeatherEntry;

public class MainActivity extends AppCompatActivity
implements FragmentMain.OnHeadlineSelectedListener{

    boolean mDual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);






        if(findViewById(R.id.detailfragment_container) != null) {
            mDual = true;
            FragmentDetail detailFragment = (FragmentDetail)getSupportFragmentManager().
                    findFragmentById(R.id.detailfragment_container);


            if (detailFragment == null) {
                FragmentDetail DetailFragment = new FragmentDetail();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detailfragment_container, DetailFragment)
                        .commit();
            }
        }


        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            FragmentMain fragmentMain = new FragmentMain();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.mainfragment_container, fragmentMain)
                    .commit();
        }
    }

    @Override
    public void onItemSelected(WeatherEntry weatherEntry) {
        FragmentDetail detailFragment = (FragmentDetail)getSupportFragmentManager().
                findFragmentById(R.id.detailfragment_container);

        if(detailFragment != null)
        {
            detailFragment.populateUi(weatherEntry);
        }
        else
        {
            FragmentDetail newDetailFragment = new FragmentDetail();


            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detailfragment_container, newDetailFragment)
                    .commit();
        }
    }


    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);

        if(fragment instanceof  FragmentMain)
        {
            FragmentMain mainFragment =  (FragmentMain) fragment;
            mainFragment.setOnHeadlineSelectedListener(this);
        }
    }
}