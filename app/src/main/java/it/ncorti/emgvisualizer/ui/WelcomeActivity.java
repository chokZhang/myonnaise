package it.ncorti.emgvisualizer.ui;

import androidx.appcompat.app.AppCompatActivity;
import it.ncorti.emgvisualizer.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "WelcomeActivity";

    private ImageView img_communicate;
    private ImageView img_voiceRecognize;
    private Button button_setting;
    private Button button_about;
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //如果没有登录需跳转到登陆页面
        mContext = getApplicationContext();
        mSharedPreferences = mContext.getSharedPreferences("test", Context.MODE_PRIVATE);

        int state = mSharedPreferences.getInt("loadingState", 0);

        if(state == 0){
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        }

        setContentView(R.layout.activity_welcome);

        button_about = findViewById(R.id.button_about);
        button_setting = findViewById(R.id.button_setting);
        img_communicate = findViewById(R.id.startCommunicate_img);
        img_voiceRecognize = findViewById(R.id.recognize_img);

        //注册点击事件监听器
        button_setting.setOnClickListener(this);
        button_about.setOnClickListener(this);
        img_voiceRecognize.setOnClickListener(this);
        img_communicate.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_about : {
                //TODO 弹出关于界面

                break;
            }
            case R.id.button_setting : {
                //TODO 弹出设置界面

                break;
            }
            case R.id.startCommunicate_img : {
                //弹出交流页面
                Log.d(TAG, "onClick: click startCommunicate");
                Intent intent = new Intent(WelcomeActivity.this, CommunicateActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.recognize_img : {
                //切换到语音识别界面
                Log.d(TAG, "onClick: click voiceRecognize");
                Intent intent = new Intent(WelcomeActivity.this, VocieRecognizeActivity.class);
                startActivity(intent);
                break;
            }
            default:{
                //什么都不会发生
                Log.d(TAG, "onClick: click nowhere");
            }
        }
    }
}
