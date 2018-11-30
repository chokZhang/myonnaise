package com.mengyuan1998.finger_dancing.Utilities.auto_complete;

import android.content.Context;

import android.util.Log;
import android.view.View;


import com.mengyuan1998.finger_dancing.adpter.PopupItemRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


public class SimpleRecyclerViewPresenter extends RecyclerViewPresenter<String> {

    private static final String TAG = "SimpleRecyclerViewPrese";
    PopupItemRecyclerViewAdapter instance ;

    public SimpleRecyclerViewPresenter(Context context){
        super(context);
    }

    @Override
    protected RecyclerView.Adapter instantiateAdapter() {
        instance = new PopupItemRecyclerViewAdapter();
        instance.setItemClickListener(new PopupItemRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemticClick(View view, String content) {
                dispatchClick(content);
            }
        });

        return instance;
    }

    @Override
    protected void onQuery(@Nullable CharSequence query) {
        Log.d(TAG, "onQuery: query: " + query.toString());
        String queryContent = query.toString();
        String[] strs = queryContent.split(" ");
        //查询词的列表
        List<String> queryList = new ArrayList<>();
        for(String str : strs){
            queryList.add(str);
        }
        List<String> items = SentenceAutoCompleter.getInstance().executeValueQuery(queryList, false);
        /* 补全内容列表样例
        List<String> items = new ArrayList<>();
        items.add("aaa");
        items.add("bbb");
        items.add("ccc");*/
        Log.d(TAG, "onQuery: print items");
        for(String item : items){
            Log.d(TAG, "onQuery: item : " + item);
        }
        instance.setItemList(items);
    }
}
