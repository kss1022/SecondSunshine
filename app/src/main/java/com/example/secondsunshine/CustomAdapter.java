package com.example.secondsunshine;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secondsunshine.Data.WeatherEntry;

import java.util.List;

import static com.example.secondsunshine.CustomAdapter.*;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    final private listItemClickLisener mListItemClickLisener;

    List<WeatherEntry> mWeatherEntryList;


    public CustomAdapter(listItemClickLisener listItemClickLisener) {
        super();
        mListItemClickLisener = listItemClickLisener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(mWeatherEntryList == null) return 0;
        return mWeatherEntryList.size();
    }

    public List<WeatherEntry> getmWeathers()
    {
        return mWeatherEntryList;
    }


    public void setWeatherEntryList(List<WeatherEntry> weatherEntryList)
    {
        mWeatherEntryList = weatherEntryList;
        notifyDataSetChanged();
    }


    // ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {
        private TextView listItemTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            listItemTextView = (TextView) itemView.findViewById(R.id.tv_item);
            listItemTextView.setOnClickListener(this);
        }

        void bind(int number) {


            WeatherEntry weatherData =  mWeatherEntryList.get(number);

            long date =  weatherData.getTime();
            String description = weatherData.getDescription();
            double temp_max = weatherData.getMax_temp();
            double temp_min = weatherData.getMin_temp();


            String weatherSummary  = "data :  "+  date  + description + "   " + temp_max + "  /  "  + temp_min;

            listItemTextView.setText(weatherSummary);
        }


        @Override
        public void onClick(View v) {
            int elementId = mWeatherEntryList.get(getAbsoluteAdapterPosition()).getId();

            mListItemClickLisener.onClickItem(elementId);
        }
    }

    public interface listItemClickLisener {
        void onClickItem(int id);
    }

}
