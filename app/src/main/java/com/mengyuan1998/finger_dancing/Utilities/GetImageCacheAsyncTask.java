package com.mengyuan1998.finger_dancing.Utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.mengyuan1998.finger_dancing.Utilities.info_rv_adapter_item.BaseItem;
import com.mengyuan1998.finger_dancing.fragment.CommunityFragment;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GetImageCacheAsyncTask extends AsyncTask<String, Void, File> {
    private static final String TAG = "getImageCacheAsyncTask";
    private final Context context;
    private File videoFile;
    private String info;
    public static boolean getImgState = false;

    public GetImageCacheAsyncTask(Context context, File file, String info) {
        this.context = context;
        this.videoFile = file;
        this.info = info;
    }

    @Override
    protected File doInBackground(String... params) {
        String imgUrl =  params[0];
        try {
            getImgState = false;
            FutureTarget<File> target = Glide.with(context)
                    .asFile()
                    .load(imgUrl)
                    .submit();
            final File imageFile = target.get();
            Log.d(TAG, "doInBackground: path:" + imageFile.getAbsolutePath());
            return imageFile;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(File result) {
        if (result == null) {
            Log.d(TAG, "onPostExecute: get Img failed");
            return;
        }
        getImgState = true;


    }


}

