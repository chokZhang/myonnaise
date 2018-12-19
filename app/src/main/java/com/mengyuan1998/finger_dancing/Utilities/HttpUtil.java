package com.mengyuan1998.finger_dancing.Utilities;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {

    private static final String TAG = "HttpUtil";

    private static ConcurrentHashMap<String, List<Cookie>> cookieStore = new ConcurrentHashMap<>();

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(100,TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .cookieJar(new CookieJar(){
                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    //保存cookie
                    cookieStore.put(url.host(), cookies);
                    for(Cookie cookie : cookies){
                        Log.d(TAG, "saveFromResponse: cookie: " + cookie);
                    }
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    //加载新的cookies
                    List<Cookie> cookies = cookieStore.get(url.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();

                }
            })

            .build();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");

    public static void postFile(String url, String info, final ProgressListener listener, okhttp3.Callback callback, File...files){

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        //Log.i("huang","files[0].getName()=="+files[0].getName());
        //第一个参数要与Servlet中的一致
        builder.addFormDataPart("video",files[0].getName(), RequestBody.create(MediaType.parse("application/octet-stream"),files[0]));
        //builder.addFormDataPart("img", files[1].getName(), RequestBody.create(MediaType.parse("application/octet-stream"),files[1]));
        builder.addFormDataPart("info", info);
        //builder.addFormDataPart("video", "abc");

        MultipartBody multipartBody = builder.build();

        Request request  = new Request.Builder().url(url).post(new ProgressRequestBody(multipartBody,listener)).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void login(String url, String name, String pass, final ProgressListener listener, okhttp3.Callback callback){
        Log.d(TAG, "login: url " + url);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        //Log.i("huang","files[0].getName()=="+files[0].getName());
        Log.d(TAG, "login: name: " + name + "pass: " +pass);
        //第一个参数要与Servlet中的一致
        builder.addFormDataPart("username", name);
        builder.addFormDataPart("password", pass);

        MultipartBody multipartBody = builder.build();
        Request request  = new Request.Builder().url(url).post(new ProgressRequestBody(multipartBody,listener)).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void getInfoUpate(String url, okhttp3.Callback callback){
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public interface ProgressListener {

        void onProgress(long currentBytes, long contentLength, boolean done);

    }

}


