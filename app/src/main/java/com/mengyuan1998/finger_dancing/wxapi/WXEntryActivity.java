package com.mengyuan1998.finger_dancing.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.mengyuan1998.finger_dancing.base.Config;
import com.mengyuan1998.finger_dancing.entity.WXAccessTokenEntity;
import com.mengyuan1998.finger_dancing.entity.WXBaseRespEntity;
import com.mengyuan1998.finger_dancing.entity.WXUserInfo;
import com.mengyuan1998.finger_dancing.ui.WelcomeActivity;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.vise.log.ViseLog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    /**
     * 微信登录相关
     */
    private IWXAPI api;
    private ImageView ivHead;
    private int a=0;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        Log.d("WXEntryActivityfuck","onCreate start");
        super.onCreate(savedInstanceState);
        //通过WXAPIFactory工厂获取IWXApI的示例

        api = WXAPIFactory.createWXAPI(this, Config.APP_ID_WX,true);
        api.handleIntent(this.getIntent(), this);
        //将应用的appid注册到微信
        api.registerApp(Config.APP_ID_WX);
        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        try {
            boolean result =  api.handleIntent(getIntent(), this);
            if(!result){
                Log.d("WXEntryActivity","参数不合法，未被SDK处理，退出");
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("WXentryActivityfuck","onActivityResult start");
        super.onActivityResult(requestCode, resultCode, data);
        api.handleIntent(data,this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("WXentryActivityfuck","onNewIntent start");
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.d("WXEntryActivit","fuck baseReq:"+ JSON.toJSONString(baseReq));
    }
    @Override
    public void onResp(BaseResp baseResp) {
        Log.d("WXentryActivityfuck","onResp start");
        Log.d("WXEntryActivity","fuck"+JSON.toJSONString(baseResp));
        Log.d("WXEntryActiviyy","fuck,"+baseResp.openId+","+baseResp.transaction+","+baseResp.errCode);
        WXBaseRespEntity entity = JSON.parseObject(JSON.toJSONString(baseResp),WXBaseRespEntity.class);
        String result = "";
        switch(baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result ="发送成功";
  //              Log.d("WXentryActivity","正在获取个人资料..");
                //现在请求获取数据 access_token https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
//                showMsg(1,result);
                /*Call call = RetrofitUtils.getApiService("https://api.weixin.qq.com/").getWeiXinAccessToken(Config.APP_ID_WX,Config.APP_SECRET_WX,entity.getCode(),"authorization_code");
                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        ViseLog.d("response:"+JSON.toJSONString(response));
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        closeDialog();
                    }
                });*/
                OkHttpUtils.get().url("https://api.weixin.qq.com/sns/oauth2/access_token")
                        .addParams("appid",Config.APP_ID_WX)
                        .addParams("secret",Config.APP_SECRET_WX)
                        .addParams("code",entity.getCode())
                        .addParams("grant_type","authorization_code")
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(okhttp3.Call call, Exception e, int id) {
                                ViseLog.d("请求错误..");
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                ViseLog.d("response:"+response);
                                WXAccessTokenEntity accessTokenEntity = JSON.parseObject(response,WXAccessTokenEntity.class);
                                if(accessTokenEntity!=null){
                                    Log.d("WXentryActivityfuck","正在获取个人资料..");
                                    getUserInfo(accessTokenEntity);
                                }else {
                                    ViseLog.d("获取失败");
                                }

                            }
                        });
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "发送取消";
                ViseLog.d("发送取消");
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送被拒绝";
                ViseLog.d("发送被拒绝");
                finish();
                break;
            case BaseResp.ErrCode.ERR_BAN:
                result = "签名错误";
                ViseLog.d("签名错误");
                break;
            default:
                result = "发送返回";
//                showMsg(0,result);
                finish();
                break;
        }
        finish();
        //Toast.makeText(WXEntryActivity.this,result,Toast.LENGTH_LONG).show();
    }

    /**
     * 获取个人信息
     * @param accessTokenEntity
     */
    private void getUserInfo(WXAccessTokenEntity accessTokenEntity) {
        //https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
        OkHttpUtils.get()
                .url("https://api.weixin.qq.com/sns/userinfo")
                .addParams("access_token",accessTokenEntity.getAccess_token())
                .addParams("openid",accessTokenEntity.getOpenid())//openid:授权用户唯一标识
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("WXEntry","userInfo:"+response);
                        WXUserInfo wxResponse = JSON.parseObject(response,WXUserInfo.class);
                        a++;
                        String headUrl = wxResponse.getHeadimgurl();
                        String nickname=wxResponse.getNickname();
                        Log.d("WXEntry","NAME  "+nickname);
                        if(a==2&&nickname!=null)
                        {
                            Intent intent1 = new Intent(WXEntryActivity.this, WelcomeActivity.class);
                            intent1.putExtra("name",nickname);
                            intent1.putExtra("head",headUrl);
                            startActivity(intent1);
                        }
                    }
                });
    }
}
