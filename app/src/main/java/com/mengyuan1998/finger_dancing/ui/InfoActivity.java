package com.mengyuan1998.finger_dancing.ui;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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
import java.util.Calendar;
import java.util.List;

public class InfoActivity extends AppCompatActivity {

    private static final String TAG = "InfoActivity";
    private File outputVedio;
    public String videoAbsPath = null;
    public static final int TAKE_VEDIO = 1;
    public static final int GET_VEDIO = 2;
    private final String uploadUrl = "http://39.96.24.179/upload";
    private final String loginUrl = "http://39.96.90.194:8888/login";
    private Uri vedioUri;
    private CommunityFragment communityFragment = CommunityFragment.getInstance();
    private PublishFragmrnt publishFragmrnt = PublishFragmrnt.getInstance();
    private UserFragment userFragment = UserFragment.getInstance();
    public static String[] thumbColumns = { MediaStore.Video.Thumbnails.DATA };
    public static String[] mediaColumns = { MediaStore.Video.Media._ID };
    //监听clearCheck()方法是否被调用
    private boolean fromClear;

    private final int REQUEST_ALL_PERMISSION = 1;

    private String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.RECORD_AUDIO
    };

    private List<String> mPermissionList = new ArrayList<>();


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
    private int currentIndex;
    //当点击发布时，是否启动相机拍摄
    private boolean shouldStartActivity = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //初始化Fresco（圆形头像需要）
        Fresco.initialize(this);
        setContentView(R.layout.activity_info);

        //微信昵称和头像的传入
        Intent intent=getIntent();
        String name=intent.getStringExtra("name");
        String head=intent.getStringExtra("head");
        Bundle bundle=new Bundle();/*创建Bundle数据包*/
        bundle.putString("name",name);
        bundle.putString("head",head);
        Log.d("blood","name is "+name);
        Log.d("blood","head is "+head);

        mRgBottomMenu = findViewById(R.id.rg_bottom_menu);

        publishFragmrnt.setInterface(new PublishFragmrnt.OnPostListener() {
            @Override
            public void onPostDone() {
                fromClear = true;
                shouldStartActivity = true;
                mRgBottomMenu.clearCheck();

                mRgBottomMenu.check(R.id.rb_community);

                communityFragment.scrollToTop();

            }

            //线程中被调用
            @Override
            public void onFailure() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "上传失败， 网络状态错误", Toast.LENGTH_SHORT).show();

                        //取消communityFragment的header
                        communityFragment.releaseHeader();
                        communityFragment.scrollToTop();
                    }
                });
            }

            @Override
            public void onSuccess() {
                //TODO 上传成功后需要做的事情
            }

            @Override
            public void onCancel() {
                fromClear = true;
                shouldStartActivity = true;
                mRgBottomMenu.clearCheck();

                mRgBottomMenu.check(R.id.rb_community);
            }
        });

        InitImgSize();

        requestPermission();

        //将Fragment加入数组
        mFragments = new Fragment[] {
                //主页、新闻、图片、视频、个人
                communityFragment,
                publishFragmrnt,
                new MessageFragment(),
                userFragment
        };
        //传递微信头像和昵称
        userFragment.setArguments(bundle);
        //开启事务
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //设置为默认界面 MainHomeFragment
        ft.add(R.id.main_content,mFragments[0]).commit();

        
        //RadioGroup选中事件监听 改变fragment
        mRgBottomMenu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                Log.d(TAG, "onCheckedChanged: change");
                
                if(fromClear){
                    fromClear = false;
                    Log.d(TAG, "onCheckedChanged: done");
                    return;
                }
                switch (checkedId) {
                    case R.id.rb_community:
                        Log.d(TAG, "onCheckedChanged: get i n 0");
                        setIndexSelected(0);
                        break;
                    case R.id.rb_publish:{
                        Log.d(TAG, "onCheckedChanged: get in");

                        requestPermission();

                        if(shouldStartActivity){
                            //startCameraActivity();
                            //openAlbum();
                            final AlertDialog centerDialog = new AlertDialog.Builder(InfoActivity.this).create();
                            final View view = getLayoutInflater().inflate(R.layout.dialog_center, null);
                            centerDialog.setView(view);

                            TextView TakeVideo = view.findViewById(R.id.take_video);
                            TextView GetVideo = view.findViewById(R.id.get_video);

                            //拍照
                            TakeVideo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startCameraActivity();
                                    centerDialog.cancel();
                                }
                            });

                            //从相册获取
                            GetVideo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    openAlbum();
                                    centerDialog.cancel();
                                }
                            });

                            centerDialog.show();

                        }else{
                            setIndexSelected(1);
                        }
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

        login();

    }

    public boolean requestPermission(){
        //小于6.0不需要权限动态申请
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }

        for(int i = 0; i < permissions.length; i++){
            if(ContextCompat.checkSelfPermission(InfoActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED){
                mPermissionList.add(permissions[i]);
            }
        }

        if(mPermissionList.size() != 0){
            String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);
            ActivityCompat.requestPermissions(InfoActivity.this, permissions, REQUEST_ALL_PERMISSION);
            return false;
        }
        return true;
    }


    @Override
    public void onResume(){
        super.onResume();

    }

    //设置图标大小
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

        if (currentIndex == index && mFragments[currentIndex].isAdded()) {
            return;
        }

        //开启事务
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //隐藏当前Fragment
        if(mFragments[currentIndex].isAdded())
        ft.hide(mFragments[currentIndex]);
        //判断Fragment是否已经添加
        if (!mFragments[index].isAdded()) {
            Log.d(TAG, "setIndexSelected: add " + index);
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
                    String thumbnailPath = getVideoInfo(data);
                    if(thumbnailPath != null){
                        publishFragmrnt.setVideoThumbnailPath(thumbnailPath);
                    }
                    setIndexSelected(1);
                    shouldStartActivity = false;
                }
                else{
                    fromClear = true;
                    shouldStartActivity = true;
                    mRgBottomMenu.clearCheck();
                    mRgBottomMenu.check(radioButtons.get(currentIndex));

                }
                break;
            }
            case GET_VEDIO:{


                if(resultCode == RESULT_OK){
                    String thumbnailPath = getVideoInfo(data);
                    if(thumbnailPath != null){
                        publishFragmrnt.setVideoThumbnailPath(thumbnailPath);
                    }
                    setIndexSelected(1);
                    shouldStartActivity = false;
                    /*Uri uri = data.getData();
                    Log.d(TAG, "onActivityResult: uri: " + uri.toString());
                    InfoActivity.getThumbnailPathForLocalFile(this, uri);
                    setIndexSelected(1);
                    shouldStartActivity = false;*/
                }
                else{
                    fromClear = true;
                    shouldStartActivity = true;
                    mRgBottomMenu.clearCheck();
                    mRgBottomMenu.check(radioButtons.get(currentIndex));

                }

                break;
            }
            default: break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    protected void onSaveInstanceState(Bundle outState) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for(int i = 0; i < mFragments.length && i != currentIndex; i++){
            transaction.remove(mFragments[i]);
        }
        transaction.commitAllowingStateLoss();
        Log.d(TAG, "onSaveInstanceState: run");
        super.onSaveInstanceState(outState);
    }

    //调用系统相机拍照
    private void startCameraActivity(){
        //存储视频的文件对象
        //String videoName = getNowTime();
        //publishFragmrnt.setVideoName("videoTemp.mp4");
        outputVedio = new File(getExternalCacheDir() + "/vedios", "videoTemp.mp4");
        publishFragmrnt.setVideoAbsPath(outputVedio.getAbsolutePath());
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
    }

    //打开相册
    public void openAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        //intent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*");
        startActivityForResult(intent, GET_VEDIO);
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

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean state = false;
        if (requestCode == REQUEST_ALL_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults.length > 0) {//安全写法，如果小于0，肯定会出错了
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        switch (grantResult){
                            case PackageManager.PERMISSION_GRANTED://同意授权0
                                break;
                            case PackageManager.PERMISSION_DENIED://拒绝授权-1
                                state = true;
                                break;
                        }
                    }
                }
            }
            if(state){
                Toast.makeText(this, "权限不足，可能会出现问题", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*//获取视频缩略图地址
    public static String getThumbnailPathForLocalFile(Activity context,
                                                      Uri fileUri) {

        long fileId = getFileId(context, fileUri);



        Cursor thumbCursor = null;
        try {

            thumbCursor = context.getContentResolver().query(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + " = "
                            + fileId, null, null);

            if (thumbCursor.moveToFirst()) {
                String thumbPath = thumbCursor.getString(thumbCursor
                        .getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                Log.d(TAG, "getThumbnailPathForLocalFile: path: " + thumbPath);
                return thumbPath;
            }
            else{
                Log.d(TAG, "getThumbnailPathForLocalFile: not");
            }

        } finally {
        }

        return null;
    }
    //获取视频id
    public static long getFileId(Activity context, Uri fileUri) {

        Cursor cursor = context.getContentResolver().query(fileUri, mediaColumns, null, null,
                null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int id = cursor.getInt(columnIndex);

            return id;
        }

        return 0;
    }*/

    public String getNowTime(){
        Calendar calendar = Calendar.getInstance();
        return "" + calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH) + "" + calendar.get(Calendar.DAY_OF_MONTH) + "" + calendar.get(Calendar.HOUR_OF_DAY) + "" + calendar.get(Calendar.MINUTE) + "" + calendar.get(Calendar.SECOND)+".mp4";
    }

    //返回视频的缩略图
    public String getVideoInfo(Intent data){
        Uri uri = data.getData();
        ContentResolver cr = this.getContentResolver();
        /** 数据库查询操作。
         * 第一个参数 uri：为要查询的数据库+表的名称。
         * 第二个参数 projection ： 要查询的列。
         * 第三个参数 selection ： 查询的条件，相当于SQL where。
         * 第三个参数 selectionArgs ： 查询条件的参数，相当于 ？。
         * 第四个参数 sortOrder ： 结果排序。
         */
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // 视频ID:MediaStore.Audio.Media._ID
                String videoPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));

                Log.d(TAG, "getVideoInfo: videoPath: " + videoPath);
                publishFragmrnt.setVideoAbsPath(videoPath);
                String selection = MediaStore.Video.Media.DATA +"=?";
                String[] selectionArgs = new String[]{
                        videoPath
                };

                /*Cursor newCursor0 = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

                if(newCursor0.moveToFirst()){
                    String path = newCursor0.getString(newCursor0.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    Log.d(TAG, "getVideoInfo: path: " + path);
                }*/

                Cursor newCursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, selection, selectionArgs, null);

                if(newCursor != null){
                    if(newCursor.moveToFirst()){
                        int videoId = newCursor.getInt(newCursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                        Log.d(TAG, "getVideoInfo: " + videoId);

                        selection = MediaStore.Video.Thumbnails.VIDEO_ID +"=?";
                        selectionArgs = new String[]{
                                videoId+""
                        };
                        Cursor thumbCursor = cr.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, null, selection, selectionArgs, null);

                        if(thumbCursor != null){
                            if(thumbCursor.moveToFirst()){
                                String path = thumbCursor.getString(thumbCursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
                                Log.d(TAG, "getVideoInfo: path: " + path);
                                thumbCursor.close();
                                return path;
                            }
                            thumbCursor.close();
                        }
                    }

                    newCursor.close();
                }


            }
            cursor.close();
        }

        Log.d(TAG, "getVideoInfo: zhang path is null");
        return null;
    }



}
