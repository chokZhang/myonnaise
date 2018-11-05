package com.mengyuan1998.finger_dancing.Utilities;


import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

/**
 * Created by Scarecrow on 2018/2/18.
 */

public class VoiceRecorder {
    private String voice_file_name;
    private MediaRecorder recorder;
    private String fileFolder = Environment.getExternalStorageDirectory()
            .getPath() + "/sign_recognize_voice_cache/";

    private boolean is_recording;

    public VoiceRecorder() {
        recorder = new MediaRecorder();
        is_recording = false;
    }


    private String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        return formatter.format(curDate);
    }

    public void prepare() {
        //准备音频文件路径
        voice_file_name = getCurrentDate();
        File file = new File(fileFolder);
        if (!file.exists()) {
            file.mkdir();
        }
        voice_file_name = "a" + getCurrentDate();
        recorder = new MediaRecorder();
        String file_full_path = fileFolder + voice_file_name + ".amr";
        recorder.setOutputFile(file_full_path);
        Log.d(TAG, "prepare: voice_path: " + file_full_path);
        recorder.setAudioSamplingRate(1600);
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);// 设置MediaRecorder的音频源为麦克风
        recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);// 设置MediaRecorder录制的音频格式
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 设置MediaRecorder录制音频的编码为amr

    }

    public void start() {
        if (voice_file_name == null)
            return;
        try {
            recorder.prepare();
            recorder.start();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    public String complete() {
        recorder.stop();
        return voice_file_name;
    }

    public void stop() {
        recorder.stop();
        File file = new File(fileFolder + voice_file_name + ".amr");
        file.delete();
        voice_file_name = null;
    }

    public double get_voice_amplitude() {
        return recorder.getMaxAmplitude();
    }

    public void quit() {
        recorder.release();
    }

}
