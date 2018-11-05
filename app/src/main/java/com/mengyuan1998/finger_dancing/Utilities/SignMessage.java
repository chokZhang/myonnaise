package com.mengyuan1998.finger_dancing.Utilities;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Scarecrow on 2018/2/6.
 *
 */

public class SignMessage extends ConversationMessage {
    public static final int CONFIRMED_CORRECT = 564,
            CONFIRMED_WRONG = 456,
            NO_RECAPTURE = 789,
            INITIAL = 2541;

    private int sign_feedback_stauts;

    private int capture_id = 0;

    private List<SignSentence> sentences;
    private SignSentence active_sentence;

    private boolean is_capture_complete = false;


    public SignMessage(String text, int msg_id) {
        super(msg_id, SIGN, text);
        sign_feedback_stauts = INITIAL;
        sentences = new LinkedList<>();
        active_sentence = new SignSentence();
        sentences.add(active_sentence);
    }

    public int getSignFeedbackStatus() {
        return sign_feedback_stauts;
    }

    public void setSignFeedbackStatus(int stauts) {
        sign_feedback_stauts = stauts;
    }

    public int getCaptureId() {
        return capture_id;
    }

    public void setCaptureId(int capture_id) {
        this.capture_id = capture_id;
    }

    public boolean isCaptureComplete() {
        return is_capture_complete;
    }

    public void setCaptureComplete(boolean status) {
        is_capture_complete = status;
    }

    protected void appendTextContent(String content) {
        active_sentence.appendWord(content);

    }

    public void cleanTextContent(){
        sentences.clear();
        active_sentence = new SignSentence();
        sentences.add(active_sentence);
    }

    @Override
    public String getTextContent() {
        StringBuilder ret = new StringBuilder();
        for(SignSentence s:sentences){
            ret.append(s.getSentenceStr());
        }
        return ret.toString();
    }
}
