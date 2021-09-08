package com.example.secondsunshine;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingActicity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    FragmentManager     mfragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ActionBar actionBar = this.getSupportActionBar();

        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        if(savedInstanceState == null) {
            mfragmentManager = getSupportFragmentManager();
            mfragmentManager.beginTransaction()
                    .replace(R.id.settingfragment_container, new SettingFragment())
                    .commit();
        }
    }


// 뒤로가기 버튼 동작하게 하기
    @Override
    public boolean onSupportNavigateUp() {

        if(mfragmentManager.getBackStackEntryCount() == 0) {
            finish();
        }else {
            mfragmentManager.popBackStack();
        }

        return super.onSupportNavigateUp();
    }

    //    계층 구조를 여러 개의 화면으로 분할
    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        // Instantiate the new Fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);


        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settingfragment_container, fragment)
                .addToBackStack(null)
                .commit();
        return true;
    }
}
