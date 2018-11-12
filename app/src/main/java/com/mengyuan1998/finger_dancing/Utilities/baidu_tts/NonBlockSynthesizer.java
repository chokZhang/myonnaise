package com.mengyuan1998.finger_dancing.Utilities.baidu_tts;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

/**
 * 在新线程中调用initTTs方法。防止UI阻塞
 * MySynthesizer 的初始化和退出需要一个线程去进行 不能放在主线程中进行
 * 这个类通过继承对这样一个过程进行包装
 * 但是合成发声等请求仍然可以通过基类调用完成
 */

public class NonBlockSynthesizer extends MySynthesizer {

    private static final int INIT = 1;

    private static final int RELEASE = 11;
    private static final String TAG = "NonBlockSynthesizer";
    private HandlerThread hThread;
    private Handler tHandler;


    public NonBlockSynthesizer(Context context, InitConfig initConfig) {
        super(context);
        initThread();
        runInHandlerThread(INIT, initConfig);
    }

    /**
     * 开启一个新的线程 在线程中对Synthesizer进行初始化
     */
    private void initThread() {
        hThread = new HandlerThread("NonBlockSynthesizer-thread");
        hThread.start();
        tHandler = new Handler(hThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case INIT:
                        InitConfig config = (InitConfig) msg.obj;
                        boolean isSuccess = init(config);
                        if (isSuccess) {
                            // speak("初始化成功");
                            Log.d(TAG, "NonBlockSynthesizer 初始化成功");
                        } else {
                            Log.d(TAG, "合成引擎初始化失败, 请查看日志");
                        }
                        break;
                    case RELEASE:
                        NonBlockSynthesizer.super.release();
                        if (Build.VERSION.SDK_INT < 18) {
                            getLooper().quit();
                        }
                        break;
                    default:
                        break;
                }

            }
        };
    }

    @Override
    public void release() {
        runInHandlerThread(RELEASE);
        if (Build.VERSION.SDK_INT >= 18) {
            hThread.quitSafely();
        }
    }


    private void runInHandlerThread(int action) {
        runInHandlerThread(action, null);
    }

    private void runInHandlerThread(int action, Object obj) {
        Message msg = Message.obtain();
        msg.what = action;
        msg.obj = obj;
        tHandler.sendMessage(msg);
    }

}
