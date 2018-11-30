package com.mengyuan1998.finger_dancing.ui;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.RadioGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.mengyuan1998.finger_dancing.R;
import com.mengyuan1998.finger_dancing.fragment.CommunityFragment;
import com.mengyuan1998.finger_dancing.fragment.MessageFragment;
import com.mengyuan1998.finger_dancing.fragment.SearchFragment;
import com.mengyuan1998.finger_dancing.fragment.UserFragment;

public class InfoActivity extends AppCompatActivity {

    private static final String TAG = "InfoActivity";

    RadioGroup mRgBottomMenu;
    //数组 存储Fragment
    Fragment[] mFragments;
    //当前Fragent的下标
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //初始化Fresco（圆形头像需要）
        Fresco.initialize(this);
        setContentView(R.layout.activity_info);

        mRgBottomMenu = findViewById(R.id.rg_bottom_menu);

        //将Fragment加入数组
        mFragments = new Fragment[] {
                //主页、新闻、图片、视频、个人
                new CommunityFragment(),
                new SearchFragment(),
                new MessageFragment(),
                new UserFragment()
        };
        //开启事务
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //设置为默认界面 MainHomeFragment
        ft.add(R.id.main_content,mFragments[0]).commit();
        //RadioGroup选中事件监听 改变fragment
        mRgBottomMenu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_community:
                        setIndexSelected(0);
                        break;
                    case R.id.rb_search:
                        setIndexSelected(1);
                        break;
                    case R.id.rb_message:
                        setIndexSelected(2);
                        break;
                    case R.id.rb_user:
                        setIndexSelected(3);
                        break;

                }
            }
        });
    }



    //设置Fragment页面
    private void setIndexSelected(int index) {
        if (currentIndex == index) {
            return;
        }
        //开启事务
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //隐藏当前Fragment
        ft.hide(mFragments[currentIndex]);
        //判断Fragment是否已经添加
        if (!mFragments[index].isAdded()) {
            ft.add(R.id.main_content,mFragments[index]).show(mFragments[index]);
        }else {
            //显示新的Fragment
            ft.show(mFragments[index]);
        }
        ft.commit();
        currentIndex = index;
    }
}
