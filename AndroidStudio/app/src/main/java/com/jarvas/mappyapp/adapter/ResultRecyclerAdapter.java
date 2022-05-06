package com.jarvas.mappyapp.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.activities.MainActivity;
import com.jarvas.mappyapp.activities.PolyLineActivity;
import com.jarvas.mappyapp.alarm.AlertReceiver;
import com.jarvas.mappyapp.models.ResultItem;
import com.jarvas.mappyapp.models.Star;
import com.jarvas.mappyapp.models.database.StarDatabase;
import com.jarvas.mappyapp.utils.ContextStorage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ResultRecyclerAdapter extends RecyclerView.Adapter<ResultRecyclerAdapter.ViewHolder> {
    private ArrayList<ResultItem> mResultList ;
    private StarDatabase database;

    private String startAddressText;
    private String destinationAddressText;

    public ResultRecyclerAdapter(String startAddressText, String destinationAddressText) {
        this.startAddressText = startAddressText;
        this.destinationAddressText = destinationAddressText;
    }

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

        holder.path.setOnClickListener(v -> {
            Intent intent = new Intent(ContextStorage.getCtx(), PolyLineActivity.class);
            intent.putExtra("startAddressText",startAddressText);
            intent.putExtra("destinationAddressText",destinationAddressText);
            ContextStorage.getCtx().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        });

        holder.notificationButton1.setOnClickListener(v -> {
            holder.notificationButton1.setVisibility(View.INVISIBLE);
            holder.notificationButton2.setVisibility(View.VISIBLE);

            updateTimeText();
            startAlarm(holder.st.getText().toString());

        });

        holder.notificationButton2.setOnClickListener(v -> {
            holder.notificationButton2.setVisibility(View.INVISIBLE);
            holder.notificationButton1.setVisibility(View.VISIBLE);

            cancelAlarm();
        });

    }

    private void updateTimeText() {
        String timeText = "출발 5분전에 알려드릴게요 :>";
        Toast.makeText(ContextStorage.getCtx(), timeText, Toast.LENGTH_SHORT).show();
    }

    private void startAlarm(String st) {
        AlarmManager alarmManager = (AlarmManager) ContextStorage.getCtx().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ContextStorage.getCtx(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ContextStorage.getCtx(),1,intent,0);

        long d = convertDateFormatToLong(st);
        System.out.println("d"+d);
        System.out.println("current"+ SystemClock.elapsedRealtime());
        //todo - 즉시 실행됨, 고쳐야함
        alarmManager.set(AlarmManager.RTC_WAKEUP,SystemClock.elapsedRealtime()+180*1000,pendingIntent);

    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) ContextStorage.getCtx().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ContextStorage.getCtx(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ContextStorage.getCtx(),1,intent,0);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(ContextStorage.getCtx(), "알람 취소 !", Toast.LENGTH_SHORT).show();
    }

    private long convertDateFormatToLong(String data) {
        System.out.println("시험"+data);
        String tmp1 = data.substring(data.indexOf(":")+1);
        //5분전 알림
        Integer tmp2 = Integer.parseInt(tmp1)-5;
        String tmp3="";

        if (tmp2<10) {
            tmp3 = 0+tmp2.toString();
        }
        else {
            tmp3 = tmp2.toString();
        }

        System.out.println("시험tmp1"+tmp1);
        System.out.println("시험tmp2"+tmp2);
        System.out.println(tmp3);

        String Ndata = data.replace(tmp1,"") + tmp3;
        System.out.println("시험2"+data);

        DateFormat df = new SimpleDateFormat("a hh:mm", Locale.KOREAN);
        Date dateData = null;
        Date m =null;
        long time=0;
        long mm=0;
        try {
            dateData = df.parse(Ndata);
            m = df.parse(data);
            mm = m.getTime();
            System.out.println("현재 date"+dateData);
            time = dateData.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("현재 time : " + time);
        System.out.println("이전 time : "+mm);

        return time;
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

        ImageView notificationButton1;
        ImageView notificationButton2;

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

            notificationButton1 = itemView.findViewById(R.id.notification_img);
            notificationButton2 = itemView.findViewById(R.id.notification_img2);
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
