package com.jarvas.mappyapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.models.Star;

import java.util.List;

public class StarAdapter extends RecyclerView.Adapter<StarAdapter.ViewHolder> {
    private List<Star> starList;

    public StarAdapter(List<Star> list) {
        starList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_result, parent, false);
        StarAdapter.ViewHolder vh = new StarAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Star item = starList.get(position);
        holder.content.setText(item.content);
    }

    @Override
    public int getItemCount() {
        return starList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView content;

        ViewHolder(View itemView) {
            super(itemView);

            content = itemView.findViewById(R.id.result_data);
        }
    }

}
