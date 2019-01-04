package com.mengyuan1998.finger_dancing.adpter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.common.logging.LoggingDelegate;
import com.facebook.drawee.view.SimpleDraweeView;
import com.mengyuan1998.finger_dancing.R;
import com.mengyuan1998.finger_dancing.Utilities.info_rv_adapter_item.BaseItem;
import com.mengyuan1998.finger_dancing.Utilities.widget.OnShowThumbnailListener;
import com.mengyuan1998.finger_dancing.Utilities.widget.PlayStateParams;
import com.mengyuan1998.finger_dancing.Utilities.widget.PlayerView;
import com.mengyuan1998.finger_dancing.Utilities.widget.VideoijkBean;
import com.mengyuan1998.finger_dancing.base.Config;
import com.mengyuan1998.finger_dancing.my_view.thumbUplib;
import com.mengyuan1998.finger_dancing.util.DialogUtils;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class InfoRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "InfoRVAdapter";

    private final int TYPE_VIDEO = 0;
    private final int TYPE_IMG = 1;
    private final int TYPE_TEXT = 2;
    private final int TYPE_USER_VEDIO = 3;

    private PlayerView vedioPresent;

    private int fragmentIndex = 0;

    List<BaseItem> mList;

    Handler handler = new Handler();

    Activity mActivity;

    Context context;

    static public int flag=0;

    //分享内容
    private IWXAPI api;
    //分享内容变量结束


    public InfoRVAdapter(Activity activity, List<BaseItem> items){
        this.mActivity = activity;
        this.context = activity;
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
            default:{
                Log.d(TAG, "onCreateViewHolder: default");
                view = View.inflate(context, R.layout.community_upload_item, null);
                return new UploadHolder(view);
            }

        }

        return new TextViewHolder(null);
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final BaseItem item = mList.get(position);

        if(holder instanceof VideoViewHolder){
            //获取图片
            final VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
            videoViewHolder.mBaseItem = item;
            videoViewHolder.tag = item.getUrl();
            if(!item.isGetImgState()){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap bitmap = getBitmapFormUrl(item.getUrl());
                        mList.get(mList.indexOf(videoViewHolder.mBaseItem)).setGetImgState(true);
                        if(bitmap != null){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //ivThumbnail.setImageBitmap(bitmap);
                                    mList.get(mList.indexOf(videoViewHolder.mBaseItem)).setBitmap(bitmap);
                                    notifyItemChanged(mList.indexOf(videoViewHolder.mBaseItem));
                                }
                            });
                        }
                    }
                }).start();
                if(item.getBitmap() == null){
                    videoViewHolder.mVideoPresent.showThumbnail(new OnShowThumbnailListener() {
                        @Override
                        public void onShowThumbnail(final ImageView ivThumbnail) {
                            ivThumbnail.setImageDrawable(new ColorDrawable(0x424242));
                        }
                    });
                }
            }
            else{
                if(item.getBitmap() != null && videoViewHolder.tag.equals(item.getUrl())){
                    videoViewHolder.mVideoPresent.showThumbnail(new OnShowThumbnailListener() {
                        @Override
                        public void onShowThumbnail(final ImageView ivThumbnail) {
                            ivThumbnail.setImageBitmap(item.getBitmap());
                        }
                    });
                }
                else {
                    videoViewHolder.mVideoPresent.showThumbnail(new OnShowThumbnailListener() {
                        @Override
                        public void onShowThumbnail(final ImageView ivThumbnail) {
                            ivThumbnail.setImageDrawable(new ColorDrawable(0x424242));
                        }
                    });
                }

            }
            videoViewHolder.commonView.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("fuckItem sharebefore ",String.valueOf(flag));
                    shareit(item.getUrl());
                }
            });
            videoViewHolder.commonView.praise.setOnThumbUp(new thumbUplib.OnThumbUp() {
                @Override
                public void like(boolean like) {
                    if (like) {
                        item.setPraise_num(item.getPraise_num()+1);
                        Log.d("fuckItem community ",String .valueOf(item.getPraise_num()));
                        videoViewHolder.praise_num.setText(item.getPraise_num() + " 次点赞");
                    } else {
                        item.setPraise_num(item.getPraise_num()-1);
                        Log.d("fuckItem community ",String .valueOf(item.getPraise_num()));
                        videoViewHolder.praise_num.setText(item.getPraise_num() + " 次点赞");
                    }
                }
            });
            videoViewHolder.praise_num.setText(item.getPraise_num() + " 次点赞");

            Log.d(TAG, "onBindViewHolder: url is " + item.getUrl());


            videoViewHolder.mVideoPresent.setPlaySource(item.getUrl());
            videoViewHolder.text_info.setText(item.getInfo_text());
            videoViewHolder.user_name.setText(item.getUser_name());
        }
        else if(holder instanceof UserVedioViewHolder){
            final UserVedioViewHolder vedioViewHolder = (UserVedioViewHolder) holder;
            vedioViewHolder.commonView.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("fuckItem before flag",String.valueOf(flag));
                    item.setShare_num(item.getShare_num()+flag);
                    Log.d("fuckItem before num",String.valueOf(item.getShare_num()));
                    vedioViewHolder.share_right_num.setText(String.valueOf(item.getShare_num()));
                    shareit(item.getUrl());
                }
            });

            vedioViewHolder.commonView.praise.setOnThumbUp(new thumbUplib.OnThumbUp() {
                @Override
                public void like(boolean like) {
                    if (like) {
                        item.setPraise_num(item.getPraise_num()+1);
                        vedioViewHolder.praise_right_num.setText(String.valueOf(item.getPraise_num()));
                    } else {
                        item.setPraise_num(item.getPraise_num()-1);
                        vedioViewHolder.praise_right_num.setText(String.valueOf(item.getPraise_num()));
                    }
                }
            });
            vedioViewHolder.praise_right_num.setText(String.valueOf(item.getPraise_num()));

            vedioViewHolder.mVideoPresent.showThumbnail(new OnShowThumbnailListener() {
                @Override
                public void onShowThumbnail(final ImageView ivThumbnail) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Bitmap bitmap = getBitmapFormUrl(item.getUrl());
                            mList.get(vedioViewHolder.getAdapterPosition()).setGetImgState(true);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ivThumbnail.setImageBitmap(bitmap);
                                }
                            });
                        }
                    }).start();
                }
            }).setPlaySource(item.getUrl());

            vedioViewHolder.text_info.setText("这是一个测试");


            if(!item.isGetImgState()){
                new Thread(){
                    @Override
                    public void run() {
                        getBitmapFormUrl(item.getUrl());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                notifyItemChanged(vedioViewHolder.getAdapterPosition());
                            }
                        });
                    }
                }.start();
            }
        }

    }


    class VideoViewHolder extends RecyclerView.ViewHolder{

        CommonView commonView = new CommonView();
        //IjkVideoView vedio_player;
        TextView text_info;
        ImageView head_img;
        TextView  user_name;
        TextView praise_num;
        PlayerView mVideoPresent;
        //设置tag以防止图片错乱
        String tag;
        BaseItem mBaseItem;


        public VideoViewHolder(View view){
            super(view);
            InitCommonView(view, commonView);
            /*vedio_player = view.findViewById(R.id.video);
            vedio_player.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: click");
                    vedio_player.start();
                }
            });*/
            mVideoPresent = new PlayerView(mActivity, view) {
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
                    .setTitle("什么")
                    .setProcessDurationOrientation(PlayStateParams.PROCESS_PORTRAIT)
                    .setScaleType(PlayStateParams.fillparent)
                    .forbidTouch(false)
                    .hideSteam(true)
                    .hideHideTopBar(true)
                    .hideCenterPlayer(false);
                    /*.setPlaySource(list)
                    //设置免费播放时长
                    //.setChargeTie(true,60)
                    .startPlay();*/

            /*mVideoPresent.setOnStartListener(new PlayerView.OnStartListener() {
                @Override
                public void getPlayer(PlayerView playerView) {
                    if(vedioPresent == null){
                        vedioPresent = playerView;
                    }
                    else{
                        vedioPresent.pauseAndRelease();
                        vedioPresent = playerView;
                    }
                }
            });*/
            text_info = view.findViewById(R.id.text_info);
            head_img = view.findViewById(R.id.head_img);
            user_name = view.findViewById(R.id.user_name);
            praise_num = view.findViewById(R.id.praise_num);
            Log.d(TAG, "VideoViewHolder: get there");
        }
    }

    class UserVedioViewHolder extends RecyclerView.ViewHolder{
        PlayerView mVideoPresent;
        CommonView commonView = new CommonView();
        TextView text_info;
        TextView praise_right_num;
        TextView share_right_num;

        public UserVedioViewHolder(View view){
            super(view);
            InitCommonView(view, commonView);

            text_info = view.findViewById(R.id.text_info);
            praise_right_num = view.findViewById(R.id.share_right_num);
            share_right_num = view.findViewById(R.id.share_right_num);
            mVideoPresent = new PlayerView(mActivity, view) {
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
                    .setTitle("什么")
                    .setProcessDurationOrientation(PlayStateParams.PROCESS_PORTRAIT)
                    .setScaleType(PlayStateParams.fillparent)
                    .forbidTouch(false)
                    .hideSteam(true)
                    .hideHideTopBar(true)
                    .hideCenterPlayer(false);
            Log.d(TAG, "VideoViewHolder: get there");
        }
    }


    class ImgViewHolder extends RecyclerView.ViewHolder{

        public ImgViewHolder(View view){
            super(view);
        }
    }

    class UploadHolder extends RecyclerView.ViewHolder{

        public UploadHolder(View view){
            super(view);

        }
    }

    class TextViewHolder extends RecyclerView.ViewHolder{

        public TextViewHolder(View view){
            super(view);
        }
    }

    class CommonView{

        thumbUplib praise;
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

        if(null != item){
            return item.getType();
        }

        return 1;
    }

    public Bitmap getBitmapFormUrl(String url) {
        Log.d(TAG, "getBitmapFormUrl: url: " + url);
        Bitmap bitmap = null;
        FFmpegMediaMetadataRetriever retriever = new  FFmpegMediaMetadataRetriever();
        try {
            retriever.setDataSource(url);
        /*getFrameAtTime()--->在setDataSource()之后调用此方法。 如果可能，该方法在任何时间位置找到代表性的帧，
         并将其作为位图返回。这对于生成输入数据源的缩略图很有用。**/
            bitmap = retriever.getFrameAtTime(100000,FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC );
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

    public void addHeader(){
        Log.d(TAG, "addHeader: added");
        mList.add(0, new BaseItem());
        notifyItemInserted(0);
    }

    public void releaseHeader(){
        Log.d(TAG, "releaseHeader: remove");
        mList.remove(0);
        notifyItemRemoved(0);
    }

    public void insertToTop(final List<BaseItem> items){
        for(int i = 0; i < items.size(); i++){
            mList.add(i, items.get(i));
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                notifyItemRangeInserted(0, items.size());
            }
        });
    }

    public void insertToBottom(final List<BaseItem> items){
        final int lastSize = mList.size();
        for(int i = 0; i < items.size(); i++){
            mList.add(items.get(i));
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                notifyItemRangeInserted(lastSize, items.size());
            }
        });
    }

    public void update(final int position, BaseItem item){
        mList.set(position, item);
        handler.post(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(position);
            }
        });
    }

    public void realease(){
        if(vedioPresent != null)
        vedioPresent.pausePlay();
        vedioPresent = null;
    }

    //微信分享内容
    private void initWx() {
        api = WXAPIFactory.createWXAPI(context, Config.APP_ID_WX,true);
        // 将该app注册到微信
        api.registerApp(Config.APP_ID_WX);
    }
    private void shareit(String Url)
    {
        flag=0;
        initWx();
        Activity activity = (Activity) context;
        String content=Url;
        DialogUtils.showSharedDialog(activity, content, new DialogUtils.SharedListener() {
            @Override
            public void sharedToWXFriend(String content) {
                sendToWeiXin("微信分享",content,"我是微信分享",BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher),0);
                Log.d("fuckItem aftershare ",String.valueOf(flag));
            }

            @Override
            public void sharedToWXFriendCircle(String content) {
                sendToWeiXin("微信分享",content,"我是微信分享",BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher),1);
            }

            @Override
            public void sharedToWXCollect(String content) {
                sendToWeiXin("微信分享",content,"我是微信分享",BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher),2);
            }
        });
    }
    public void sendToWeiXin(String title, String openUrl, String description, Bitmap icon, int requestCode) {
        //初始化一个WXVideoObject，填写url
        WXVideoObject video = new WXVideoObject();
        video.videoUrl =openUrl;
        //用 WXVideoObject 对象初始化一个 WXMediaMessage 对象
        WXMediaMessage msg = new WXMediaMessage(video);
        msg.title =title;
        msg.description =description;
        msg.setThumbImage(icon);
        /*//初始化一个WXWebpageObject对象，填写url
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = openUrl;
        //Y用WXWebpageObject对象初始化一个WXMediaMessage对象，填写标题、描述
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;//网页标题
        msg.description = description;//网页描述
        msg.setThumbImage(icon);
        //构建一个Req
        */
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "supplier";
        req.message = msg;
        req.scene = requestCode;
        api.sendReq(req);
        flag++;
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

    public void setmActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }
}
