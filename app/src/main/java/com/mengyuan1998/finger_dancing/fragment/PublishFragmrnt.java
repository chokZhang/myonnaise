package com.mengyuan1998.finger_dancing.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mengyuan1998.finger_dancing.R;
import com.mengyuan1998.finger_dancing.Utilities.GetImgUtils;
import com.mengyuan1998.finger_dancing.Utilities.HttpUtil;
import com.mengyuan1998.finger_dancing.Utilities.JsonUtils;
import com.mengyuan1998.finger_dancing.Utilities.VedioAutoPlayer;
import com.mengyuan1998.finger_dancing.Utilities.info_rv_adapter_item.BaseItem;

import java.io.File;
import java.io.IOException;


import androidx.fragment.app.FragmentTransaction;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class PublishFragmrnt extends BaseFragment {

    private static final String TAG = "PublishFragmrnt";

    VedioAutoPlayer vedio_player;
    TextView publish;
    EditText editText;
    Fragment1CallBack callBack;
    private final String uploadUrl = "http://39.96.24.179/upload";
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
        publish = findViewById(R.id.publish);
        editText = findViewById(R.id.talkSomething);
        vedio_player = findViewById(R.id.video);

        final File file = new File(getActivity().getExternalCacheDir() + "/vedios", "output_vedio.mp4");

        vedio_player.setUp(file.getAbsolutePath(),JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "video");

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
                String info = editText.getText().toString();
                Log.d(TAG, "onClick: info: " + info);
                postFile(file, info);
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
        vedio_player.startVideo();

    }

    @Override
    public void onStop(){
        super.onStop();
        JZVideoPlayer.releaseAllVideos();
    }

    public void postFile(File file, String info){



        File img = new File(getActivity().getExternalCacheDir() + "/vedios", "img.jpg");

        File[] files = new File[2];
        files[0] = file;
        files[1] = img;

        HttpUtil.postFile(uploadUrl, info, new HttpUtil.ProgressListener() {
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
        }, files);
    }

    public void setInterface(Fragment1CallBack callBack){
        this.callBack = callBack;
    }

    public interface Fragment1CallBack{
        public void onPostDone();

    }


}
