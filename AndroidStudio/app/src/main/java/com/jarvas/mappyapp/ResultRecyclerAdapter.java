package com.jarvas.mappyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import javax.xml.transform.Result;

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.result_data);
        }

        void onBind(ResultItem item){
            System.out.println("bind"+mResultList);
            content.setText(item.getContent());
        }
    }
}
