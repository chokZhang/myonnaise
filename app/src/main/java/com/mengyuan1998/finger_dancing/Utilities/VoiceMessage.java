package com.mengyuan1998.finger_dancing.Utilities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Scarecrow on 2018/2/8.
 * 一开始语音消息是没有文字的  需要在后续调用转换
 */

public class VoiceMessage extends ConversationMessage {

    private String voice_file_path;

    private StringBuilder result_buffer = new StringBuilder();

    /**
     * 最大等待时间， 单位ms
     */
    private int maxWaitTime = 500;
    /**
     * 每次等待时间
     */
    private static final int perWaitTime = 100;
    /**
     * 出现异常时最多重复次数
     */
    private static final int maxQueueTimes = 3;



    public VoiceMessage(int msg_id, String voice_file_path) {
        super(msg_id, VOICE, "正在识别语音");
        this.voice_file_path = voice_file_path;
        Log.d(TAG, "VoiceMessage: building new msg voice path: " + voice_file_path);
        transVoice2Text();
    }


    private void transVoice2Text() {
        // 将这个函数放在asynctask里面用
        // 返回String后修改UI即可
        //在这里调用科大讯飞的api语音转文字

        //16bit，16000hz，单声道的pcm,wav文件
        try {
            new Thread(trans_task).start();
        } catch (Exception ee) {
            Log.e(TAG, "transVoice2Text: " + ee);
            ee.printStackTrace();
        }

    }

    private Runnable trans_task = new Runnable() {
        @Override
        public void run() {
            try {
                recognizePcmfileByte();
            } catch (Exception ee) {
                Log.e(TAG, "transVoice2Text: " + ee);
                ee.printStackTrace();
            }

        }
    };

    /**
     * 如果直接从音频文件识别，需要模拟真实的音速，防止音频队列的堵塞
     *
     * @throws InterruptedException sleep 方法
     */
    private void recognizePcmfileByte()  throws Exception{
        if (SpeechRecognizer.getRecognizer() == null)
            Log.e(TAG, "recognizePcmfileByte: recognize is null" );

        // 1、读取音频文件
        FileInputStream fis = null;
        byte[] voice_buffer;
        try {
            fis = new FileInputStream(new File(voice_file_path));
            voice_buffer = new byte[fis.available()];
            fis.read(voice_buffer);
        } catch (Exception e) {
            Log.e(TAG, "recognizePcmfileByte: failed to open voice file ", e);
            e.printStackTrace();
            return;
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                    fis = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 2、音频流听写
        if (0 == voice_buffer.length) {
            result_buffer.append("no audio avaible!");
        } else {
            //解析之前将存出结果置为空
            result_buffer.setLength(0);
            SpeechRecognizer recognizer = SpeechRecognizer.getRecognizer();
            recognizer.setParameter(SpeechConstant.DOMAIN, "iat");
            recognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            recognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
            //写音频流时，文件是应用层已有的，不必再保存
//			recognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH,
//					"./iflytek.pcm");
            recognizer.setParameter(SpeechConstant.RESULT_TYPE, "plain");
            recognizer.startListening(recListener);
            ArrayList<byte[]> buffers = splitBuffer(voice_buffer,
                    voice_buffer.length, 4800);
            for (int i = 0; i < buffers.size(); i++) {
                // 每次写入msc数据4.8K,相当150ms录音数据
                recognizer.writeAudio(buffers.get(i), 0, buffers.get(i).length);
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "recognizePcmfileByte: get out");
            recognizer.stopListening();
            Log.d(TAG, "recognizePcmfileByte: finish");

            // 在原有的代码基础上主要添加了这个while代码等待音频解析完成，recognizer.isListening()返回true，
            // 说明解析工作还在进行
            /*while(recognizer.isListening()) {
                if(maxWaitTime < 0) {
                    result_buffer.setLength(0);
                    Log.d(TAG, "recognizePcmfileByte: 解析超时！");
                    break;
                }
                Thread.sleep(perWaitTime);
                maxWaitTime -= perWaitTime;
            }*/
        }
    }

    /**
     * 将字节缓冲区按照固定大小进行分割成数组
     *
     * @param buffer 缓冲区
     * @param length 缓冲区大小
     * @param spsize 切割块大小
     * @return
     */
    private ArrayList<byte[]> splitBuffer(byte[] buffer, int length, int spsize) {
        ArrayList<byte[]> array = new ArrayList<>();
        if (spsize <= 0 || length <= 0 || buffer == null
                || buffer.length < length)
            return array;
        int size = 0;
        while (size < length) {
            int left = length - size;
            if (spsize < left) {
                byte[] sdata = new byte[spsize];
                System.arraycopy(buffer, size, sdata, 0, spsize);
                array.add(sdata);
                size += spsize;
            } else {
                byte[] sdata = new byte[left];
                System.arraycopy(buffer, size, sdata, 0, left);
                array.add(sdata);
                size += left;
            }
        }
        return array;
    }

    private RecognizerListener recListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        @Override
        public void onBeginOfSpeech() {
            Log.d(TAG, "onBeginOfSpeech: ");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech: ");

        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            result_buffer.append(recognizerResult.getResultString());
            Log.d(TAG, "onResult: " + recognizerResult.getResultString());
            finish_handler.obtainMessage(0, result_buffer.toString())
                    .sendToTarget();

        }

        @Override
        public void onError(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };


    @SuppressLint("HandlerLeak")
    private Handler finish_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            text_content = (String) msg.obj;
            Log.d(TAG, "handleMessage: " + text_content);
            MessageManager.getInstance()
                    .noticeAllTargetMsgChange();
        }
    };


}

