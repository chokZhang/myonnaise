package com.mengyuan1998.finger_dancing.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.mengyuan1998.finger_dancing.R;
import com.mengyuan1998.finger_dancing.Utilities.HttpUtil;
import com.mengyuan1998.finger_dancing.Utilities.JsonUtils;
import com.mengyuan1998.finger_dancing.Utilities.info_rv_adapter_item.BaseItem;
import com.mengyuan1998.finger_dancing.Utilities.info_rv_adapter_item.VedioItem;
import com.mengyuan1998.finger_dancing.adpter.InfoRVAdapter;
import com.mengyuan1998.finger_dancing.my_view.LoadMoreFooterView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import java.util.logging.LogRecord;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class CommunityFragment extends BaseFragment {

    private static final String TAG = "CommunityFragment";
    private IRecyclerView mRecyclerView;
    private InfoRVAdapter mAdapter;
    private ImageView return_button;
    private List<BaseItem> items = new ArrayList<>();
    private static CommunityFragment fragment = new CommunityFragment();
    private boolean freshed = false;
    LoadMoreFooterView loadMoreFooterView;
    String mAction = "";
    Activity mActivity;
    //最顶部的视频编号
    String upFreshFlag = "";
    //最底部的
    String downFreshFlag = "";

    public static CommunityFragment getInstance(){
        return fragment;
    }

    @Override
    protected int attachLayoutRes() {


        return R.layout.fragment_community;
    }

    @Override
    protected void initViews(Context context) {
        mRecyclerView = findViewById(R.id.community_fragment_rv);
        return_button = findViewById(R.id.community_fragment_return);

        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        /*//初始化SwipeRefreshLayout
        swiper = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        //为SwipeRefreshLayout设置监听事件
        swiper.setOnRefreshListener(this);
        //为SwipeRefreshLayout设置刷新时的颜色变化，最多可以设置4种
        swiper.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);*/

        //设置LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        InitItems();
        //设置Adapter

        loadMoreFooterView = (LoadMoreFooterView) mRecyclerView.getLoadMoreFooterView();

        mAdapter = new InfoRVAdapter(getActivity(), items);
        mRecyclerView.setIAdapter(mAdapter);
        mRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh: ");
                loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                upFresh();
            }
        });
        mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.d(TAG, "onLoadMore: ");
                if (loadMoreFooterView.canLoadMore() && mAdapter.getmList().size() > 0) {

                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
                    downFresh();
                }
            }
        });
        //onRefresh();


    }

    @Override
    public void onStart(){
        super.onStart();
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

    }

    /*@Override
    public void onRefresh() {

        *//*if(freshed){
            mAdapter.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "没有更多内容了", Toast.LENGTH_LONG).show();
                    swiper.setRefreshing(false);
                }
            });
            return;
        }

        Log.d(TAG, "onRefresh: get in");
        new Thread(){
            @Override
            public void run(){
                try{
                    *//**//*List<BaseItem> items = JsonUtils.parseJSONWithJSONObject(jsonTemp);
                    items.addAll(mAdapter.getmList());
                    mAdapter.setmList(items);
                    mAdapter.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            swiper.setRefreshing(false);
                        }
                    });*//**//*

                    mAdapter.getmList().add(new VedioItem());
                    mAdapter.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            swiper.setRefreshing(false);
                        }
                    });

                    freshed = true;

                }catch (Exception e){
                    e.printStackTrace();
                    Log.d(TAG, "run: on err");
                }
            }
        }.start();*//*
        upFresh();
    }
*/
    void InitItems(){
        BaseItem item9 = new BaseItem();
        item9.setPraise_num(12);
        item9.setType(0);
        item9.setUser_name("大帅府");
        item9.setUrl("http://sign-resource.oss-cn-beijing.aliyuncs.com/%E5%A4%A7%E5%B8%85%E5%BA%9C2%20%283%29.mp4");
        items.add(item9);
        BaseItem item = new BaseItem();
        item.setPraise_num(10);
        item.setType(0);
        item.setUser_name("大连金石滩");
        item.setUrl("http://sign-resource.oss-cn-beijing.aliyuncs.com/intro_videos/2%E4%B8%80%E5%A4%A7%E8%BF%9E%E9%87%91%E7%9F%B3%E6%BB%A9.mp4");
        items.add(item);
        BaseItem item2 = new BaseItem();
        item2.setPraise_num(12);
        item2.setType(0);
        item2.setUser_name("本溪水洞");
        item2.setUrl("http://sign-resource.oss-cn-beijing.aliyuncs.com/intro_videos/2%E4%B8%89%E6%9C%AC%E6%BA%AA%E6%B0%B4%E6%B4%9E.mp4");
        items.add(item2);
        BaseItem item3 = new BaseItem();
        item3.setPraise_num(12);
        item3.setType(0);
        item3.setUser_name("大连老虎滩");
        item3.setUrl("http://sign-resource.oss-cn-beijing.aliyuncs.com/intro_videos/2%E4%BA%8C%E5%A4%A7%E8%BF%9E%E8%80%81%E8%99%8E%E6%BB%A9.mp4");
        items.add(item3);
        BaseItem item4 = new BaseItem();
        item4.setPraise_num(12);
        item4.setType(0);
        item4.setUser_name("植物园");
        item4.setUrl("http://sign-resource.oss-cn-beijing.aliyuncs.com/intro_videos/3%E4%B8%89%E6%A4%8D%E7%89%A9%E5%9B%AD.mp4");
        items.add(item4);
        BaseItem item5 = new BaseItem();
        item5.setPraise_num(12);
        item5.setType(0);
        item5.setUser_name("故宫");
        item5.setUrl("http://sign-resource.oss-cn-beijing.aliyuncs.com/intro_videos/3%E4%BA%8C%E6%95%85%E5%AE%AB.mp4");
        items.add(item5);
        BaseItem item6 = new BaseItem();
        item6.setPraise_num(12);
        item6.setType(0);
        item6.setUser_name("四千山");
        item6.setUrl("http://sign-resource.oss-cn-beijing.aliyuncs.com/intro_videos/3%E5%9B%9B%E5%8D%83%E5%B1%B1.mp4");
        items.add(item6);
        BaseItem item7 = new BaseItem();
        item7.setPraise_num(12);
        item7.setType(0);
        item7.setUser_name("鸭绿江断桥");
        item7.setUrl("http://sign-resource.oss-cn-beijing.aliyuncs.com/intro_videos/4%E4%B8%80%E9%B8%AD%E7%BB%BF%E6%B1%9F%E6%96%AD%E6%A1%A5.mp4");
        items.add(item7);
        BaseItem item8 = new BaseItem();
        item8.setPraise_num(12);
        item8.setType(0);
        item8.setUser_name("锦州笔架山");
        item8.setUrl("http://sign-resource.oss-cn-beijing.aliyuncs.com/intro_videos/4%E4%B8%89%E9%94%A6%E5%B7%9E%E7%AC%94%E6%9E%B6%E5%B1%B1.mp4");
        items.add(item8);
        BaseItem item10 = new BaseItem();
        item10.setPraise_num(12);
        item10.setType(0);
        item10.setUser_name("盘锦红海滩");
        item10.setUrl("http://sign-resource.oss-cn-beijing.aliyuncs.com/intro_videos/4%E4%BA%8C%E7%9B%98%E9%94%A6%E7%BA%A2%E6%B5%B7%E6%BB%A9.mp4");
        items.add(item10);


    }

    public void upFresh(){
        String url = "http://39.96.90.194:8888/fresh?mAction=upFreshInCommunity&flag=" + upFreshFlag;
        mAction = "upFreshInCommunity";
        HttpUtil.onFresh(url, mAction, freshCallBack);

    }

    public void downFresh(){
        String url = "http://39.96.90.194:8888/fresh?mAction=downFreshInCommunity&flag=" + downFreshFlag;
        mAction = "downFreshInCommunity";
        for(int i = 1; i <= 100000; i++){

        }
        HttpUtil.onFresh(url, mAction, freshCallBack);
    }

  @Override
    public void onPause(){
        super.onPause();
        mAdapter.realease();
    }

    public void addHeader(){
        mAdapter.addHeader();
    }

    //删除头部
    public void releaseHeader(){
        mAdapter.releaseHeader();
    }

    //更新
    public void update(int position, BaseItem item){
        Log.d(TAG, "update: ");
        mAdapter.update(position, item);
    }

    //提醒有新数据插入
    public void insert(int flag, List<BaseItem> items){
        Log.d(TAG, "insert: insert");
        if(flag == 0){
            mAdapter.insertToTop(items);
        }
        else{
            mAdapter.insertToBottom(items);
        }

    }


    Callback freshCallBack = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d(TAG, "onFailure: err");
            if(getActivity() != null){
                HttpUtil.showStateWrong(getActivity(), "状态错误");
            }

            onError();
            e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Log.d(TAG, "onResponse: get in");
            if (response != null) {
                String result = response.body().string();
                Log.i(TAG, "result===" + result);
                if(response.code() != 200){
                    //线程中调用
                    Log.d(TAG, "onResponse: code:" + response.code());
                    if(getActivity() != null){
                        HttpUtil.showStateWrong(getActivity(), "网络不可用");
                    }
                    onError();
                }
                else{
                    try{
                        List<BaseItem> items = JsonUtils.parseJSONWithJSONObject(result);
                        if(items.size() > 0){
                            if (mAction == "upFreshInCommunity") {
                                upFreshFlag = items.get(0).getId();
                                insert(0, items);
                            } else if (mAction == "downFreshInCommunity") {
                                downFreshFlag = items.get(items.size() - 1).getId();
                                insert(1, items);
                            }
                            onSuccessStop();
                        }
                        else{
                            if(getActivity() != null){
                                HttpUtil.showStateWrong(getActivity(), "没有更多内容了");
                                onNothingFind();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    //刷新或加载错误
    public void onError(){
        Log.d(TAG, "onError: mAction" + mAction);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mAction.compareTo("upFreshInCommunity") == 0){
                    Log.d(TAG, "run: set false");
                    mRecyclerView.setRefreshing(false);
                }else{
                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                }
            }
        });
    }

    //刷新或者加载没有更多内容
    public void onNothingFind(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mAction.compareTo("upFreshInCommunity") == 0){
                    mRecyclerView.setRefreshing(false);
                }else{
                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                }
            }
        });
    }

    //刷新停止
    public void onSuccessStop(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mAction.compareTo("upFreshInCommunity") == 0){
                    mRecyclerView.setRefreshing(false);
                }else{
                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                }
            }
        });
    }

    public void scrollToTop(){
        mRecyclerView.smoothScrollToPosition(0);
    }


}
