package com.example.secondsunshine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class SettingFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    AlertDialog.Builder adb;
    boolean dialogResult;

    EditTextPreference mEditTextPreference_Location;
    EditTextPreference mEditTextPreference_Day;

    ListPreference mListPreference_Temperature;
    CheckBoxPreference mCheckBoxPreference_Show;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting, rootKey);


        Context context = getContext();
        mEditTextPreference_Location = findPreference(context.getString(R.string.pref_location_key));
        mEditTextPreference_Day = findPreference(context.getString(R.string.pref_day_key));
        mListPreference_Temperature = findPreference(context.getString(R.string.pref_temperature_key));
        mCheckBoxPreference_Show = findPreference(context.getString(R.string.pref_checkbox_key));


        if (mEditTextPreference_Location != null) {
            //
            mEditTextPreference_Location.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());

            //TEXT만 입력받게 설정
            mEditTextPreference_Location.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                @Override
                public void onBindEditText(@NonNull EditText editText) {
                    editText.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            });
        }

        if(mEditTextPreference_Day!= null)
        {
            mEditTextPreference_Day.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());

            mEditTextPreference_Day.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                @Override
                public void onBindEditText(@NonNull EditText editText) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            });
        }

        //Label은 번역이 되지만 Key값은 번역(translatable)이 되지 않기 때문에
        // ListPreference는 값을 가져와서 Summary를 설정해준다
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        String listPrefValue = sharedPreferences.getString(mListPreference_Temperature.getKey(),
                "");
        setPreferenceSummary(mListPreference_Temperature, listPrefValue);

        //체크박스를 눌렀을시 대화상자 사용
        if (mCheckBoxPreference_Show != null) {
            mCheckBoxPreference_Show.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (mCheckBoxPreference_Show.isChecked()) {
                        adb.setMessage("알람을 보이게 않게 하겠습니까?");
                    } else {
                        adb.setMessage("알람을 보이게 하겠습니까?");
                    }
                    adb.show();
                    return false;
                }
            });
        }


        alertDialogSetting();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        //ListPreference만 확인해줌
        if (preference != null) {
            if ((preference instanceof ListPreference)) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
            }
        }
    }


    //  ListPreference만 확인해줌
    private void setPreferenceSummary(Preference preference, String key) {
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(key);
            if (prefIndex >= 0) {
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
    }


    //대화 상자 설정
    private void alertDialogSetting() {
        adb = new AlertDialog.Builder(getActivity());


        adb.setMessage("")
                .setTitle("Show or Hidden")
                .create();


        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCheckBoxPreference_Show.setChecked(!mCheckBoxPreference_Show.isChecked());
            }
        });

        adb.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCheckBoxPreference_Show.setChecked(mCheckBoxPreference_Show.isChecked());
            }
        });

        AlertDialog dialog = adb.create();
    }


}
