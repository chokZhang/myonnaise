package com.mengyuan1998.finger_dancing.base;


public class Config {

    /**
     * QQ登录的APPID
     */
    public static final String QQ_LOGIN_APP_ID = "1106210336";

    /**
     * app_id是从微信官网申请到的合法APPid
     */
    public static final String APP_ID_WX = "wx33773ca47db2fdfe";

    /**
     * 微信AppSecret值
     */
    public static final String APP_SECRET_WX = "4a6b319b64238391e0d97ebbea4fa4a3";

    /**
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     */
//    public static final String REDIRECT_URL = "http://www.sina.com";
    //注意，这里的回调页应该和官网的回调页一致，不然会报21322错误
    public static final String REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";

    /**
     * WeiboSDKDemo 应用对应的权限，第三方开发者一般不需要这么多，可直接设置成空即可。
     * 详情请查看 Demo 中对应的注释。
     */
    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

    public static final String APP_LOGO_URL = "http://182.254.232.121:8080/commonImg/stop_car_app_logo.png";

}
