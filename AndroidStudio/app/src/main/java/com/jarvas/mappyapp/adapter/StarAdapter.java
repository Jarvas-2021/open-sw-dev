package com.jarvas.mappyapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.models.ResultItem;
import com.jarvas.mappyapp.models.Star;
import com.jarvas.mappyapp.models.database.StarDatabase;
import com.jarvas.mappyapp.utils.ContextStorage;

import java.util.List;

public class StarAdapter extends RecyclerView.Adapter<StarAdapter.ViewHolder> {
    private List<Star> starList;
    private StarDatabase database;

    public StarAdapter(List<Star> list) {
        starList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_star_result, parent, false);
        StarAdapter.ViewHolder vh = new StarAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.onBind(starList.get(position));
        database = StarDatabase.getInstance(ContextStorage.getCtx());
        holder.starButton1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                holder.starButton1.setVisibility(View.INVISIBLE);
                holder.starButton2.setVisibility(View.VISIBLE);

                Star star = starList.get(holder.getAdapterPosition());
                database.starDAO().deleteStar(star);


                int position = holder.getAdapterPosition();
                starList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,starList.size());
            }
        });

        holder.starButton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                holder.starButton1.setVisibility(View.VISIBLE);
                holder.starButton2.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return starList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView content;
        ImageView starButton1;
        ImageView starButton2;

        ViewHolder(View itemView) {
            super(itemView);

            content = (TextView) itemView.findViewById(R.id.star_result_data);
            starButton1 = (ImageView) itemView.findViewById(R.id.star_save_img);
            starButton2 = (ImageView) itemView.findViewById(R.id.star_save_img2);
        }

        void onBind(Star item){
            content.setText(item.content);
        }

    }

}
