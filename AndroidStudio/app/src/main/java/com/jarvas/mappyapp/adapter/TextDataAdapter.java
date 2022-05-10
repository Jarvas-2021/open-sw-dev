package com.jarvas.mappyapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jarvas.mappyapp.R;
import com.jarvas.mappyapp.models.TextDataItem;
import com.jarvas.mappyapp.utils.Code;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TextDataAdapter extends RecyclerView.Adapter<TextDataAdapter.ViewHolder> {
    private ArrayList<TextDataItem> mTextList;
    Integer viewType = 1;
    String text = "";

    public TextDataAdapter(ArrayList<TextDataItem> mTextDataItems) {
        mTextList = mTextDataItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvChat;
        public ViewHolder(View view) {
            super(view);
            tvChat = (TextView) view.findViewById(R.id.tvChat);

        }
        public TextView getTextView() {
            return tvChat;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mTextList.get(position).getViewType().equals(viewType)) {
            return 1; // 사용자 메시지
        } else {
            return 2;   // 매피 메시지
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()) // 상대방 메시지이면
                .inflate(R.layout.item_list_left, parent, false); // 말풍선이 왼쪽에서 나타나는 xml 파일 인플레이션

        if(viewType == 1) { // 사용자 메시지이면
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_right, parent, false); // 말풍선이 오른쪽에서 나타나는 xml 파일 인플레이션
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextView().setText(mTextList.get(position).getTextData());
        text = mTextList.get(position).getTextData();
    }

    public String getTextItem() {
        return text;
    }


    public void setFriendList(ArrayList<TextDataItem> list) {
        this.mTextList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mTextList.size();
    }


}
