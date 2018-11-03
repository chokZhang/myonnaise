package it.ncorti.emgvisualizer.ui.recognize;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.TextView;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import it.ncorti.emgvisualizer.R;
import it.ncorti.emgvisualizer.recognize.InFileStream;
import it.ncorti.emgvisualizer.recognize.MyLogger;

/**
 * Created by fujiayi on 2017/6/20.
 */

public abstract class ActivityCommon extends AppCompatActivity {
    protected TextView txtLog;
    protected Button btn;
    protected Button setting;
    protected TextView txtResult;

    protected Handler handler;

    protected final int layout;

    public ActivityCommon() {
        this( R.layout.activity_vocie_recognize);
    }

    public ActivityCommon( int layout) {
        super();
        this.layout = layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setStrictMode();
        InFileStream.setContext(this);
        setContentView(layout);
        initView();
        handler = new Handler() {

            /*
             * @param msg
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handleMsg(msg);
            }

        };
        MyLogger.setHandler(handler);
        initPermission();
    }

    protected void handleMsg(Message msg) {
        if (txtLog != null && msg.obj != null) {
            txtLog.append(msg.obj.toString() + "\n");
        }
    }

    protected void initView() {
        txtResult = (TextView) findViewById(R.id.text);
        //txtLog = (TextView) findViewById(R.id.txtLog);
        btn = (Button) findViewById(R.id.button_rec);
        //setting = (Button) findViewById(R.id.setting);
        String descText = "";
        //txtLog.setText(descText);
        //txtLog.append("\n");
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String[] permissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.

            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }

    private void setStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

    }
}
