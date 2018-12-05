package com.mengyuan1998.finger_dancing.Utilities;

import android.content.Context;
import android.util.AttributeSet;

import cn.jzvd.JZMediaManager;
import cn.jzvd.JZVideoPlayerStandard;

public class VedioAutoPlayer extends JZVideoPlayerStandard {

    public VedioAutoPlayer (Context context) {
        super(context);
    }

    public VedioAutoPlayer (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        //重播
        /*try{
            seekToInAdvance = 20000;
            JZMediaManager.instance().mediaPlayer.seekTo(10000);
        }catch (Exception e){
            e.printStackTrace();
            startVideo();
        }*/
        startVideo();
    }

}
