package com.mengyuan1998.finger_dancing.Utilities;

import android.util.Log;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by Scarecrow on 2018/2/8.
 * 消息id分为两种 一种是本地的id 给予文字和语音识别消息的id
 * 这些消息不需要和服务器进行交互 仅在本地进行管理即可。
 * 还有一种是手语消息的id 由于手语可能需要进行重发
 * 所以需要与服务端进行进行同步工作 手语数据主要是在服务端进行管理
 * 为了便于服务端手语数据的管理 这个id从服务端获取。
 */

public class MessageManager {
    private static MessageManager instance = new MessageManager();
    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    //protected String offlineVoice = OfflineResource.VOICE_MALE;
    private List<ConversationMessage> messages_list;
    private List<NoticeMessageChanged> notice_list = new ArrayList<>();
    private Map<Integer, SignMessage> sign_message_map = new Hashtable<>();
    private boolean capture_state = false;
    private String appId = "11138165";
    private String appKey = "atDLVSr4NFmDNPxPWHxWnPVS";
    private String secretKey = "20da52346b042869be7cda3f8fb12cf5";
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
   /* private TtsMode ttsMode = TtsMode.MIX;
    private OfflineResource offlineResource = null;

    private MySynthesizer synthesizer = null;
    private TTSMassageListener synthesizerListener = new TTSMassageListener();*/


    /**
     * 这个方法被用于inputControl的fragment中
     * 在这个fragment中点按创建的手语识别消息都是新建的
     * 该方法先新建一个手语消息实例并并显示出来 并将该实例暂存下来
     * 当时识别完成后使用回调更新该手语的数据
     */
    private SignMessage new_added_msg;
    // false -> 没有采集
    // true  -> 采集中


    private MessageManager() {
        messages_list = new ArrayList<>();
    }

    public static MessageManager getInstance() {
        return instance;
    }


    private int acquire_curr_id() {
        return messages_list.size();
    }

    public void buildSignMessage(String content) {
        new_added_msg = new SignMessage("正在识别手语中", 0);
        messages_list.add(new_added_msg);
        new_added_msg.appendTextContent(content);
        noticeAllTargetMsgAdded();
    }

    public TextMessage buildTextMessage(String text) {
        TextMessage new_msg = new TextMessage(acquire_curr_id(), text);
        messages_list.add(new_msg);
        noticeAllTargetMsgAdded();
        return new_msg;
    }

    public VoiceMessage buildVoiceMessage(String voice_path) {
        VoiceMessage new_msg = new VoiceMessage(acquire_curr_id(), voice_path);
        messages_list.add(new_msg);
        noticeAllTargetMsgAdded();
        return new_msg;
    }

    public void processSignMessageFeedback(String feedback_json) {
        try {
            JSONObject jsonObject = new JSONObject(feedback_json);
            String control_info = jsonObject.getString("control");
            if (control_info.equals("update_recognize_res")) {
                processSignMessageFeedback(jsonObject.getString("text"),
                        jsonObject.getInt("sign_id"),
                        jsonObject.getInt("capture_id"));

            } else if (control_info.equals("end_recognize")) {
                int sign_id = jsonObject.getInt("sign_id");
                if (sign_message_map.containsKey(sign_id)) {
                    sign_message_map.get(sign_id).setCaptureComplete(true);
                    noticeAllTargetSignCaptureEnd();
                    noticeAllTargetMsgChange();
                }
            }
        } catch (Exception ee) {
            Log.e(TAG, "buildSignMessage:  error: " + ee);
            ee.printStackTrace();
        }
    }

    /**
     * 当返回一条手语识别的消息调用后 更新一个手语消息的实例
     * 有两种情况 一种是新创建的手语消息实例 另一种是重发的手语
     * 通过手语的id进行map判断这个手语是否被识别过一次
     *
     * @param text          手语的文字内容 来自服务器
     * @param sign_id       手语的id码 来自服务器给定
     * @return 新生成的手语消息对象
     */
    private void processSignMessageFeedback(String text, int sign_id, int capture_id) {
        SignMessage new_msg;
        if (sign_message_map.containsKey(sign_id)) {
            new_msg = sign_message_map.get(sign_id);
            synthesizeVoice(text);
            new_msg.appendTextContent(text);
        } else {
            synthesizeVoice(text);
            new_added_msg.cleanTextContent();
            new_added_msg.setCaptureId(capture_id);
            new_added_msg.appendTextContent(text);
            new_added_msg.setMsgId(sign_id);
            sign_message_map.put(sign_id, new_added_msg);
        }
        noticeAllTargetMsgChange();
    }

    /*public void initTTS(Context context) {
        try {
            LoggerProxy.printable(true); // 日志打印在logcat中
            // 设置初始化参数
            // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
            offlineResource = new OfflineResource(context, offlineVoice);
            Map<String, String> params = initTTSParams();
            // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
            InitConfig initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params, synthesizerListener);
            synthesizer = new NonBlockSynthesizer(context, initConfig);

        } catch (Exception ee) {
            Log.e(TAG, "initTTS: 出现错误 " + ee);
        }
    }

    public void releaseTTS() {
        synthesizer.release();
    }*/

    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return
     */
    /*private Map<String, String> initTTSParams() {
        Map<String, String> params = new HashMap<String, String>();
        // 以下参数均为选填
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        params.put(SpeechSynthesizer.PARAM_SPEAKER, "1");
        // 设置合成的音量，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_VOLUME, "9");
        // 设置合成的语速，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, "5");

        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE);
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        // 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
        // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
        params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE,
                offlineResource.getTextFilename());
        params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
                offlineResource.getModelFilename());
        return params;
    }*/



    private void synthesizeVoice(String voice_str) {
        //synthesizer.speak(voice_str);
    }


    /*public boolean requestCaptureSign() {
        if (capture_state) {
            Log.e(TAG, "requestCaptureSign: sign capturing repeat");
            return false;
        }
        capture_state = true;
        try {
            SocketConnectionManager.getInstance()
                    .sendMessage(buildSignRecognizeRequest(0));
            MessageManager.getInstance()
                    .buildSignMessage();
        } catch (Exception ee) {
            Log.e(TAG, "requestCaptureSign: can not create request, " + ee.getMessage());
            Toast.makeText(APP_CONTEXT,
                    "与服务器连接已断开，请退出后重新连接手环再发起识别请求",
                    Toast.LENGTH_LONG)
                    .show();
            capture_state = false;
            return false;
        }
        return true;
    }*/

    /*public boolean recaptureSignRequest(SignMessage message) {
        if (capture_state) {
            Log.e(TAG, "requestCaptureSign: sign capturing repeat");
            return false;
        }
        try {
            //初始化消息里的数据
            capture_state = true;
            message.setCaptureComplete(false);
            message.cleanTextContent();
            //向外界告知开始手语识别了
            noticeAllTargetMsgSignCaptureStart();
            SocketConnectionManager.getInstance()
                    .sendMessage(buildSignRecognizeRequest(message.getMsgId()));
        } catch (Exception ee) {
            Log.e(TAG, "requestCaptureSign: can not create request, " + ee.getMessage());
            capture_state = false;
            Toast.makeText(APP_CONTEXT,
                    "与服务器连接已断开，请退出后重新连接手环再发起识别请求",
                    Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        return true;
    }*/

    /*public boolean stopSignRecognize() {
        if (capture_state) {
           SocketConnectionManager.getInstance()
                    .sendMessage(buildStopCaptureRequest());
            capture_state = false;
            noticeAllTargetSignCaptureEnd();
            return true;
        } else {
            Log.e(TAG, "requestCaptureSign: sign capturing didn't start");
            return false;
        }

    }*/

    /**
     * 手语识别请求体构造
     * 如果是新增识别， 的 sign_id字段使用0 标识
     * 如："data": {"sign_id" :0}
     *
     * @return 请求的json
     */
    /*private String buildSignRecognizeRequest(int sign_id) throws Exception {
        Armband[] paired_armbands = ArmbandManager.getArmbandsManger()
                .getCurrentConnectedArmband();
        JSONArray armbands_json_array = new JSONArray();
        for (Armband armband : paired_armbands) {
            if (armband == null)
                throw new Exception("paired armband lose");
            armbands_json_array.put(armband.getArmbandId());
        }
        JSONObject request_body = new JSONObject();
        try {
            request_body.accumulate("control", "sign_cognize_request");
            JSONObject data = new JSONObject();
            data.accumulate("armband_id", armbands_json_array);
            data.accumulate("sign_id", sign_id);
            request_body.accumulate("data", data);
        } catch (Exception ee) {
            Log.e(TAG, "buildSignRecognizeRequest: on build request json " + ee);
            ee.printStackTrace();
        }
        return request_body.toString();
    }*/


    private String buildStopCaptureRequest() {
        JSONObject request_body = new JSONObject();
        try {
            request_body.accumulate("control", "stop_recognize");
            request_body.accumulate("data", "");
        } catch (Exception ee) {
            Log.e(TAG, "buildStopCaptureRequest: ", ee);
            ee.printStackTrace();
        }
        return request_body.toString();
    }


    public boolean isCapturingSign() {
        return capture_state;
    }

    public List<ConversationMessage> getMessagesList() {
        return messages_list;
    }

    public void cleanMessageList() {
        messages_list.clear();
        noticeAllTargetMsgChange();
    }


    /**
     * 以下是一系列使用观察者模式实现的回调类M
     * 当手语识别开始或者结束的时候 通过回调告知所有相关的对象
     */

    public void addNewNoticeTarget(NoticeMessageChanged obj) {
        notice_list.add(obj);
    }

    private void noticeAllTargetSignCaptureEnd() {
        capture_state = false;
        for (NoticeMessageChanged obj : notice_list) {
            obj.onSignCaptureEnd();
        }
    }

    private void noticeAllTargetMsgAdded() {
        for (NoticeMessageChanged obj : notice_list) {
            obj.onNewMessageAdd();
        }
    }

    public void noticeAllTargetMsgChange() {
        for (NoticeMessageChanged obj : notice_list) {
            obj.onMessageContentChange();
        }
    }

    private void noticeAllTargetMsgSignCaptureStart() {
        for (NoticeMessageChanged obj : notice_list) {
            obj.onSignCaptureStart();
        }
    }

    public void sampleDisplayCreate(){
        buildSignMessage("");
        processSignMessageFeedback("医生 您好",2,1);
        processSignMessageFeedback("{\"control\":\"end_recognize\",\"sign_id\":2}");
        noticeAllTargetSignCaptureEnd();

        VoiceMessage v = buildVoiceMessage("");
        v.setTextContent("您好，请问您那里不舒服？");
        noticeAllTargetMsgChange();


    }



    public interface NoticeMessageChanged {
        void onNewMessageAdd();

        void onMessageContentChange();

        void onSignCaptureStart();

        void onSignCaptureEnd();
    }
}
