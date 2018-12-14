package com.mengyuan1998.finger_dancing.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class GetImgUtils {

    private static final String TAG = "GetImgUtils";

    private static File imgFile;

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

    /**
     * 存储视频的缩略图
     */
    public static boolean saveBitmapToFile(Context context, Bitmap bitmap){
        imgFile = new File(context.getExternalCacheDir() + "/vedios", "thumbnail.jpg");
        try{
            if(imgFile.exists()){
                imgFile.delete();
            }
            if(!imgFile.getParentFile().exists()){
                imgFile.getParentFile().mkdir();
            }
            imgFile.createNewFile();

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imgFile));

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            bos.flush();
            bos.close();

        }catch (Exception e){
            Log.e(TAG, "onClick: err happend in takeVedio");
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
