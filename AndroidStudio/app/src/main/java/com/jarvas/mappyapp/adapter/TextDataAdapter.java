package com.jarvas.mappyapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.models.TextDataItem;

import java.util.ArrayList;

public class TextDataAdapter extends RecyclerView.Adapter<TextDataAdapter.ViewHolder> {
    private ArrayList<TextDataItem> mTextList;

    @NonNull
    @Override
    public TextDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextDataAdapter.ViewHolder holder, int position) {
        holder.onBind(mTextList.get(position));
    }

    public void setFriendList(ArrayList<TextDataItem> list){
        this.mTextList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mTextList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textData = (TextView) itemView.findViewById(R.id.text_data);
        }

        void onBind(TextDataItem item){
            textData.setText(item.getTextData());
        }
    }
}
