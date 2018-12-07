package com.mengyuan1998.finger_dancing.ui;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import cn.jzvd.JZVideoPlayer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.mengyuan1998.finger_dancing.R;
import com.mengyuan1998.finger_dancing.Utilities.GetImgUtils;
import com.mengyuan1998.finger_dancing.Utilities.HttpUtil;
import com.mengyuan1998.finger_dancing.Utilities.JsonUtils;
import com.mengyuan1998.finger_dancing.Utilities.info_rv_adapter_item.BaseItem;
import com.mengyuan1998.finger_dancing.fragment.CommunityFragment;
import com.mengyuan1998.finger_dancing.fragment.MessageFragment;
import com.mengyuan1998.finger_dancing.fragment.PublishFragmrnt;
import com.mengyuan1998.finger_dancing.fragment.SearchFragment;
import com.mengyuan1998.finger_dancing.fragment.UserFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InfoActivity extends AppCompatActivity {

    private static final String TAG = "InfoActivity";
    private File outputVedio;
    public static final int TAKE_VEDIO = 1;
    public static final int GET_VEDIO = 2;
    private final String uploadUrl = "http://39.96.24.179/upload";
    private final String loginUrl = "http://39.96.24.179/login";
    private Uri vedioUri;
    private CommunityFragment communityFragment = CommunityFragment.getInstance();
    private PublishFragmrnt publishFragmrnt = PublishFragmrnt.getInstance();
    //监听clearCheck()方法是否被调用
    private boolean fromClear;


    List<Integer> radioButtons = Arrays.asList(R.id.rb_community,
            R.id.rb_publish,
            R.id.rb_message,
            R.id.rb_user);

    List<Integer> imgs = Arrays.asList(R.drawable.bottom_home_selector,
            R.drawable.bottom_publish_selector,
            R.drawable.bottom_message_selector,
            R.drawable.bottom_user_selector);

    public static RadioGroup mRgBottomMenu;
    //数组 存储Fragment
    Fragment[] mFragments;
    //当前Fragent的下标
    private  int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //初始化Fresco（圆形头像需要）
        Fresco.initialize(this);
        setContentView(R.layout.activity_info);

        mRgBottomMenu = findViewById(R.id.rg_bottom_menu);

        publishFragmrnt.setInterface(new PublishFragmrnt.Fragment1CallBack() {
            @Override
            public void onPostDone() {
                fromClear = true;
                mRgBottomMenu.clearCheck();

                mRgBottomMenu.check(R.id.rb_community);

                communityFragment.scrollToTop();

            }
        });

        InitImgSize();

        //将Fragment加入数组
        mFragments = new Fragment[] {
                //主页、新闻、图片、视频、个人
                communityFragment,
                publishFragmrnt,
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
                        Log.d(TAG, "onCheckedChanged: get i n 0");
                        setIndexSelected(0);
                        break;
                    case R.id.rb_publish:{
                        if(fromClear){
                            fromClear = false;
                            return;
                        }
                        Log.d(TAG, "onCheckedChanged: get in");
                        JZVideoPlayer.releaseAllVideos();

                        //存储视频的文件对象
                        login();
                        outputVedio = new File(getExternalCacheDir() + "/vedios", "output_vedio.mp4");
                        try{
                            if(outputVedio.exists()){
                                outputVedio.delete();
                            }
                            if(!outputVedio.getParentFile().exists()){
                                outputVedio.getParentFile().mkdir();
                            }
                            outputVedio.createNewFile();
                        }catch (Exception e){
                            Log.e(TAG, "onClick: err happend in takeVedio");
                            e.printStackTrace();
                        }
                        if(Build.VERSION.SDK_INT >= 24){

                            Log.d(TAG, "onCheckedChanged: >= 24");

                            vedioUri = FileProvider.getUriForFile(InfoActivity.this, "com.mengyuan1998.finger_dancing.provider", outputVedio);
                        }
                        else{
                            Log.d(TAG, "onCheckedChanged: < 24");
                            vedioUri = Uri.fromFile(outputVedio);
                        }
                        Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, vedioUri);
                        startActivityForResult(intent, TAKE_VEDIO);


                        break;
                    }

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

    void InitImgSize(){
        RadioButton radioButton = null;
        Drawable drawable = null;
        for(int i = 0; i < radioButtons.size(); i++){
            radioButton = findViewById(radioButtons.get(i));
            drawable = getResources().getDrawable(imgs.get(i));
            drawable.setBounds(0, 0, 80, 80);

            radioButton.setCompoundDrawables(null, drawable, null, null);
        }
    }


    //设置Fragment页面
    public void setIndexSelected(int index) {

        if (currentIndex == index) {
            return;
        }
        Log.d(TAG, "setIndexSelected: get in");
        JZVideoPlayer.releaseAllVideos();
        //开启事务
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //隐藏当前Fragment
        if(mFragments[currentIndex].isAdded())
        ft.hide(mFragments[currentIndex]);
        //判断Fragment是否已经添加
        if (!mFragments[index].isAdded()) {
            Log.d(TAG, "setIndexSelected: add");
            ft.add(R.id.main_content,mFragments[index]).show(mFragments[index]);
        }else {
            //显示新的Fragment
            ft.show(mFragments[index]);
        }
        ft.commitAllowingStateLoss();
        currentIndex = index;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case TAKE_VEDIO:{
                if(resultCode == RESULT_OK){
                    /*new Thread(new Runnable() {
                        @Override
                        public void run() {
                            postFile(outputVedio);
                        }
                    }).run();
                    communityFragment.addHeader();*/
                }
                setIndexSelected(1);
                //break;
            }
            case GET_VEDIO:{

                /*Uri uri = data.getData();
                String path = FileUtils.getFilePathByUri(getApplicationContext(),uri);
                if(null == path){
                    Log.d(TAG, "onActivityResult: path is null!!");
                    break;
                }
                File file = new File(path);
                if (!file.exists()) {
                    Log.d(TAG, "onActivityResult: file doesnt exist");
                    break;
                }
                *//*HttpUtil.postFile(uploadUrl, new HttpUtil.ProgressListener() {
                    @Override
                    public void onProgress(long currentBytes, long contentLength, boolean done) {
                        Log.i(TAG, "currentBytes==" + currentBytes + "==contentLength==" + contentLength + "==done==" + done);


                    }
                }, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response != null) {
                            String result = response.body().string();
                            Log.i(TAG, "result===" + result);
                        }
                    }
                }, outputVedio);*//*
                postFile(file);
                break;*/
            }
            default: break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    protected void onSaveInstanceState(Bundle outState) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for(int i = 1; i < mFragments.length; i++){
            transaction.remove(mFragments[i]);
        }
        transaction.commitAllowingStateLoss();
        Log.d(TAG, "onSaveInstanceState: run");
        super.onSaveInstanceState(outState);
    }

    public void postFile(File file){



        File img = new File(getExternalCacheDir() + "/vedios", "img.jpg");

        File[] files = new File[2];
        files[0] = file;
        files[1] = img;

        HttpUtil.postFile(uploadUrl, "", new HttpUtil.ProgressListener() {
            @Override
            public void onProgress(long currentBytes, long contentLength, boolean done) {
                Log.i(TAG, "currentBytes==" + currentBytes + "==contentLength==" + contentLength + "==done==" + done);


            }
        }, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    String result = response.body().string();
                    Log.i(TAG, "result===" + result);
                    if(response.code() != 200){
                        Toast.makeText(getApplicationContext(), "网络状态错误", Toast.LENGTH_SHORT).show();
                    }
                    try{
                        BaseItem baseItem = JsonUtils.parseJSON(result);
                        //只有一个元素
                        if(null != baseItem){
                            communityFragment.update(0, baseItem);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }, files);
    }

    public void login(){
        HttpUtil.login(loginUrl, "superuser", "123456" , new HttpUtil.ProgressListener() {
            @Override
            public void onProgress(long currentBytes, long contentLength, boolean done) {
                Log.i(TAG, "currentBytes==" + currentBytes + "==contentLength==" + contentLength + "==done==" + done);


            }
        },new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.toString());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    String result = response.body().string();
                    Log.i(TAG, "result===" + result);
                    Log.d(TAG, "onResponse: code " + response.code());
                    //Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                    //startActivity(intent);
                }
            }
        });
    }


}
