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

import org.w3c.dom.Text;

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
            star.time = holder.time.getText().toString();
            star.fee = holder.price.getText().toString();
            star.walktime = holder.walktime.getText().toString();
            star.transfer = holder.transfer.getText().toString();
            star.distance = holder.distance.getText().toString();
            star.transport = holder.transType.getText().toString();
            star.transporttime = holder.interTime.getText().toString();
            star.path = holder.path.getText().toString();
            database.starDAO().insertStar(star);
            System.out.println(star.path);

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
        TextView time;
        TextView st;
        TextView dt;

        TextView path;
        TextView price;

        TextView price_;

        TextView walktime_;
        TextView transfer_;
        TextView distance_;

        TextView walktime;
        TextView transfer;
        TextView distance;

        TextView transType;
        TextView interTime;


        ImageView starButton1;
        ImageView starButton2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.tv_result_time);
            st = itemView.findViewById(R.id.tv_result_st);
            dt = itemView.findViewById(R.id.tv_result_dt);

            path = (TextView) itemView.findViewById(R.id.result_data);
            price = itemView.findViewById(R.id.tv_result_fee2);
            price_ = itemView.findViewById(R.id.tv_result_fee1);



            walktime = itemView.findViewById(R.id.tv_result_walktime2);
            transfer = itemView.findViewById(R.id.tv_result_transfer2);
            distance = itemView.findViewById(R.id.tv_result_distance2);

            walktime_ = itemView.findViewById(R.id.tv_result_walktime);
            transfer_ = itemView.findViewById(R.id.tv_result_transfer);
            distance_ = itemView.findViewById(R.id.tv_result_distance);

            transType = itemView.findViewById(R.id.tv_result_transport);
            interTime = itemView.findViewById(R.id.tv_result_transporttime);

            starButton1 = (ImageView) itemView.findViewById(R.id.save_img);
            starButton2 = (ImageView) itemView.findViewById(R.id.save_img2);
        }

        void onBind(ResultItem item){
            System.out.println("bind"+mResultList);
            time.setText(item.getTime());
            st.setText(item.getSt());
            dt.setText(item.getDt());
            path.setText(item.getPath());
            price.setText(item.getPrice());

            if (item.getTransType().isEmpty()) {
                walktime.setText(item.getWalkTime());
                transfer.setText(item.getTransfer());
                distance.setText(item.getDistance());
                walktime.setVisibility(View.VISIBLE);
                walktime_.setVisibility(View.VISIBLE);
                transfer.setVisibility(View.VISIBLE);
                transfer_.setVisibility(View.VISIBLE);
                distance.setVisibility(View.VISIBLE);
                distance_.setVisibility(View.VISIBLE);

            } else {
                transType.setText(item.getTransType());
                interTime.setText(item.getInterTime());

                transType.setVisibility(View.VISIBLE);
                interTime.setVisibility(View.VISIBLE);

            }
        }

    }


}
