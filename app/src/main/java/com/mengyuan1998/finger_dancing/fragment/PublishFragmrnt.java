package com.mengyuan1998.finger_dancing.fragment;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.facebook.drawee.view.SimpleDraweeView;
import com.mengyuan1998.finger_dancing.R;
import com.mengyuan1998.finger_dancing.Utilities.HttpUtil;
import com.mengyuan1998.finger_dancing.Utilities.JsonUtils;
import com.mengyuan1998.finger_dancing.Utilities.info_rv_adapter_item.BaseItem;
import com.mengyuan1998.finger_dancing.Utilities.widget.OnShowThumbnailListener;
import com.mengyuan1998.finger_dancing.Utilities.widget.PlayStateParams;
import com.mengyuan1998.finger_dancing.Utilities.widget.PlayerView;
import com.mengyuan1998.finger_dancing.Utilities.widget.VideoijkBean;
import com.mengyuan1998.finger_dancing.ui.InfoActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class PublishFragmrnt extends BaseFragment {

    private static final String TAG = "PublishFragmrnt";

    PlayerView vedio_player;
    TextView publish;
    EditText editText;
    TextView cancel;
    OnPostListener callBack;
    private String videoName;
    private String videoAbsPath;
    private String videoThumbnailPath;
    private Uri videoUri;
    private final String uploadUrl = "http://39.96.90.194:8888/upload";
    private static PublishFragmrnt instance = new PublishFragmrnt();
    private boolean publish_state = false;
    private boolean cancel_state = false;

    public static PublishFragmrnt getInstance(){
        return instance;
    }


    @Override
    protected int attachLayoutRes() {
        return R.layout.fragment_publish;
    }

    @Override
    protected void initViews(Context context) {
        /*final File file = new File(getActivity().getExternalCacheDir() + "/vedios", videoName);

        Log.d(TAG, "initViews: " + file.getAbsolutePath());*/

        publish_state = true;
        cancel_state = true;

        final String pathUrl = "file://" + videoAbsPath;
        final File file = new File(videoAbsPath);

        publish = findViewById(R.id.publish);
        cancel =findViewById(R.id.cancel);
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

                        if(videoThumbnailPath != null){
                            Bitmap bmp= BitmapFactory.decodeFile(videoThumbnailPath);
                            ivThumbnail.setImageBitmap(bmp);
                            Log.d(TAG, "onShowThumbnail: setThumbnail");
                        }
                        else{
                            //Glide加载本地视频缩略图
                            RequestOptions requestOptions = new RequestOptions()
                                    .placeholder(R.color.cl_default)
                                    .error(R.color.cl_default)
                                    .dontAnimate()
                                    .fallback(R.color.cl_default);



                            Glide.with(getActivity())
                                    .load(pathUrl)
                                    .apply(requestOptions)
                                    .into(ivThumbnail);
                        }

                    }
                })
                .setPlaySource(pathUrl);


        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!publish_state){
                    return;
                }
                publish_state = false;
                final String info = editText.getText().toString();
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        postFile(file, info);
                    }
                }).start();*/

                new GetImageCacheAsyncTask(getActivity(), file, info).execute(pathUrl);
                CommunityFragment.getInstance().addHeader();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.remove(instance);
                ft.commit();
                Log.d(TAG, "onClick: gone");
                callBack.onPostDone();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cancel_state){
                    return;
                }
                cancel_state = false;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.remove(instance);
                ft.commit();
                callBack.onCancel();
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
        vedio_player.pausePlay();
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        vedio_player.onDestroy();
    }

    public void postFile(final File[] files, String info){

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
                callBack.onFailure();
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
                        callBack.onFailure();
                    }
                    else{
                        try{
                            BaseItem baseItem = JsonUtils.parseJSON(result);
                            //只有一个元素
                            if(null != baseItem){
                                CommunityFragment.getInstance().update(0, baseItem);
                            }
                            files[0].delete();
                            callBack.onSuccess();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, files);
    }





    public void setInterface(OnPostListener callBack){
        this.callBack = callBack;
    }

    //监听开始上传
    public interface OnPostListener{
        public void onPostDone();

        public void onFailure();

        public void onSuccess();

        void onCancel();

    }

    private class GetImageCacheAsyncTask extends AsyncTask<String, Void, File> {
        private static final String TAG = "getImageCacheAsyncTask";
        private final Context context;
        private File videoFile;
        private String info;
        public boolean getImgState = false;

        public GetImageCacheAsyncTask(Context context, File file, String info) {
            this.context = context;
            this.videoFile = file;
            this.info = info;
        }

        @Override
        protected File doInBackground(String... params) {
            /*String imgUrl = params[0];
            try {
                getImgState = false;
                return Glide.with(context)
                        .load(imgUrl)
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }*/
            if(videoThumbnailPath != null){
                File file = new File(videoThumbnailPath);
                return file;
            }
            return null;
        }

        @Override
        protected void onPostExecute(File result) {
            if (result == null) {
                Log.d(TAG, "onPostExecute: get Img failed");
                return;
            }
            Log.d(TAG, "onPostExecute: file path: " + result.getAbsolutePath());

            /*File imgFile = new File(getActivity().getExternalCacheDir() + "/vedios", "img.png");
            try{
                if(imgFile.exists()){
                    imgFile.delete();
                }
                imgFile.createNewFile();
            }catch (Exception e){
                e.printStackTrace();
            }
            copySdcardFile(result.getAbsolutePath(), imgFile.getAbsolutePath());*/
            getImgState = true;
            File[] files = new File[2];
            files[0] = videoFile;
            files[1] = result;
            postFile(files, info);

        }
    }

    public static boolean copySdcardFile(String fromFile, String toFile) {

        try {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return true;

        } catch (Exception ex) {
            return false;
        }
    }



    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoThumbnailPath() {
        return videoThumbnailPath;
    }

    public void setVideoThumbnailPath(String videoThumbnailPath) {
        this.videoThumbnailPath = videoThumbnailPath;
    }

    public Uri getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(Uri videoUri) {
        this.videoUri = videoUri;
    }

    public String getVideoAbsPath() {
        return videoAbsPath;
    }

    public void setVideoAbsPath(String videoAbsPath) {
        this.videoAbsPath = videoAbsPath;
    }
}
