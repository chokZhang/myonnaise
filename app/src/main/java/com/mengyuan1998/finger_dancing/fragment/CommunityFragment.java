package com.mengyuan1998.finger_dancing.fragment;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


import com.mengyuan1998.finger_dancing.R;
import com.mengyuan1998.finger_dancing.Utilities.JsonUtils;
import com.mengyuan1998.finger_dancing.Utilities.info_rv_adapter_item.BaseItem;
import com.mengyuan1998.finger_dancing.Utilities.info_rv_adapter_item.VedioItem;
import com.mengyuan1998.finger_dancing.adpter.InfoRVAdapter;

import java.util.ArrayList;
import java.util.List;


import java.util.logging.LogRecord;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class CommunityFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "CommunityFragment";

    private SwipeRefreshLayout swiper;
    private RecyclerView mRecyclerView;
    private InfoRVAdapter mAdapter;
    private List<BaseItem> items = new ArrayList<>();
    private static CommunityFragment fragment = new CommunityFragment();
    private boolean freshed = false;
    String jsonTemp = "[{\"create_time\": \"2018-11-30 21:46:13\", \"info\": \"123\", \"thumbs\": 0, \"video_link\": \"/content/1/video/4d3e3ce4-f4a6-11e8-bcd3-00163e2ef950\", \"id\": 9, \"img_link\": \"/content/1/img/4d3e3ce5-f4a6-11e8-bcd3-00163e2ef950\"}, {\"create_time\": \"2018-11-30 21:46:17\", \"info\": \"123\", \"thumbs\": 0, \"video_link\": \"/content/1/video/4f974486-f4a6-11e8-bcd3-00163e2ef950\", \"id\": 10, \"img_link\": \"/content/1/img/4f974487-f4a6-11e8-bcd3-00163e2ef950\"}, {\"create_time\": \"2018-11-30 14:01:52\", \"info\": null, \"thumbs\": 0, \"video_link\": null, \"id\": 11, \"img_link\": null}, {\"create_time\": \"2018-12-02 18:50:51\", \"info\": \"214\", \"thumbs\": 0, \"video_link\": \"\", \"id\": 12, \"img_link\": \"\"}]";


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
        //初始化SwipeRefreshLayout
        swiper = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        //为SwipeRefreshLayout设置监听事件
        swiper.setOnRefreshListener(this);
        //为SwipeRefreshLayout设置刷新时的颜色变化，最多可以设置4种
        swiper.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //设置LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        //InitItems();
        //设置Adapter
        mAdapter = new InfoRVAdapter(context, items);
        mRecyclerView.setAdapter(mAdapter);
        onRefresh();


    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onRefresh() {

        if(freshed){
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
                    /*List<BaseItem> items = JsonUtils.parseJSONWithJSONObject(jsonTemp);
                    items.addAll(mAdapter.getmList());
                    mAdapter.setmList(items);
                    mAdapter.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            swiper.setRefreshing(false);
                        }
                    });*/

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
        }.start();
    }

    void InitItems(){
        BaseItem item = new BaseItem();
        item.setPraise_num(10);
        item.setType(0);
        item.setUser_name("Test");
        item.setUrl("http://jzvd.nathen.cn/342a5f7ef6124a4a8faf00e738b8bee4/cf6d9db0bd4d41f59d09ea0a81e918fd-5287d2089db37e62345123a1be272f8b.mp4");
        items.add(item);
        BaseItem item2 = new BaseItem();
        item2.setPraise_num(12);
        item2.setType(0);
        item2.setUser_name("MyTest");
        item2.setUrl("https://media.w3.org/2010/05/sintel/trailer.mp4");
        items.add(item2);
        BaseItem item3 = new BaseItem();
        item3.setPraise_num(12);
        item3.setType(0);
        item3.setUser_name("MyTest");
        item3.setUrl("http://sign-resource.oss-cn-beijing.aliyuncs.com/%E5%A4%A7%E5%B8%85%E5%BA%9C-%E6%96%B9%E5%BD%A2%E8%A3%81%E5%88%87.mp4");
        items.add(item3);

    }

    public void addHeader(){
        mAdapter.addHeader();
    }

    public void update(int position, BaseItem item){
        mAdapter.update(position, item);
    }

    public void scrollToTop(){
        mRecyclerView.smoothScrollToPosition(0);
    }


}
