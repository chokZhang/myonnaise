package com.mengyuan1998.finger_dancing.Utilities.baidu_tts;

import android.util.Log;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizerListener;

import static android.content.ContentValues.TAG;

/**
 * Created by Scarecrow on 2018/4/22.
 * 在进行语音合成时 从这里进行回调
 */

public class TTSMassageListener implements SpeechSynthesizerListener {
    @Override
    public void onSynthesizeStart(String var1) {
        Log.d(TAG, "onSynthesizeStart: 语音合成开始");

    }

    public void onSynthesizeDataArrived(String var1, byte[] var2, int var3) {
        Log.d(TAG, "onSynthesizeDataArrived: 语音合成数据已获得");
    }

    public void onSynthesizeFinish(String var1) {
        Log.d(TAG, "onSynthesizeFinish: 语音合成完毕");

    }

    public void onSpeechStart(String var1) {
        Log.d(TAG, "onSpeechStart: 语音合成开始");

    }

    public void onSpeechProgressChanged(String var1, int var2) {

    }

    public void onSpeechFinish(String var1) {
        Log.d(TAG, "onSpeechFinish: 语音合成结束");

    }

    public void onError(String var1, SpeechError var2) {
        Log.e(TAG, "onError: 语音合成出错 " + var2.toString());
    }
}
