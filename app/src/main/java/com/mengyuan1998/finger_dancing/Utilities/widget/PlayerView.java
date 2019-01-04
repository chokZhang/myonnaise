package com.mengyuan1998.finger_dancing.Utilities.widget;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;


import com.facebook.drawee.view.SimpleDraweeView;
import com.mengyuan1998.finger_dancing.R;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


public class PlayerView {

    /**
     * 打印日志的TAG
     */
    private static final String TAG = PlayerView.class.getSimpleName();
    /**
     * 全局上下文
     */
    private final Context mContext;
    /**
     * 依附的容器Activity
     */
    private final Activity mActivity;
    /**
     * Activity界面的中布局的查询器
     */
    private final LayoutQuery query;
    /**
     * 原生的Ijkplayer
     */
    private final IjkVideoView videoView;
    /**
     * 播放器整个界面
     */
    private final View rl_box;
    /**
     * 播放器顶部控制bar
     */
    private final View ll_topbar;
    /**
     * 播放器底部控制bar
     */
    private final View ll_bottombar;
    /**
     * 播放器封面，播放前的封面或者缩列图
     */
    private final ImageView iv_trumb;
    /**
     * 视频方向旋转按钮
     */
    private final ImageView iv_rotation = null;
    /**
     * 视频返回按钮
     */
    private final ImageView iv_back;
    /**
     * 视频菜单按钮
     */
    private final ImageView iv_menu ;
    /**
     * 视频bottonbar的播放按钮
     */
    private final ImageView iv_bar_player;
    /**
     * 视频中间的播放按钮
     */
    private final ImageView iv_player;
    /**
     * 视频全屏按钮
     */
    private final ImageView iv_fullscreen;
    /**
     * 视频分辨率按钮
     */
    private final TextView tv_steam;
    /**
     * 视频加载速度
     */
    private final TextView tv_speed;
    /**
     * 视频播放进度条
     */
    private final SeekBar seekBar;

    /**
     * 码流列表
     */
    private List<VideoijkBean> listVideos = new ArrayList<VideoijkBean>();

    /**
     * 当前状态
     */
    private int status = PlayStateParams.STATE_IDLE;
    /**
     * 当前播放位置
     */
    private int currentPosition;
    /**
     * 滑动进度条得到的新位置，和当前播放位置是有区别的,newPosition =0也会调用设置的，故初始化值为-1
     */
    private long newPosition = -1;
    /**
     * 视频旋转的角度，默认只有0,90.270分别对应向上、向左、向右三个方向
     */
    private int rotation = 0;
    /**
     * 视频显示比例,默认保持原视频的大小
     */
    private int currentShowType = PlayStateParams.fitparent;
    /**
     * 播放总时长
     */
    private long duration;
    /**
     * 当前声音大小
     */
    private int volume;
    /**
     * 设备最大音量
     */
    private final int mMaxVolume;
    /**
     * 获取当前设备的宽度
     */
    private final int screenWidthPixels;
    /**
     * 记录播放器竖屏时的高度
     */
    private final int initHeight;
    /**
     * 当前亮度大小
     */
    private float brightness;
    /**
     * 当前播放地址
     */
    private String currentUrl;
    /**
     * 当前选择的视频流索引
     */
    private int currentSelect;
    /**
     * 记录进行后台时的播放状态0为播放，1为暂停
     */
    private int bgState;
    /**
     * 自动重连的时间
     */
    private int autoConnectTime = 5000;
    /**
     * 第三方so是否支持，默认不支持，true为支持
     */
    private boolean playerSupport;
    /**
     * 是否是直播 默认为非直播，true为直播false为点播，根据isLive()方法前缀rtmp或者后缀.m3u8判断得出的为直播，比较片面，有好的建议欢迎交流
     */
    private boolean isLive;
    /**
     * 是否显示控制面板，默认为隐藏，true为显示false为隐藏
     */
    private boolean isShowControlPanl;
    /**
     * 禁止触摸，默认可以触摸，true为禁止false为可触摸
     */
    private boolean isForbidTouch;
    /**
     * 禁止收起控制面板，默认可以收起，true为禁止false为可触摸
     */
    private boolean isForbidHideControlPanl;
    /**
     * 当前是否切换视频流，默认为否，true是切换视频流，false没有切换
     */
    private boolean isHasSwitchStream;
    /**
     * 是否在拖动进度条中，默认为停止拖动，true为在拖动中，false为停止拖动
     */
    private boolean isDragging;
    /**
     * 播放的时候是否需要网络提示，默认显示网络提示，true为显示网络提示，false不显示网络提示
     */
    private boolean isGNetWork = true;

    private boolean isCharge;
    private int maxPlaytime;
    /**
     * 是否只有全屏，默认非全屏，true为全屏，false为非全屏
     */
    private boolean isOnlyFullScreen;
    /**
     * 是否禁止双击，默认不禁止，true为禁止，false为不禁止
     */
    private boolean isForbidDoulbeUp;
    /**
     * 是否出错停止播放，默认是出错停止播放，true出错停止播放,false为用户点击停止播放
     */
    private boolean isErrorStop = true;
    /**
     * 是否是竖屏，默认为竖屏，true为竖屏，false为横屏
     */
    private boolean isPortrait = true;
    /**
     * 是否隐藏中间播放按钮，默认不隐藏，true为隐藏，false为不隐藏
     */
    private boolean isHideCenterPlayer;
    /**
     * 是否自动重连，默认5秒重连，true为重连，false为不重连
     */
    private boolean isAutoReConnect = true;
    /**
     * 是否隐藏topbar，true为隐藏，false为不隐藏
     */
    private boolean isHideTopBar;
    /**
     * 是否隐藏bottonbar，true为隐藏，false为不隐藏
     */
    private boolean isHideBottonBar;
    /**
     * 音频管理器
     */
    private final AudioManager audioManager;

    /**
     *  是否循环播放
     */
    private boolean isLoop;

    /**
     * 同步进度
     */
    private static final int MESSAGE_SHOW_PROGRESS = 1;
    /**
     * 设置新位置
     */
    private static final int MESSAGE_SEEK_NEW_POSITION = 3;
    /**
     * 隐藏提示的box
     */
    private static final int MESSAGE_HIDE_CENTER_BOX = 4;
    /**
     * 重新播放
     */
    private static final int MESSAGE_RESTART_PLAY = 5;


    /**
     * 消息处理
     */
    @SuppressWarnings("HandlerLeak")
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**滑动完成，隐藏滑动提示的box*/
                case MESSAGE_HIDE_CENTER_BOX:
                    query.id(R.id.app_video_fastForward_box).gone();
                    break;
                /**滑动完成，设置播放进度*/
                case MESSAGE_SEEK_NEW_POSITION:
                    if (!isLive && newPosition >= 0) {
                        videoView.seekTo((int) newPosition);
                        newPosition = -1;
                    }
                    break;
                /**滑动中，同步播放进度*/
                case MESSAGE_SHOW_PROGRESS:
                    long pos = syncProgress();
                    if (!isDragging && isShowControlPanl) {
                        msg = obtainMessage(MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                        updatePausePlay();
                    }
                    break;
                /**重新去播放*/
                case MESSAGE_RESTART_PLAY:
                    status = PlayStateParams.STATE_ERROR;
                    startPlay();
                    updatePausePlay();
                    break;
            }
        }
    };

    /**========================================视频的监听方法==============================================*/

    /**
     * 控制面板收起或者显示的轮询监听
     */
    private AutoPlayRunnable mAutoPlayRunnable = new AutoPlayRunnable();
    /**
     * Activity界面方向监听
     */
    private final OrientationEventListener orientationEventListener;
    /**
     * 控制面板显示或隐藏监听
     */
    private OnControlPanelVisibilityChangeListener onControlPanelVisibilityChangeListener;
    /**
     * 视频封面显示监听
     */
    private OnShowThumbnailListener mOnShowThumbnailListener;
    /**
     * 视频的返回键监听
     */
    private OnPlayerBackListener mPlayerBack;
    /**
     * 视频播放时信息回调
     */
    private IMediaPlayer.OnInfoListener onInfoListener;

    /**
     * 点击事件监听
     */
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: clicked");

            if (v.getId() == R.id.app_video_menu) {
                /**菜单*/
                //TODO 添加新的菜单功能
                //showMenu();
            } else if (v.getId() == R.id.app_video_stream) {
                /**选择分辨率*/
                //TODO 分辨率选择
                //showStreamSelectView();
            } /*else if (v.getId() == R.id.ijk_iv_rotation) {
             *//**旋转视频方向*//*

                //setPlayerRotation();
            }*/ else if (v.getId() == R.id.app_video_fullscreen) {
                /**视频全屏切换*/

            } else if (v.getId() == R.id.app_video_play || v.getId() == R.id.play_icon) {
                /**视频播放和暂停*/
                if (videoView.isPlaying()) {
                    if (isLive) {
                        videoView.stopPlayback();
                    } else {
                        pausePlay();
                    }
                } else {
                    startPlay();
                    if (videoView.isPlaying()) {
                        /**ijkplayer内部的监听没有回调，只能手动修改状态*/
                        status = PlayStateParams.STATE_PREPARING;
                        hideStatusUI();
                        Log.d(TAG, "onClick: zhang hideStatusUI in pauseOrPlay");
                    }
                }
                updatePausePlay();
            } else if (v.getId() == R.id.app_video_finish) {
                /**返回*/
                if (!isOnlyFullScreen && !isPortrait) {
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    if (mPlayerBack != null) {
                        mPlayerBack.onPlayerBack();
                    } else {
                        mActivity.finish();
                    }
                }
            } else if (v.getId() == R.id.app_video_netTie_icon) {
                /**使用移动网络提示继续播放*/
                isGNetWork = false;
                hideStatusUI();
                Log.d(TAG, "onClick: zhang hideStatusUI in app_video_netTie_icon");
                startPlay();
                updatePausePlay();
            } else if (v.getId() == R.id.app_video_replay_icon) {
                /**重新播放*/
                status = PlayStateParams.STATE_ERROR;
                hideStatusUI();
                Log.d(TAG, "onClick: zhang hideStatusUI in app_video_replay_icon");
                startPlay();
                videoView.seekTo(0);
                updatePausePlay();
            }
            else if(v.getId() == R.id.iv_trumb){

            }
        }
    };

    /**
     * 进度条滑动监听
     */
    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {

        /**数值的改变*/
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                /**不是用户拖动的，自动播放滑动的情况*/
                return;
            } else {
                long duration = getDuration();
                int position = (int) ((duration * progress * 1.0) / 1000);
                String time = generateTime(position);
                query.id(R.id.app_video_currentTime).text(time);
                query.id(R.id.app_video_currentTime_full).text(time);
                query.id(R.id.app_video_currentTime_left).text(time);
            }

        }

        /**开始拖动*/
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isDragging = true;
            mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
        }

        /**停止拖动*/
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            long duration = getDuration();
            videoView.seekTo((int) ((duration * seekBar.getProgress() * 1.0) / 1000));
            mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
            isDragging = false;
            mHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, 1000);
        }
    };



    /**
     * ========================================视频的监听方法==============================================
     */

    /**
     * 保留旧的调用方法
     */
    public PlayerView(Activity activity) {
        this(activity, null);
    }

    /**
     * 新的调用方法，适用非Activity中使用PlayerView，例如fragment、holder中使用
     */
    public PlayerView(Activity activity, View rootView) {
        this.mActivity = activity;
        this.mContext = activity;
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
            playerSupport = true;
        } catch (Throwable e) {
            Log.e(TAG, "loadLibraries error", e);
        }
        screenWidthPixels = mContext.getResources().getDisplayMetrics().widthPixels;
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);


        query = new LayoutQuery(mActivity, rootView);
        rl_box = rootView.findViewById(R.id.app_video_box);
        videoView = (IjkVideoView) rootView.findViewById(R.id.video_view);


        ll_topbar = rootView.findViewById(R.id.app_video_top_box);
        ll_bottombar = rootView.findViewById(R.id.ll_bottom_bar);
        iv_trumb =  rootView.findViewById(R.id.iv_trumb);
        iv_back = (ImageView) rootView.findViewById(R.id.app_video_finish);
        iv_menu = (ImageView) rootView.findViewById(R.id.app_video_menu);
        iv_bar_player = (ImageView) rootView.findViewById(R.id.app_video_play);
        iv_player = (ImageView) rootView.findViewById(R.id.play_icon);
        //iv_rotation = (ImageView) rootView.findViewById(R.id.ijk_iv_rotation);
        iv_fullscreen = (ImageView) rootView.findViewById(R.id.app_video_fullscreen);
        tv_steam = (TextView) rootView.findViewById(R.id.app_video_stream);
        tv_speed = (TextView) rootView.findViewById(R.id.app_video_speed);
        seekBar = (SeekBar) rootView.findViewById(R.id.app_video_seekBar);

        seekBar.setMax(1000);
        seekBar.setOnSeekBarChangeListener(mSeekListener);
        iv_bar_player.setOnClickListener(onClickListener);
        iv_player.setOnClickListener(onClickListener);
        iv_fullscreen.setOnClickListener(onClickListener);
        //iv_rotation.setOnClickListener(onClickListener);
        tv_steam.setOnClickListener(onClickListener);
        iv_back.setOnClickListener(onClickListener);
        //iv_menu.setOnClickListener(onClickListener);
        query.id(R.id.app_video_netTie_icon).clicked(onClickListener);
        query.id(R.id.app_video_replay_icon).clicked(onClickListener);
        videoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                if (what == PlayStateParams.MEDIA_INFO_NETWORK_BANDWIDTH || what == PlayStateParams.MEDIA_INFO_BUFFERING_BYTES_UPDATE) {
                    Log.e("", "dou361.====extra=======" + extra);
                    if (tv_speed != null) {
                        tv_speed.setText(getFormatSize(extra));
                    }
                }
                statusChange(what);
                if (onInfoListener != null) {
                    onInfoListener.onInfo(mp, what, extra);
                }
                /*if (isCharge && maxPlaytime < getCurrentPosition()) {
                    query.id(R.id.app_video_freeTie).visible();
                    pausePlay();
                }*/
                return true;
            }
        });


        final GestureDetector gestureDetector = new GestureDetector(mContext, new PlayerGestureListener());
        rl_box.setClickable(true);
        rl_box.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        if (mAutoPlayRunnable != null) {
                            mAutoPlayRunnable.stop();
                        }
                        break;
                }
                if (gestureDetector.onTouchEvent(motionEvent))
                    return true;
                // 处理手势结束
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        endGesture();
                        break;
                }
                return false;
            }
        });


        orientationEventListener = new OrientationEventListener(mActivity) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation >= 0 && orientation <= 30 || orientation >= 330 || (orientation >= 150 && orientation <= 210)) {
                    //竖屏
                    if (isPortrait) {
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        orientationEventListener.disable();
                    }
                } else if ((orientation >= 90 && orientation <= 120) || (orientation >= 240 && orientation <= 300)) {
                    if (!isPortrait) {
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        orientationEventListener.disable();
                    }
                }
            }
        };
        if (isOnlyFullScreen) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        isPortrait = (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initHeight = rl_box.getLayoutParams().height;
        hideAll();
        if (!playerSupport) {
            showStatus(mActivity.getResources().getString(R.string.not_support));
        } else {
            query.id(R.id.ll_bg).visible();
        }
    }

    /**==========================================Activity生命周期方法回调=============================*/
    /**
     * @Override protected void onPause() {
     * super.onPause();
     * if (player != null) {
     * player.onPause();
     * }
     * }
     */
    public PlayerView onPause() {
        bgState = (videoView.isPlaying() ? 0 : 1);
        getCurrentPosition();
        videoView.onPause();
        return this;
    }

    /**
     * @Override protected void onResume() {
     * super.onResume();
     * if (player != null) {
     * player.onResume();
     * }
     * }
     */
    public PlayerView onResume() {
        videoView.onResume();
        if (isLive) {
            videoView.seekTo(0);
        } else {
            videoView.seekTo(currentPosition);
        }
        if (bgState == 0) {

        } else {
            pausePlay();
        }
        return this;
    }

    /**
     * @Override protected void onDestroy() {
     * super.onDestroy();
     * if (player != null) {
     * player.onDestroy();
     * }
     * }
     */
    public PlayerView onDestroy() {
        orientationEventListener.disable();
        mHandler.removeMessages(MESSAGE_RESTART_PLAY);
        mHandler.removeMessages(MESSAGE_SEEK_NEW_POSITION);
        videoView.stopPlayback();
        return this;
    }

    /**
     * @Override public void onConfigurationChanged(Configuration newConfig) {
     * super.onConfigurationChanged(newConfig);
     * if (player != null) {
     * player.onConfigurationChanged(newConfig);
     * }
     * }
     */
    public PlayerView onConfigurationChanged(final Configuration newConfig) {
        isPortrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
        doOnConfigurationChanged(isPortrait);
        return this;
    }

    /**
     * @Override public void onBackPressed() {
     * if (player != null && player.onBackPressed()) {
     * return;
     * }
     * super.onBackPressed();
     * }
     */
    public boolean onBackPressed() {
        if (!isOnlyFullScreen && getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return true;
        }
        return false;
    }

    /**
     * ==========================================Activity生命周期方法回调=============================
     */

    /**
     * ==========================================对外的方法=============================
     */


    /**
     * 显示缩略图
     */
    public PlayerView showThumbnail(OnShowThumbnailListener onShowThumbnailListener) {
        this.mOnShowThumbnailListener = onShowThumbnailListener;
        if (mOnShowThumbnailListener != null && iv_trumb != null) {
            mOnShowThumbnailListener.onShowThumbnail(iv_trumb);
        }
        return this;
    }

    /**
     * 设置播放信息监听回调
     */
    public PlayerView setOnInfoListener(IMediaPlayer.OnInfoListener onInfoListener) {
        this.onInfoListener = onInfoListener;
        return this;
    }

    /**
     * 设置播放器中的返回键监听
     */
    public PlayerView setPlayerBackListener(OnPlayerBackListener listener) {
        this.mPlayerBack = listener;
        return this;
    }

    /**
     * 设置控制面板显示隐藏监听
     */
    public PlayerView setOnControlPanelVisibilityChangListenter(OnControlPanelVisibilityChangeListener listener) {
        this.onControlPanelVisibilityChangeListener = listener;
        return this;
    }

    /**
     * 百分比显示切换
     */
    public PlayerView toggleAspectRatio() {
        if (videoView != null) {
            videoView.toggleAspectRatio();
        }
        return this;
    }

    /**
     * 设置播放区域拉伸类型
     */
    public PlayerView setScaleType(int showType) {
        currentShowType = showType;
        videoView.setAspectRatio(currentShowType);
        return this;
    }

    /**
     * 旋转角度
     */
    public PlayerView setPlayerRotation() {
        if (rotation == 0) {
            rotation = 90;
        } else if (rotation == 90) {
            rotation = 270;
        } else if (rotation == 270) {
            rotation = 0;
        }
        setPlayerRotation(rotation);
        return this;
    }

    /**
     * 旋转指定角度
     */
    public PlayerView setPlayerRotation(int rotation) {
        if (videoView != null) {
            videoView.setPlayerRotation(rotation);
            videoView.setAspectRatio(currentShowType);
        }
        return this;
    }

    /**
     * 设置播放地址
     * 包括视频清晰度列表
     * 对应地址列表
     */
    public PlayerView setPlaySource(List<VideoijkBean> list) {
        listVideos.clear();
        if (list != null && list.size() > 0) {
            listVideos.addAll(list);
            switchStream(0);
        }
        return this;
    }

    /**
     * 设置播放地址
     * 单个视频VideoijkBean
     */
    public PlayerView setPlaySource(VideoijkBean videoijkBean) {
        listVideos.clear();
        if (videoijkBean != null) {
            listVideos.add(videoijkBean);
            switchStream(0);
        }
        return this;
    }

    /**
     * 设置播放地址
     * 单个视频地址时
     * 带流名称
     */
    public PlayerView setPlaySource(String stream, String url) {
        VideoijkBean mVideoijkBean = new VideoijkBean();
        mVideoijkBean.setStream(stream);
        mVideoijkBean.setUrl(url);
        setPlaySource(mVideoijkBean);
        return this;
    }

    /**
     * 设置播放地址
     * 单个视频地址时
     */
    public PlayerView setPlaySource(String url) {
        setPlaySource("标清", url);
        return this;
    }

    /**
     * 自动播放
     */
    public PlayerView autoPlay(String path) {
        setPlaySource(path);
        startPlay();
        return this;
    }

    /**
     * 开始播放
     */
    public PlayerView startPlay() {
        //播放时屏幕常亮
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if(isLoop){
            videoView.setLooping(true);
        }
        if (isLive) {
            videoView.setVideoPath(currentUrl);
            videoView.seekTo(0);
        } else {
            if (isHasSwitchStream || status == PlayStateParams.STATE_ERROR) {
                //换源之后声音可播，画面卡住，主要是渲染问题，目前只是提供了软解方式，后期提供设置方式
                videoView.setRender(videoView.RENDER_TEXTURE_VIEW);
                videoView.setVideoPath(currentUrl);
                videoView.seekTo(currentPosition);
                isHasSwitchStream = false;
            }
        }
        hideStatusUI();
        Log.d(TAG, "startPlay: zhang hideStatusUI in startPlay");
        if (isGNetWork && (NetworkUtils.getNetworkType(mContext) == 4 || NetworkUtils.getNetworkType(mContext) == 5 || NetworkUtils.getNetworkType(mContext) == 6)) {
            query.id(R.id.app_video_netTie).visible();
        } else {
            if (isCharge && maxPlaytime < getCurrentPosition()) {
                //query.id(R.id.app_video_freeTie).visible();

            } else {
                if (playerSupport) {
                    query.id(R.id.app_video_loading).visible();
                    Log.d(TAG, "startPlay: zhang visible in startPlay");
                    videoView.start();
                } else {
                    showStatus(mActivity.getResources().getString(R.string.not_support));
                }
            }
        }
        return this;
    }

    /**
     * 设置视频名称
     */
    public PlayerView setTitle(String title) {
        query.id(R.id.app_video_title).text(title);
        return this;
    }

    /**
     * 选择要播放的流
     */
    public PlayerView switchStream(int index) {
        if (listVideos.size() > index) {
            tv_steam.setText(listVideos.get(index).getStream());
            currentUrl = listVideos.get(index).getUrl();
            listVideos.get(index).setSelect(true);
            isLive();
            if (videoView.isPlaying()) {
                getCurrentPosition();
                videoView.release(false);
            }
            isHasSwitchStream = true;
        }
        return this;
    }

    /**
     * 暂停播放
     */
    public PlayerView pausePlay() {
        //不保持屏幕常亮
        mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        status = PlayStateParams.STATE_PAUSED;
        getCurrentPosition();
        videoView.pause();
        mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return this;
    }

    /**
     * 停止播放
     */
    public PlayerView stopPlay() {
        //不保持屏幕常亮
        mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        videoView.stopPlayback();
        isErrorStop = true;
        if (mHandler != null) {
            mHandler.removeMessages(MESSAGE_RESTART_PLAY);
        }
        return this;
    }

    /**
     * 设置播放位置
     */
    public PlayerView seekTo(int playtime) {
        videoView.seekTo(playtime);
        return this;
    }

    /**
     * 获取当前播放位置
     */
    public int getCurrentPosition() {
        if (!isLive) {
            currentPosition = videoView.getCurrentPosition();
        } else {
            /**直播*/
            currentPosition = -1;
        }
        return currentPosition;
    }

    /**
     * 获取视频播放总时长
     */
    public long getDuration() {
        duration = videoView.getDuration();
        return duration;
    }

    /**
     * 设置2/3/4/5G和WiFi网络类型提示，
     *
     * @param isGNetWork true为进行2/3/4/5G网络类型提示
     *                   false 不进行网络类型提示
     */
    public PlayerView setNetWorkTypeTie(boolean isGNetWork) {
        this.isGNetWork = isGNetWork;
        return this;
    }

    /**
     * 设置最大观看时长
     *
     * @param isCharge    true为收费 false为免费即不做限制
     * @param maxPlaytime 最大能播放时长，单位秒
     */
    public PlayerView setChargeTie(boolean isCharge, int maxPlaytime) {
        this.isCharge = isCharge;
        this.maxPlaytime = maxPlaytime * 1000;
        return this;
    }


    /**
     * 是否仅仅为全屏
     */
    public PlayerView setOnlyFullScreen(boolean isFull) {
        this.isOnlyFullScreen = isFull;
        tryFullScreen(isOnlyFullScreen);
        if (isOnlyFullScreen) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
        return this;
    }

    /**
     * 设置是否禁止双击
     */
    public PlayerView setForbidDoulbeUp(boolean flag) {
        this.isForbidDoulbeUp = flag;
        return this;
    }

    /**
     * 设置是否禁止隐藏bar
     */
    public PlayerView setForbidHideControlPanl(boolean flag) {
        this.isForbidHideControlPanl = flag;
        return this;
    }

    /**
     * 当前播放的是否是直播
     */
    public boolean isLive() {
        if (currentUrl != null
                && (currentUrl.startsWith("rtmp://")
                || (currentUrl.startsWith("http://") && currentUrl.endsWith(".m3u8"))
                || (currentUrl.startsWith("http://") && currentUrl.endsWith(".flv")))) {
            isLive = true;
        } else {
            isLive = false;
        }
        return isLive;
    }

    /**
     * 是否禁止触摸
     */
    public PlayerView forbidTouch(boolean forbidTouch) {
        this.isForbidTouch = forbidTouch;
        return this;
    }

    /**
     * 隐藏所有状态界面
     */
    public PlayerView hideAllUI() {
        if (query != null) {
            hideAll();
        }
        return this;
    }

    /**
     * 获取顶部控制barview
     */
    public View getTopBarView() {
        return ll_topbar;
    }

    /**
     * 获取底部控制barview
     */
    public View getBottonBarView() {
        return ll_bottombar;
    }

    /**
     * 获取旋转view
     */
    public ImageView getRationView() {
        return iv_rotation;
    }

    /**
     * 获取返回view
     */
    public ImageView getBackView() {
        return iv_back;
    }

    /**
     * 获取菜单view
     */
    public ImageView getMenuView() {
        return iv_menu;
    }

    /**
     * 获取全屏按钮view
     */
    public ImageView getFullScreenView() {
        return iv_fullscreen;
    }

    /**
     * 获取底部bar的播放view
     */
    public ImageView getBarPlayerView() {
        return iv_bar_player;
    }

    /**
     * 获取中间的播放view
     */
    public ImageView getPlayerView() {
        return iv_player;
    }

    /**
     * 隐藏返回键，true隐藏，false为显示
     */
    public PlayerView hideBack(boolean isHide) {
        iv_back.setVisibility(isHide ? View.GONE : View.VISIBLE);
        return this;
    }

    /**
     * 隐藏菜单键，true隐藏，false为显示
     */
    public PlayerView hideMenu(boolean isHide) {
        iv_menu.setVisibility(isHide ? View.GONE : View.VISIBLE);
        return this;
    }

    /**
     * 隐藏分辨率按钮，true隐藏，false为显示
     */
    public PlayerView hideSteam(boolean isHide) {
        tv_steam.setVisibility(isHide ? View.GONE : View.VISIBLE);
        return this;
    }

    /**
     * 隐藏旋转按钮，true隐藏，false为显示
     */
    public PlayerView hideRotation(boolean isHide) {
        iv_rotation.setVisibility(isHide ? View.GONE : View.VISIBLE);
        return this;
    }

    /**
     * 隐藏全屏按钮，true隐藏，false为显示
     */
    public PlayerView hideFullscreen(boolean isHide) {
        iv_fullscreen.setVisibility(isHide ? View.GONE : View.VISIBLE);
        return this;
    }

    /**
     * 隐藏中间播放按钮,ture为隐藏，false为不做隐藏处理，但不是显示
     */
    public PlayerView hideCenterPlayer(boolean isHide) {
        isHideCenterPlayer = isHide;
        iv_player.setVisibility(isHideCenterPlayer ? View.GONE : View.VISIBLE);
        return this;
    }

    /**
     * 是否隐藏topbar，true为隐藏，false为不隐藏，但不一定是显示
     */
    public PlayerView hideHideTopBar(boolean isHide) {
        isHideTopBar = isHide;
        ll_topbar.setVisibility(isHideTopBar ? View.GONE : View.VISIBLE);
        return this;
    }

    /**
     * 是否隐藏bottonbar，true为隐藏，false为不隐藏，但不一定是显示
     */
    public PlayerView hideBottonBar(boolean isHide) {
        isHideBottonBar = isHide;
        ll_bottombar.setVisibility(isHideBottonBar ? View.GONE : View.VISIBLE);
        return this;
    }

    /**
     * 是否隐藏上下bar，true为隐藏，false为不隐藏，但不一定是显示
     */
    public PlayerView hideControlPanl(boolean isHide) {
        hideBottonBar(isHide);
        hideHideTopBar(isHide);
        return this;
    }

    /**
     * 设置自动重连的模式或者重连时间，isAuto true 出错重连，false出错不重连，connectTime重连的时间
     */
    public PlayerView setAutoReConnect(boolean isAuto, int connectTime) {
        this.isAutoReConnect = isAuto;
        this.autoConnectTime = connectTime;
        return this;
    }

    /**
     * 显示或隐藏操作面板
     */
    public PlayerView operatorPanl() {
        isShowControlPanl = !isShowControlPanl;
        if (isShowControlPanl) {
            Log.d(TAG, "operatorPanl: zhang true");
            ll_topbar.setVisibility(isHideTopBar ? View.GONE : View.VISIBLE);
            ll_bottombar.setVisibility(isHideBottonBar ? View.GONE : View.VISIBLE);
            if (isLive) {
                query.id(R.id.app_video_process_panl).invisible();
            } else {
                query.id(R.id.app_video_process_panl).visible();
            }
            if (isOnlyFullScreen || isForbidDoulbeUp) {
                iv_fullscreen.setVisibility(View.GONE);
            } else {
                iv_fullscreen.setVisibility(View.VISIBLE);
            }
            if (onControlPanelVisibilityChangeListener != null) {
                onControlPanelVisibilityChangeListener.change(true);
            }
            /**显示面板的时候再根据状态显示播放按钮*/
            if (status == PlayStateParams.STATE_PLAYING
                    || status == PlayStateParams.STATE_PREPARED
                    || status == PlayStateParams.STATE_PREPARING
                    || status == PlayStateParams.STATE_PAUSED) {
                if (isHideCenterPlayer) {
                    iv_player.setVisibility(View.GONE);
                } else {
                    iv_player.setVisibility(isLive ? View.GONE : View.VISIBLE);
                }
            } else {
                iv_player.setVisibility(View.GONE);
            }
            updatePausePlay();
            mHandler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
            mAutoPlayRunnable.start();
        } else {
            Log.d(TAG, "operatorPanl: zhang false");
            if (isHideTopBar) {
                ll_topbar.setVisibility(View.GONE);
            } else {
                ll_topbar.setVisibility(isForbidHideControlPanl ? View.VISIBLE : View.GONE);

            }
            if (isHideBottonBar) {
                ll_bottombar.setVisibility(View.GONE);
            } else {
                ll_bottombar.setVisibility(isForbidHideControlPanl ? View.VISIBLE : View.GONE);

            }
            if (!isLive && status == PlayStateParams.STATE_PAUSED && !videoView.isPlaying()) {
                if (isHideCenterPlayer) {
                    iv_player.setVisibility(View.GONE);
                } else {
                    /**暂停时一直显示按钮*/
                    iv_player.setVisibility(View.VISIBLE);
                }
            } else {
                iv_player.setVisibility(View.GONE);
            }
            mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
            if (onControlPanelVisibilityChangeListener != null) {
                onControlPanelVisibilityChangeListener.change(false);
            }
            mAutoPlayRunnable.stop();
        }
        return this;
    }


    /**
     * 显示菜单设置
     */
    public PlayerView showMenu() {
        if (!isForbidHideControlPanl) {
            ll_topbar.setVisibility(View.GONE);
            ll_bottombar.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * 进度条和时长显示的方向切换
     */
    public PlayerView toggleProcessDurationOrientation() {
        setProcessDurationOrientation(PlayStateParams.PROCESS_PORTRAIT);
        return this;
    }

    /**
     * 设置显示加载网速或者隐藏
     */
    public PlayerView setShowSpeed(boolean isShow) {
        tv_speed.setVisibility(isShow ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * 设置进度条和时长显示的方向，默认为上下显示，true为上下显示false为左右显示
     */
    public PlayerView setProcessDurationOrientation(int portrait) {
        if (portrait == PlayStateParams.PROCESS_CENTER) {
            query.id(R.id.app_video_currentTime_full).gone();
            query.id(R.id.app_video_endTime_full).gone();
            query.id(R.id.app_video_center).gone();
            query.id(R.id.app_video_lift).visible();
        } else if (portrait == PlayStateParams.PROCESS_LANDSCAPE) {
            query.id(R.id.app_video_currentTime_full).visible();
            query.id(R.id.app_video_endTime_full).visible();
            query.id(R.id.app_video_center).gone();
            query.id(R.id.app_video_lift).gone();
        } else {
            query.id(R.id.app_video_currentTime_full).gone();
            query.id(R.id.app_video_endTime_full).gone();
            query.id(R.id.app_video_center).visible();
            query.id(R.id.app_video_lift).gone();
        }
        return this;
    }


    /**
     * 获取界面方向
     */
    public int getScreenOrientation() {
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    /**
     * ==========================================对外的方法=============================
     */

    /**
     * ==========================================内部方法=============================
     */


    /**
     * 状态改变同步UI
     */
    private void statusChange(int newStatus) {
        if (newStatus == PlayStateParams.STATE_COMPLETED) {
            status = PlayStateParams.STATE_COMPLETED;
            currentPosition = 0;
            hideAll();
            showStatus("播放结束");
        } else if (newStatus == PlayStateParams.STATE_PREPARING
                || newStatus == PlayStateParams.MEDIA_INFO_BUFFERING_START) {
            status = PlayStateParams.STATE_PREPARING;
            /**视频缓冲*/
            hideStatusUI();
            Log.d(TAG, "statusChange: zhang hideStatusUI in statusChange + newStatus: " + newStatus);
            query.id(R.id.app_video_loading).visible();
        } else if (newStatus == PlayStateParams.MEDIA_INFO_VIDEO_RENDERING_START
                || newStatus == PlayStateParams.STATE_PLAYING
                || newStatus == PlayStateParams.STATE_PREPARED
                || newStatus == PlayStateParams.MEDIA_INFO_BUFFERING_END
                || newStatus == PlayStateParams.STATE_PAUSED) {

            if (status == PlayStateParams.STATE_PAUSED) {
                status = PlayStateParams.STATE_PAUSED;
            } else {
                status = PlayStateParams.STATE_PLAYING;
            }
            /**视频缓冲结束后隐藏缩列图*/
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideStatusUI();
                    Log.d(TAG, "run: zhang hideStatusUI in statusChange + postDelayed ");
                    /**显示控制bar*/
                    isShowControlPanl = false;
                    if (!isForbidTouch) {
                        Log.d(TAG, "run: zhang open panel");
                        operatorPanl();
                    }
                    /**延迟0.5秒隐藏视频封面隐藏*/
                    query.id(R.id.ll_bg).gone();
                }
            }, 500);
        } else if (newStatus == PlayStateParams.MEDIA_INFO_VIDEO_INTERRUPT) {
            /**直播停止推流*/
            status = PlayStateParams.STATE_ERROR;
            if (!(isGNetWork &&
                    (NetworkUtils.getNetworkType(mContext) == 4
                            || NetworkUtils.getNetworkType(mContext) == 5
                            || NetworkUtils.getNetworkType(mContext) == 6))) {
                if (isCharge && maxPlaytime < getCurrentPosition()) {
                    //query.id(R.id.app_video_freeTie).visible();
                } else {
                    hideAll();
                    if (isLive) {
                        showStatus("获取不到直播源");
                    } else {
                        showStatus(mActivity.getResources().getString(R.string.small_problem));
                    }
                    /**5秒尝试重连*/
                    if (!isErrorStop && isAutoReConnect) {
                        mHandler.sendEmptyMessageDelayed(MESSAGE_RESTART_PLAY, autoConnectTime);
                    }

                }
            } else {
                hideAllUI();
                query.id(R.id.app_video_netTie).visible();
            }

        } else if (newStatus == PlayStateParams.STATE_ERROR
                || newStatus == PlayStateParams.MEDIA_INFO_UNKNOWN
                || newStatus == PlayStateParams.MEDIA_ERROR_IO
                || newStatus == PlayStateParams.MEDIA_ERROR_MALFORMED
                || newStatus == PlayStateParams.MEDIA_ERROR_UNSUPPORTED
                || newStatus == PlayStateParams.MEDIA_ERROR_TIMED_OUT
                || newStatus == PlayStateParams.MEDIA_ERROR_SERVER_DIED) {
            status = PlayStateParams.STATE_ERROR;
            if (!(isGNetWork && (NetworkUtils.getNetworkType(mContext) == 4 || NetworkUtils.getNetworkType(mContext) == 5 || NetworkUtils.getNetworkType(mContext) == 6))) {
                if (isCharge && maxPlaytime < getCurrentPosition()) {
                    //query.id(R.id.app_video_freeTie).visible();
                } else {
                    hideStatusUI();
                    Log.d(TAG, "statusChange: zhang hideStatusUI in statusChange + newStatus: " + newStatus);
                    if (isLive) {
                        showStatus(mActivity.getResources().getString(R.string.small_problem));
                    } else {
                        showStatus(mActivity.getResources().getString(R.string.small_problem));
                    }
                    /**5秒尝试重连*/
                    if (!isErrorStop && isAutoReConnect) {
                        mHandler.sendEmptyMessageDelayed(MESSAGE_RESTART_PLAY, autoConnectTime);
                    }
                }
            } else {
                hideAllUI();
                query.id(R.id.app_video_netTie).visible();
            }
        }
    }

    /**
     * 显示视频播放状态提示
     */
    private void showStatus(String statusText) {
        query.id(R.id.app_video_replay).visible();
        query.id(R.id.app_video_status_text).text(statusText);
    }

    /**
     * 界面方向改变是刷新界面
     */
    private void doOnConfigurationChanged(final boolean portrait) {
        if (videoView != null && !isOnlyFullScreen) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    tryFullScreen(!portrait);
                    if (portrait) {
                        query.id(R.id.app_video_box).height(initHeight, false);
                    } else {
                        int heightPixels = mActivity.getResources().getDisplayMetrics().heightPixels;
                        int widthPixels = mActivity.getResources().getDisplayMetrics().widthPixels;
                        query.id(R.id.app_video_box).height(Math.min(heightPixels, widthPixels), false);
                    }
                    updateFullScreenButton();
                }
            });
            orientationEventListener.enable();
        }
    }


    /**
     * 设置界面方向
     */
    private void setFullScreen(boolean fullScreen) {
        if (mActivity != null) {

            WindowManager.LayoutParams attrs = mActivity.getWindow().getAttributes();
            if (fullScreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                mActivity.getWindow().setAttributes(attrs);
                mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mActivity.getWindow().setAttributes(attrs);
                mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
            toggleProcessDurationOrientation();
        }

    }

    /**
     * 设置界面方向带隐藏actionbar
     */
    private void tryFullScreen(boolean fullScreen) {
        if (mActivity instanceof AppCompatActivity) {
            ActionBar supportActionBar = ((AppCompatActivity) mActivity).getSupportActionBar();
            if (supportActionBar != null) {
                if (fullScreen) {
                    supportActionBar.hide();
                } else {
                    supportActionBar.show();
                }
            }
        }
        setFullScreen(fullScreen);
    }


    /**
     * 隐藏状态界面
     */
    private void hideStatusUI() {
        iv_player.setVisibility(View.GONE);
        query.id(R.id.app_video_replay).gone();
        query.id(R.id.app_video_netTie).gone();
        //query.id(R.id.app_video_freeTie).gone();
        if(videoView.isPlaying()){
            query.id(R.id.app_video_loading).gone();
            Log.d(TAG, "hideStatusUI: zhang gone in hide");
        }
            
        if (onControlPanelVisibilityChangeListener != null) {
            onControlPanelVisibilityChangeListener.change(false);
        }
    }

    /**
     * 隐藏所有界面
     */
    private void hideAll() {
        if (!isForbidHideControlPanl) {
            ll_topbar.setVisibility(View.GONE);
            ll_bottombar.setVisibility(View.GONE);
        }
        hideStatusUI();
        Log.d(TAG, "hideAll: zhang hideStatusUI in hideAll");
    }

    /**
     * 显示分辨率列表
     */
    private void showStreamSelectView() {
        if (!isForbidHideControlPanl) {
            ll_topbar.setVisibility(View.GONE);
            ll_bottombar.setVisibility(View.GONE);
        }
    }



    /**
     * 手势结束
     */
    private void endGesture() {
        volume = -1;
        if (newPosition >= 0) {
            mHandler.removeMessages(MESSAGE_SEEK_NEW_POSITION);
            mHandler.sendEmptyMessage(MESSAGE_SEEK_NEW_POSITION);
        } else {
            /**什么都不做(do nothing)*/
        }
        mHandler.removeMessages(MESSAGE_HIDE_CENTER_BOX);
        mHandler.sendEmptyMessageDelayed(MESSAGE_HIDE_CENTER_BOX, 500);
        if (mAutoPlayRunnable != null) {
            mAutoPlayRunnable.start();
        }

    }

    /**
     * 同步进度
     */
    private long syncProgress() {
        if (isDragging) {
            return 0;
        }
        long position = videoView.getCurrentPosition();
        long duration = videoView.getDuration();
        if (seekBar != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                seekBar.setProgress((int) pos);
            }
            int percent = videoView.getBufferPercentage();
            seekBar.setSecondaryProgress(percent * 10);
        }

        if (isCharge && maxPlaytime + 1000 < getCurrentPosition()) {
           /* query.id(R.id.app_video_freeTie).visible();
            pausePlay();*/
        } else {
            query.id(R.id.app_video_currentTime).text(generateTime(position));
            query.id(R.id.app_video_currentTime_full).text(generateTime(position));
            query.id(R.id.app_video_currentTime_left).text(generateTime(position));
            query.id(R.id.app_video_endTime).text(generateTime(duration));
            query.id(R.id.app_video_endTime_full).text(generateTime(duration));
            query.id(R.id.app_video_endTime_left).text(generateTime(duration));
        }
        return position;
    }

    /**
     * 时长格式化显示
     */
    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * 下载速度格式化显示
     */
    private String getFormatSize(int size) {
        long fileSize = (long) size;
        String showSize = "";
        if (fileSize >= 0 && fileSize < 1024) {
            showSize = fileSize + "Kb/s";
        } else if (fileSize >= 1024 && fileSize < (1024 * 1024)) {
            showSize = Long.toString(fileSize / 1024) + "KB/s";
        } else if (fileSize >= (1024 * 1024) && fileSize < (1024 * 1024 * 1024)) {
            showSize = Long.toString(fileSize / (1024 * 1024)) + "MB/s";
        }
        return showSize;
    }


    /**
     * 更新播放、暂停和停止按钮
     */
    private void updatePausePlay() {
        if (videoView.isPlaying()) {
            if (isLive) {
                iv_bar_player.setImageResource(R.drawable.simple_player_stop_white_24dp);
            } else {
                iv_bar_player.setImageResource(R.drawable.simple_player_icon_media_pause);
                iv_player.setImageResource(R.drawable.simple_player_center_pause);
            }
        } else {
            iv_bar_player.setImageResource(R.drawable.simple_player_arrow_white_24dp);
            iv_player.setImageResource(R.drawable.simple_player_center_play);
        }
    }

    /**
     * 更新全屏和半屏按钮
     */
    private void updateFullScreenButton() {
        if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            iv_fullscreen.setImageResource(R.drawable.simple_player_icon_fullscreen_shrink);
        } else {
            iv_fullscreen.setImageResource(R.drawable.simple_player_icon_fullscreen_stretch);
        }
    }





    /**
     * ==========================================内部方法=============================
     */


    /**
     * ==========================================内部类=============================
     */

    /**
     * 收起控制面板轮询，默认5秒无操作，收起控制面板，
     */
    private class AutoPlayRunnable implements Runnable {
        private int AUTO_PLAY_INTERVAL = 3000;
        private boolean mShouldAutoPlay;

        /**
         * 五秒无操作，收起控制面板
         */
        public AutoPlayRunnable() {
            mShouldAutoPlay = false;
        }

        public void start() {
            /*if (!mShouldAutoPlay) {
                mShouldAutoPlay = true;
                mHandler.removeCallbacks(this);
                mHandler.postDelayed(this, AUTO_PLAY_INTERVAL);
                Log.d(TAG, "start: zhang run start");
            }*/
            mShouldAutoPlay = true;
            mHandler.removeCallbacks(this);
            mHandler.postDelayed(this, AUTO_PLAY_INTERVAL);
            Log.d(TAG, "start: zhang run start");
        }

        public void stop() {
            if (mShouldAutoPlay) {
                mHandler.removeCallbacks(this);
                mShouldAutoPlay = false;
            }
        }

        @Override
        public void run() {
            Log.d(TAG, "run: zhang in the run");
            hideAllUI();
            if (mShouldAutoPlay) {
                mHandler.removeCallbacks(this);
                if (!isForbidTouch && !isShowControlPanl) {
                    operatorPanl();
                    Log.d(TAG, "run: zhang there there");
                }
            }

        }
    }

    /**
     * 播放器的手势监听
     */
    public class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {

        /**
         * 是否是按下的标识，默认为其他动作，true为按下标识，false为其他动作
         */
        private boolean isDownTouch;
        /**
         * 是否横向滑动，默认为纵向滑动，true为横向滑动，false为纵向滑动
         */
        private boolean isLandscape;

        /**
         * 双击
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            /**视频视窗双击事件*/
            if (!isForbidTouch && !isOnlyFullScreen && !isForbidDoulbeUp) {
                //toggleFullScreen();
            }
            return true;
        }

        /**
         * 按下
         */
        @Override
        public boolean onDown(MotionEvent e) {
            isDownTouch = true;
            return super.onDown(e);
        }


        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!isForbidTouch) {
                float mOldX = e1.getX(), mOldY = e1.getY();
                float deltaY = mOldY - e2.getY();
                float deltaX = mOldX - e2.getX();
                if (isDownTouch) {
                    isLandscape = Math.abs(distanceX) >= Math.abs(distanceY);
                    isDownTouch = false;
                }

                return false;
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        /**
         * 单击
         */
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            /**视频视窗单击事件*/
            if (!isForbidTouch) {
                operatorPanl();
            }
            return true;
        }
    }
    /**
     * ==========================================内部方法=============================
     */

    public void setLoop(boolean loop) {
        isLoop = loop;
    }
}
