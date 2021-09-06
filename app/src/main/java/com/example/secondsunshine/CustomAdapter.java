package com.example.secondsunshine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.secondsunshine.CustomAdapter.*;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    int mListItemNumber;
    final private listItemClickLisener mListItemClickLisener;

    public CustomAdapter(int number, listItemClickLisener listItemClickLisener) {
        super();

        mListItemNumber = number;
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
        return mListItemNumber;
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
            listItemTextView.setText(Integer.toString(number));
        }


        @Override
        public void onClick(View v) {
            int clickPosition = getAbsoluteAdapterPosition();
            mListItemClickLisener.onClickItem(clickPosition);
        }
    }

    public interface listItemClickLisener {
        void onClickItem(int number);
    }

}
