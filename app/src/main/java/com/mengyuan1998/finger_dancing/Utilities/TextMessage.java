package com.mengyuan1998.finger_dancing.Utilities;

/**
 * Created by Scarecrow on 2018/2/8.
 */

public class TextMessage extends ConversationMessage {
    public TextMessage(int msg_id, String text) {
        super(msg_id, TEXT, text);
    }
}
