package com.mengyuan1998.finger_dancing.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mengyuan1998.finger_dancing.R;
import com.mengyuan1998.finger_dancing.Utilities.info_rv_adapter_item.BaseItem;
import com.mengyuan1998.finger_dancing.adpter.InfoRVAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UserFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private InfoRVAdapter mAdapter;
    private List<BaseItem> items = new ArrayList<>();
    private static UserFragment instance = new UserFragment();

    public static UserFragment getInstance(){
        return instance;
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_user;
    }

    @Override
    protected void initViews(Context context) {
        mRecyclerView = findViewById(R.id.community_fragment_rv);
        //设置LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        InitImgSize();
        InitItems();
        //设置Adapter
        InfoRVAdapter adapter = new InfoRVAdapter(getActivity(), items);
        //声明是userFragment在使用adapter
        adapter.setFragmentIndex(3);
        mRecyclerView.setAdapter(adapter);

    }

    void InitImgSize(){
        TextView nickname=(TextView) mRootView.findViewById(R.id.NickName);
        SimpleDraweeView headUrl=(SimpleDraweeView) mRootView.findViewById(R.id.head_img);
        String name=getArguments().get("name")+"";
        String head=getArguments().get("head")+"";
        nickname.setText(name);
        headUrl.setImageURI(head);
        TextView contactUs = findViewById(R.id.contact_us);
        Drawable contactUs_drawable = getResources().getDrawable(R.drawable.contact);
        contactUs_drawable.setBounds(0, 0, 60, 60);
        //放上面
        contactUs.setCompoundDrawables(null, contactUs_drawable, null, null);

        TextView editPage = findViewById(R.id.edit_page);
        Drawable editPage_drawable = getResources().getDrawable(R.drawable.edit_page);
        editPage_drawable.setBounds(0, 0, 60, 60);
        //放上面
        editPage.setCompoundDrawables(null, editPage_drawable, null, null);
    }

    void InitItems(){
        BaseItem item = new BaseItem();
        item.setPraise_num(10);
        item.setType(0);
        item.setUser_name("Test");
        item.setUrl("http://www.jmzsjy.com/UploadFile/微课/地方风味小吃——宫廷香酥牛肉饼.mp4");
        items.add(item);
        BaseItem item2 = new BaseItem();
        item2.setPraise_num(12);
        item2.setType(0);
        item2.setUser_name("MyTest");
        item2.setUrl("https://media.w3.org/2010/05/sintel/trailer.mp4");
        items.add(item2);

    }

}
