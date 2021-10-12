package com.example.secondsunshine;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secondsunshine.Data.WeatherEntry;
import com.example.secondsunshine.Utility.TimeUtil;
import com.example.secondsunshine.Utility.WeatherUtil;
import com.example.secondsunshine.databinding.ListItemBinding;
import com.example.secondsunshine.databinding.ListItemTodayBinding;

import java.util.List;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    final private listItemClickLisener mListItemClickLisener;

    List<WeatherEntry> mWeatherEntryList;



    boolean mDaul;
    int mClickPosition;



    private static int VIEW_TYPE_TODAY = 0;
    private static int VIEW_TYPE_FUTURE_DAY = 1;


    public interface listItemClickLisener {
        void onClickItem(int id);
    }


    public CustomAdapter(listItemClickLisener listItemClickLisener) {
        super();
        mListItemClickLisener = listItemClickLisener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

        int layoutId;

        if (position == VIEW_TYPE_TODAY  && !mDaul ) {
            layoutId = R.layout.list_item_today;
            ListItemTodayBinding listItemTodayBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(viewGroup.getContext()),layoutId, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(listItemTodayBinding);
            return  viewHolder;
        } else {
            layoutId = R.layout.list_item;
            ListItemBinding listItemBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(viewGroup.getContext()),layoutId, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(listItemBinding);
            return  viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(holder.mBinding != null && mDaul) {
            holder.mBinding.listItemLayout.setSelected(mClickPosition == position);
        }
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mWeatherEntryList == null) return 0;
        return mWeatherEntryList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return VIEW_TYPE_TODAY;
        } else {
            return VIEW_TYPE_FUTURE_DAY;
        }
    }

    public List<WeatherEntry> getWeathers() {
        return mWeatherEntryList;
    }


    public void setWeatherEntryList(List<WeatherEntry> weatherEntryList) {
        mWeatherEntryList = weatherEntryList;
        notifyDataSetChanged();
    }

    public void setDaul(boolean mDaul) {
        this.mDaul = mDaul;
    }

    public void setClickPosition(int mClickPosition) {
        this.mClickPosition = mClickPosition;
    }


    // ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ListItemBinding mBinding;
        ListItemTodayBinding mTodayBinding;

        public ViewHolder(ListItemBinding binding) {
            super(binding.getRoot());

            mBinding = binding;
            mBinding.listItemLayout.setOnClickListener(this);
        }

        public ViewHolder(ListItemTodayBinding binding) {
            super(binding.getRoot());

            mTodayBinding = binding;
            mTodayBinding.listItemLayout.setOnClickListener(this);
        }

        void bind(int number) {
            WeatherEntry weatherData = mWeatherEntryList.get(number);

            long time = weatherData.getTime();

            String strTime = TimeUtil.convertUtcToLocal(time);

            String description = weatherData.getDescription();
            double temp_max = weatherData.getMax_temp();
            double temp_min = weatherData.getMin_temp();
            int weather_id = weatherData.getWeather_id();


            if (CustomAdapter.this.getItemViewType(number) == 0 && !mDaul) {
                mTodayBinding.listItemIvWeatherIcon.setImageResource(WeatherUtil.getLargeArtResourceIdForWeatherCondition(weather_id));
                mTodayBinding.listItemTvDayInfo.setText(strTime);
                mTodayBinding.listItemTvWeatherInfo.setText(description);
                mTodayBinding.listItemTvMaxTemp.setText(Double.toString(temp_max) + "°");
                mTodayBinding.listItemTvMinTemp.setText(Double.toString(temp_min) + "°");
            } else {
                mBinding.listItemIvWeatherIcon.setImageResource(WeatherUtil.getSmallArtResourceIdForWeatherCondition(weather_id));
                mBinding.listItemTvDayInfo.setText(strTime);
                mBinding.listItemTvWeatherInfo.setText(description);
                mBinding.listItemTvMaxTemp.setText(Double.toString(temp_max) + "°");
                mBinding.listItemTvMinTemp.setText(Double.toString(temp_min) + "°");
            }
        }


        @Override
        public void onClick(View v) {

            if(getAdapterPosition() == RecyclerView.NO_POSITION) return;

            notifyItemChanged(mClickPosition);
            mClickPosition =getAbsoluteAdapterPosition();
            notifyItemChanged(mClickPosition);

            mListItemClickLisener.onClickItem(mClickPosition);
        }
    }



}
