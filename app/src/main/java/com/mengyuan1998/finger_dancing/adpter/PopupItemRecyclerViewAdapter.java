package com.mengyuan1998.finger_dancing.adpter;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.mengyuan1998.finger_dancing.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PopupItemRecyclerViewAdapter extends RecyclerView.Adapter <PopupItemRecyclerViewAdapter.StringListItemViewHolder>{

    List<String> items;


    private OnItemClickListener mListener = null;

    public PopupItemRecyclerViewAdapter(){
        items = new ArrayList<>();
    }

    /*public static PopupItemRecyclerViewAdapter getPopupItemRecyclerViewAdapter(){
        if(instance == null){
            instance = new PopupItemRecyclerViewAdapter();
        }
        return instance;
    }*/

    public void setItemList(List<String> itemList){

        this.items.clear();
        this.items.addAll(itemList);
        notifyDataSetChanged();
    }

    public void clearItems(){
        items.clear();
    }

    public void setItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public StringListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.popup_item, parent, false);
        return new StringListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StringListItemViewHolder holder, int position) {
        final String nowContent = items.get(position);
        holder.content.setText(nowContent);
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null)
                    mListener.onItemticClick(view, nowContent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class StringListItemViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout item_body;
        public TextView content;

        public StringListItemViewHolder(View item_view){
            super(item_view);
            item_body = (LinearLayout) item_view;
            content = item_view.findViewById(R.id.popup_item_content);
        }
    }

    public static interface OnItemClickListener{
        void onItemticClick(View view, String content);
        //void onItemLongClick(View view);
    }
}
