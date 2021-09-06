package com.example.secondsunshine;

import android.content.Intent;
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
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;

import com.example.secondsunshine.Utility.NetworkUtil;

import java.net.URL;

public class FragmentDetail extends Fragment {
    TextView mTextView;
    Toast mToast;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mTextView = rootView.findViewById(R.id.tv_detail);

        Intent intent = getActivity().getIntent();


        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            String intentString = intent.getStringExtra(Intent.EXTRA_TEXT);

            if (intentString != null) {
                mTextView.setText(intentString);
            }
        }


        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.refresh, menu);
        inflater.inflate(R.menu.share, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuId = item.getItemId();

        switch (menuId) {
            case R.id.action_refresh:
                actionRefresh();
            case R.id.action_share:
                actionShare();
                break;
            default:
            break;
        }


        return super.onOptionsItemSelected(item);
    }


    private void actionRefresh() {
        mToast = Toast.makeText(getActivity(), "action_search", Toast.LENGTH_LONG);
        mToast.show();

        URL forecastURL = NetworkUtil.buildURL(NetworkUtil.OPENWEATHERMAP_BASE_URL);

        new WeatherTask().execute(forecastURL);
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
        String textToShare =  ( mTextView.getText()) .toString();


      ShareCompat.IntentBuilder.from(getActivity())
              .setChooserTitle(title)
              .setType(mimeType)
              .setText(textToShare)
              .startChooser();
    }
}
