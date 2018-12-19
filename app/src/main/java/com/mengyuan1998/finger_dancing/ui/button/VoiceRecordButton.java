package com.mengyuan1998.finger_dancing.ui.button;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.mengyuan1998.finger_dancing.R;
import com.mengyuan1998.finger_dancing.Utilities.AudioRecorderConfiguration;
import com.mengyuan1998.finger_dancing.Utilities.ExtAudioRecorder;
import com.mengyuan1998.finger_dancing.Utilities.MessageManager;
import com.mengyuan1998.finger_dancing.ui.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.content.ContentValues.TAG;

/**
 * Created by Scarecrow on 2018/2/18.
 * 仿微信可通过手势控制的音频录制按钮
 */

public class VoiceRecordButton extends AppCompatImageView {

    private static final int RECORD_ON = -5,
            RECORD_OFF = -9;

    private Thread record_timer_thread;

    private double recording_time = 0.0,
            voice_value = 0.0;
    private int recorder_state = RECORD_OFF;
    private float init_y;
    private boolean is_cancel = false;
    private Dialog recorder_dialog;
    private ImageView dialog_img;
    private Context mContext;
    private final int REQUEST_ALL_PERMISSION = 1;
    private PermissionListener mPermissionListener;
    //是否有权限足以使用
    private boolean isReady = true;

    private String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.RECORD_AUDIO,
    };

    private List<String> mPermissionList = new ArrayList<>();


    @SuppressLint("HandlerLeak")
    private Handler recorder_ui_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            double voice_value = (double) msg.obj;
            setRecorderValueImg(voice_value);
        }
    };

    private ExtAudioRecorder recorder = new ExtAudioRecorder(AudioRecorderConfiguration
            .createDefaultSetting()
            .handler(recorder_ui_handler)
            .build());
    private TextView dialog_text;
    /**
     * 计时器 用于记录录音长度
     */
    private Runnable timer = new Runnable() {
        @Override
        public void run() {
            recording_time = 0;

            while (recorder_state == RECORD_ON &&
                    !Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(100);
//                    Log.d(TAG, "timer: " + recording_time);
                    recording_time += 0.1;
                } catch (InterruptedException e) {
                }
            }
        }
    };


    public VoiceRecordButton(Context context) {

        super(context);
    }

    public VoiceRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public VoiceRecordButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        requestPermission();
        if(!isReady){
            Toast.makeText(mContext, "权限不足", Toast.LENGTH_SHORT).show();
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 按下按钮
                if (recorder_state == RECORD_OFF) {
                    recorder_state = RECORD_ON;
                    record_timer_thread = new Thread(timer);
                    record_timer_thread.start();

                    Log.d(TAG, "onTouchEvent: recorder start record");
                    recorder.prepare();
                    recorder.start();
                    init_y = event.getY();
                    updateVoiceDialog(false);
                }

                break;
            case MotionEvent.ACTION_MOVE: // 滑动手指
                float moveY = event.getY();
                if (init_y - moveY > 80) {
                    is_cancel = true;
                    updateVoiceDialog(true);
                }
                if (init_y - moveY < 20) {
                    is_cancel = false;
                    updateVoiceDialog(false);
                }
                break;
            case MotionEvent.ACTION_UP: // 松开手指
                recorder_state = RECORD_OFF;
                recorder_dialog.dismiss();
                record_timer_thread.interrupt();
                if (is_cancel) {
                    recorder.stop();
                } else {
                    if (recording_time <= 1) {
                        Log.d(TAG, "onTouchEvent: " + recording_time);
                        Toast.makeText(getContext(), "语音时间太短", Toast.LENGTH_LONG)
                                .show();
                        recorder.stop();
                    } else {
                        String voice_file_path = recorder.complete();
                        if (voice_file_path.charAt(0) != '/')
                            Log.e(TAG, "onTouchEvent: error in complete record: "
                                    + voice_file_path);
                        else
                            MessageManager.getInstance()
                                    .buildVoiceMessage(voice_file_path);
                    }

                }
                recorder.reset();
                break;
        }
        return true;
    }

    public void requestPermission(){
        //小于6.0不需要权限动态申请
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return;
        }

        for(int i = 0; i < permissions.length; i++){
            if(ContextCompat.checkSelfPermission(mContext, permissions[i]) != PackageManager.PERMISSION_GRANTED){
                mPermissionList.add(permissions[i]);
            }
        }

        if(mPermissionList.size() != 0){
            String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);
            mPermissionListener.requestPermission(permissions);
            isReady = false;
        }
    }

    private void updateVoiceDialog(boolean is_can_cancel) {
        if (recorder_dialog == null) {
            recorder_dialog = new Dialog(getContext(), R.style.Theme_AppCompat_Dialog);
            recorder_dialog.setContentView(R.layout.recorder_dialog);
            dialog_img = recorder_dialog.findViewById(R.id.record_dialog_img);
            dialog_text = recorder_dialog.findViewById(R.id.record_dialog_txt);
        }
        if (is_can_cancel) {
            dialog_img.setImageResource(R.mipmap.record_cancel);
            dialog_text.setText("松开手指可取消录音");
//            this.setText("松开手指 取消录音");
        } else {
            dialog_img.setImageResource(R.mipmap.record_animate_01);
            dialog_text.setText("向上滑动可取消录音");
//            this.setText("松开手指 完成录音");

        }
        if (!recorder_dialog.isShowing())
            recorder_dialog.show();

    }

    private void setRecorderValueImg(double voiceValue) {
        voiceValue *= 10;
        if (voiceValue < 600.0) {
            dialog_img.setImageResource(R.mipmap.record_animate_01);
        } else if (voiceValue > 600.0 && voiceValue < 2000.0) {
            dialog_img.setImageResource(R.mipmap.record_animate_02);
        } else if (voiceValue > 2000.0 && voiceValue < 3500.0) {
            dialog_img.setImageResource(R.mipmap.record_animate_03);
        } else if (voiceValue > 3500.0 ) {
            dialog_img.setImageResource(R.mipmap.record_animate_04);
        }
    }

    public void releaseMediaResource() {
        if (recorder != null)
            recorder.release();
    }

    public interface PermissionListener{
        void requestPermission(String[] permissions);
    }

    public void setmPermissionListener(PermissionListener mPermissionListener) {
        this.mPermissionListener = mPermissionListener;
    }
}
