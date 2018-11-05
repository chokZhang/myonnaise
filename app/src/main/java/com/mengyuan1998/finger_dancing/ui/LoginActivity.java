package com.mengyuan1998.finger_dancing.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mengyuan1998.finger_dancing.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView weChatLogin;
    private ImageView toWeChat;
    private Button login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //TODO 补全电话登陆的逻辑

        //微信登陆
        weChatLogin = findViewById(R.id.login_wechat);
        toWeChat = findViewById(R.id.to_login_wechat);
        login_button = findViewById(R.id.login_button);

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
                Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                startActivity(intent);
            }
            default: break;
        }
    }
}
