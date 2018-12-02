package com.mengyuan1998.finger_dancing.fragment;

import android.content.Context;


import com.mengyuan1998.finger_dancing.R;
import com.mengyuan1998.finger_dancing.Utilities.info_rv_adapter_item.BaseItem;
import com.mengyuan1998.finger_dancing.adpter.InfoRVAdapter;

import java.util.ArrayList;
import java.util.List;


import java.util.logging.LogRecord;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CommunityFragment extends BaseFragment {

    private static final String TAG = "CommunityFragment";

    private RecyclerView mRecyclerView;
    private InfoRVAdapter mAdapter;
    private List<BaseItem> items = new ArrayList<>();


    @Override
    protected int attachLayoutRes() {


        return R.layout.fragment_community;
    }

    @Override
    protected void initViews(Context context) {
        mRecyclerView = findViewById(R.id.community_fragment_rv);
        //设置LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        InitItems();
        //设置Adapter
        InfoRVAdapter adapter = new InfoRVAdapter(context, items);
        mRecyclerView.setAdapter(adapter);



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


}
