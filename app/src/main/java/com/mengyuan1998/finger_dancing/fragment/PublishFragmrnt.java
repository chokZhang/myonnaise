package com.mengyuan1998.finger_dancing.fragment;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mengyuan1998.finger_dancing.R;
import com.mengyuan1998.finger_dancing.Utilities.GetImgUtils;
import com.mengyuan1998.finger_dancing.Utilities.HttpUtil;
import com.mengyuan1998.finger_dancing.Utilities.JsonUtils;
import com.mengyuan1998.finger_dancing.Utilities.VedioAutoPlayer;
import com.mengyuan1998.finger_dancing.Utilities.info_rv_adapter_item.BaseItem;
import com.mengyuan1998.finger_dancing.Utilities.widget.OnShowThumbnailListener;
import com.mengyuan1998.finger_dancing.Utilities.widget.PlayStateParams;
import com.mengyuan1998.finger_dancing.Utilities.widget.PlayerView;
import com.mengyuan1998.finger_dancing.Utilities.widget.VideoijkBean;

import java.io.File;
import java.io.IOException;
import java.util.List;


import androidx.fragment.app.FragmentTransaction;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class PublishFragmrnt extends BaseFragment {

    private static final String TAG = "PublishFragmrnt";

    PlayerView vedio_player;
    TextView publish;
    EditText editText;
    Fragment1CallBack callBack;
    private final String uploadUrl = "http://39.96.24.179/upload";
    //是否获取到视频的缩略图, 1代表成功， -1代表失败
    private int saveThumbnailState;
    private static PublishFragmrnt instance = new PublishFragmrnt();

    public static PublishFragmrnt getInstance(){
        return instance;
    }


    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_publish;
    }

    @Override
    protected void initViews(Context context) {
        final File file = new File(getActivity().getExternalCacheDir() + "/vedios", "output_vedio.mp4");

        Log.d(TAG, "initViews: " + file.getAbsolutePath());

        final String pathUrl = "file://" + file.getAbsolutePath();

        publish = findViewById(R.id.publish);
        editText = findViewById(R.id.talkSomething);

        vedio_player = new PlayerView(getActivity(), mRootView){
            @Override
            public PlayerView toggleProcessDurationOrientation() {
                hideSteam(getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return setProcessDurationOrientation(getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ? PlayStateParams.PROCESS_PORTRAIT : PlayStateParams.PROCESS_LANDSCAPE);
            }

            @Override
            public PlayerView setPlaySource(List<VideoijkBean> list) {
                return super.setPlaySource(list);
            }
        }
                .setProcessDurationOrientation(PlayStateParams.PROCESS_PORTRAIT)
                .setScaleType(PlayStateParams.fillparent)
                .forbidTouch(false)
                .hideSteam(true)
                .hideHideTopBar(true)
                .hideCenterPlayer(false)
                .showThumbnail(new OnShowThumbnailListener() {
                    @Override
                    public void onShowThumbnail(ImageView ivThumbnail) {
                        Glide.with(getActivity())
                                .load(pathUrl)
                                .placeholder(R.color.cl_default)
                                .error(R.color.cl_default)
                                .into(ivThumbnail);

                    }
                })
                .setPlaySource(pathUrl);


        vedio_player.setLoop(true);


        /*new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap =  GetImgUtils.getLocalVideoThumbnail(file.getAbsolutePath());
                if(null != bitmap){

                }
            }
        }).start();*/


        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String info = editText.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        postFile(file, info);
                    }
                }).start();
                CommunityFragment.getInstance().addHeader();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.remove(instance);
                ft.commit();
                Log.d(TAG, "onClick: gone");
                callBack.onPostDone();
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        editText.setText("");
    }

    @Override
    public void onResume(){
        super.onResume();

    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        vedio_player.onDestroy();
    }

    public void postFile(File file, String info){

        //可以在这做一些逻辑处理，例如添加一些文件


        HttpUtil.postFile(uploadUrl, info, new HttpUtil.ProgressListener() {
            @Override
            public void onProgress(long currentBytes, long contentLength, boolean done) {
                Log.i(TAG, "currentBytes==" + currentBytes + "==contentLength==" + contentLength + "==done==" + done);


            }
        }, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: err");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: get in");
                if (response != null) {
                    String result = response.body().string();
                    Log.i(TAG, "result===" + result);
                    if(response.code() != 200){
                        Toast.makeText(getActivity(), "网络状态错误", Toast.LENGTH_SHORT).show();
                    }
                    try{
                        BaseItem baseItem = JsonUtils.parseJSON(result);
                        //只有一个元素
                        if(null != baseItem){
                            CommunityFragment.getInstance().update(0, baseItem);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }, file);
    }



    public void setInterface(Fragment1CallBack callBack){
        this.callBack = callBack;
    }

    public interface Fragment1CallBack{
        public void onPostDone();
    }


}
