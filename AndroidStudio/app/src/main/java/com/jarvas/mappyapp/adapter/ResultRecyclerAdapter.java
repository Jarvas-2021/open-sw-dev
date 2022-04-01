package com.jarvas.mappyapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class ResultRecyclerAdapter extends RecyclerView.Adapter<ResultRecyclerAdapter.ViewHolder> {
    private ArrayList<ResultItem> mResultList ;

    @NonNull
    @Override
    public ResultRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultRecyclerAdapter.ViewHolder holder, int position) {
        holder.onBind(mResultList.get(position));
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
        ImageView starButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.result_data);
            starButton = (ImageView) itemView.findViewById(R.id.save_img);
        }

        void onBind(ResultItem item){
            System.out.println("bind"+mResultList);
            content.setText(item.getContent());

            starButton.setOnClickListener(v -> {
                starButton.setImageResource(R.drawable.ic_baseline_star_24);
                InsertRunnable insertRunnable = new InsertRunnable();
                Thread addThread = new Thread(insertRunnable);
                addThread.start();
            });
        }

        class InsertRunnable implements Runnable {
            @Override
            public void run() {
                Star star = new Star();
                star.content = content.getText().toString();
                StarDatabase.getInstance(ContextStorage.getCtx()).starDAO().insertStar(star);
            }
        }
    }


}
