package com.mengyuan1998.finger_dancing.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mengyuan1998.finger_dancing.R;
import com.mengyuan1998.finger_dancing.Utilities.HttpUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private TextView weChatLogin;
    private ImageView toWeChat;
    private EditText et_account;
    private EditText et_password;
    private Button login_button;
    private final String loginUrl = "http://39.96.24.179/login";
    private final int REQUEST_ALL_PERMISSION = 0;

    private String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private List<String> mPermissionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*//系统状态栏透明
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);*/

        requestPermission();

        //TODO 补全电话登陆的逻辑

        //微信登陆
        weChatLogin = findViewById(R.id.login_wechat);
        toWeChat = findViewById(R.id.to_login_wechat);
        login_button = findViewById(R.id.login_button);
        et_account = findViewById(R.id.et_account);
        et_password = findViewById(R.id.et_password);

        //注册监听器
        weChatLogin.setOnClickListener(this);
        toWeChat.setOnClickListener(this);
        login_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.to_login_wechat :
            case R.id.login_wechat : {
                //TODO 微信登陆

                break;
            }
            case R.id.login_button : {
                /*Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                startActivity(intent);*/

                String name = et_account.getText().toString();
                String pass = et_password.getText().toString();

                login(name, pass);
            }
            default: break;
        }
    }

    public void requestPermission(){
        //小于6.0不需要权限动态申请
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return;
        }

        for(int i = 0; i < permissions.length; i++){
            if(ContextCompat.checkSelfPermission(LoginActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED){
                mPermissionList.add(permissions[i]);
            }
        }

        if(mPermissionList.size() != 0){
            String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);
            ActivityCompat.requestPermissions(LoginActivity.this, permissions, REQUEST_ALL_PERMISSION);
        }
    }

    public void login(String name, String pass){
        HttpUtil.login(loginUrl, name, pass , new HttpUtil.ProgressListener() {
            @Override
            public void onProgress(long currentBytes, long contentLength, boolean done) {
                Log.i(TAG, "currentBytes==" + currentBytes + "==contentLength==" + contentLength + "==done==" + done);


            }
        },new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.toString());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    String result = response.body().string();
                    Log.i(TAG, "result===" + result);
                    Log.d(TAG, "onResponse: code " + response.code());
                    if(response.code() == 200){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                    else if(response.code() == 401){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "账号密码错误", Toast.LENGTH_SHORT).show();
                                et_password.setText("");
                            }
                        });
                    }
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "网络状态异常", Toast.LENGTH_SHORT).show();
                                et_password.setText("");
                            }
                        });
                    }
                    //Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                    //startActivity(intent);
                }
            }
        });
    }
}
