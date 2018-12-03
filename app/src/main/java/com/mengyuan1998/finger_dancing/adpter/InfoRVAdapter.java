package com.mengyuan1998.finger_dancing.adpter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mengyuan1998.finger_dancing.R;
import com.mengyuan1998.finger_dancing.Utilities.info_rv_adapter_item.BaseItem;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cn.jzvd.JZVideoPlayerStandard;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class InfoRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "InfoRVAdapter";

    private final int TYPE_VIDEO = 0;
    private final int TYPE_IMG = 1;
    private final int TYPE_TEXT = 2;
    private final int TYPE_USER_VEDIO = 3;

    private int fragmentIndex = 0;

    List<BaseItem> mList;

    Handler handler = new Handler();

    Context context;

    public InfoRVAdapter(Context context, List<BaseItem> items){
        this.context = context;
        mList = items;
        Log.d(TAG, "InfoRVAdapter: get start");
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (context != null){
            this.context = parent.getContext();
        }
        View view = null;
        switch (viewType){
            case TYPE_VIDEO: {
                if(fragmentIndex == 3){
                    view = View.inflate(context, R.layout.user_vedio_item, null);
                    return new UserVedioViewHolder(view);
                }

                view = View.inflate(context, R.layout.community_vedio_item, null);
                return new VideoViewHolder(view);
            }
            case TYPE_IMG: {

                break;
            }
            case TYPE_TEXT: {

                break;
            }

        }

        return new TextViewHolder(null);
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final BaseItem item = mList.get(position);

        if(holder instanceof VideoViewHolder){
            VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
            videoViewHolder.user_name.setText(item.getUser_name());
            videoViewHolder.praise_num.setText(item.getPraise_num() + " 次点赞");

            Log.d(TAG, "onBindViewHolder: url is " + item.getUrl());
            videoViewHolder.vedio_player.setUp(item.getUrl(), JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "video");

            if(null != item.getBitmap()){
                videoViewHolder.vedio_player.thumbImageView.setImageBitmap(item.getBitmap());
            }

            videoViewHolder.text_info.setText(item.getInfo_text());


            if(!item.isGetImgState()){
                new Thread(){
                    @Override
                    public void run() {
                        getBitmapFormUrl(item.getUrl(), position);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                notifyItemChanged(position);
                            }
                        });
                    }
                }.start();
            }

        }
        else if(holder instanceof UserVedioViewHolder){
            UserVedioViewHolder vedioViewHolder = (UserVedioViewHolder) holder;

            vedioViewHolder.vedio_player.setUp(item.getUrl(), JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "video");

            if(null != item.getBitmap()){
                vedioViewHolder.vedio_player.thumbImageView.setImageBitmap(item.getBitmap());
            }

            vedioViewHolder.text_info.setText("这是一个测试");


            if(!item.isGetImgState()){
                new Thread(){
                    @Override
                    public void run() {
                        getBitmapFormUrl(item.getUrl(), position);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                notifyItemChanged(position);
                            }
                        });
                    }
                }.start();
            }
        }

    }


    class VideoViewHolder extends RecyclerView.ViewHolder{

        CommonView commonView = new CommonView();
        JZVideoPlayerStandard vedio_player;
        TextView text_info;
        ImageView head_img;
        TextView  user_name;
        TextView praise_num;


        public VideoViewHolder(View view){
            super(view);
            InitCommonView(view, commonView);
            vedio_player = view.findViewById(R.id.video);
            text_info = view.findViewById(R.id.text_info);
            head_img = view.findViewById(R.id.head_img);
            user_name = view.findViewById(R.id.user_name);
            praise_num = view.findViewById(R.id.praise_num);
            Log.d(TAG, "VideoViewHolder: get there");
        }
    }

    class UserVedioViewHolder extends RecyclerView.ViewHolder{

        CommonView commonView = new CommonView();
        JZVideoPlayerStandard vedio_player;
        TextView text_info;
        TextView praise_right_num;
        TextView share_right_num;

        public UserVedioViewHolder(View view){
            super(view);
            InitCommonView(view, commonView);
            vedio_player = view.findViewById(R.id.video);
            text_info = view.findViewById(R.id.text_info);
            praise_right_num = view.findViewById(R.id.share_right_num);
            share_right_num = view.findViewById(R.id.share_right_num);
            Log.d(TAG, "VideoViewHolder: get there");
        }
    }


    class ImgViewHolder extends RecyclerView.ViewHolder{

        public ImgViewHolder(View view){
            super(view);
        }
    }

    class TextViewHolder extends RecyclerView.ViewHolder{

        public TextViewHolder(View view){
            super(view);
        }
    }

    class CommonView{

        ImageView praise;
        ImageView share;

    }

    protected void InitCommonView(View itemView, CommonView commonView){

        commonView.praise = itemView.findViewById(R.id.praise);
        commonView.share = itemView.findViewById(R.id.share);

    }

    /*void bindCommonData(CommonView commonView, BaseItem item){
        //commonView.head_img.setImageURI(item.getHeadImg_url());
        commonView.user_name.setText(item.getUser_name());
        commonView.praise_num.setText(item.getPraise_num() + " 次点赞");

    }*/


    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position){
        BaseItem item = mList.get(position);

        if(null != item && item.getType() != -1){
            return item.getType();
        }

        return 1;
    }

    public Bitmap getBitmapFormUrl(String url, final int position) {
        Log.d(TAG, "getBitmapFormUrl: url: " + url);
        Bitmap bitmap = null;
        mList.get(position).setGetImgState(true);
        FFmpegMediaMetadataRetriever retriever = new  FFmpegMediaMetadataRetriever();
        try {
            retriever.setDataSource(url);
        /*getFrameAtTime()--->在setDataSource()之后调用此方法。 如果可能，该方法在任何时间位置找到代表性的帧，
         并将其作为位图返回。这对于生成输入数据源的缩略图很有用。**/
            bitmap = retriever.getFrameAtTime(100000,FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC );
            mList.get(position).setBitmap(bitmap);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public List<BaseItem> getmList() {
        return mList;
    }

    public void setmList(List<BaseItem> mList) {
        this.mList = mList;
    }

    public int getFragmentIndex() {
        return fragmentIndex;
    }

    public void setFragmentIndex(int fragmentIndex) {
        this.fragmentIndex = fragmentIndex;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}
