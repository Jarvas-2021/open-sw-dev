package com.jarvas.mappyapp.adapter;

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

import java.util.ArrayList;
import java.util.List;

public class ResultRecyclerAdapter extends RecyclerView.Adapter<ResultRecyclerAdapter.ViewHolder> {
    private ArrayList<ResultItem> mResultList ;
    private StarDatabase database;

    @NonNull
    @Override
    public ResultRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultRecyclerAdapter.ViewHolder holder, int position) {
        holder.onBind(mResultList.get(position));

        database = StarDatabase.getInstance(ContextStorage.getCtx());

        holder.starButton1.setOnClickListener(v -> {
            holder.starButton1.setVisibility(View.INVISIBLE);
            holder.starButton2.setVisibility(View.VISIBLE);

            Star star = new Star();
            star.content = holder.content.getText().toString();
            database.starDAO().insertStar(star);
            System.out.println(star.content);

        });
    }

    public void setResultList(ArrayList<ResultItem> list){
        this.mResultList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        System.out.println("result size:"+mResultList.size());
        return mResultList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        ImageView starButton1;
        ImageView starButton2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.result_data);
            starButton1 = (ImageView) itemView.findViewById(R.id.save_img);
            starButton2 = (ImageView) itemView.findViewById(R.id.save_img2);
        }

        void onBind(ResultItem item){
            System.out.println("bind"+mResultList);
            content.setText(item.getContent());

        }

    }


}
