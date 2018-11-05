package com.mengyuan1998.finger_dancing.Utilities;

/**
 * 交流信息的基类 交流信息有语音 手语以及文字三种
 */

public abstract class ConversationMessage {
    public static final int SIGN = 714,
            VOICE = 564,
            TEXT = 654;

    protected int msg_id;
    protected String text_content;
    protected int msg_type;




    protected ConversationMessage(int msg_id, int msg_type, String text_content) {
        this.msg_id = msg_id;
        this.msg_type = msg_type;
        this.text_content = text_content;
    }

    public int getMsgId() {
        return msg_id;
    }

    public void setMsgId(int id) {
        msg_id = id;
    }

    public int getMsgType() {
        return msg_type;
    }

    public String getTextContent() {
        return text_content;
    }

    public void setTextContent(String content) {
        text_content = content;
    }
}
