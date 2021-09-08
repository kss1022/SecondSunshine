package com.example.secondsunshine;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.secondsunshine.CustomAdapter.*;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    final private listItemClickLisener mListItemClickLisener;

    String[] mWeatherData;


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
        Log.d("CustomAdapter :" ,"getItemtCount " );
        if(mWeatherData == null) return 0;
        return mWeatherData.length;
    }

    public void setData(String[] data)
    {
        mWeatherData = data;
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
            listItemTextView.setText(mWeatherData[number]);
        }


        @Override
        public void onClick(View v) {
            int clickPosition = getAbsoluteAdapterPosition();
            String weatherData = mWeatherData[clickPosition];
            mListItemClickLisener.onClickItem(clickPosition, weatherData);
        }
    }

    public interface listItemClickLisener {
        void onClickItem(int number, String data);
    }

}
