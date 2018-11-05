package com.mengyuan1998.finger_dancing.recognize;


public class AsrFinishJsonData {
    private String error;
    private String sub_error;
    private String desc;
    private RecogResult origin_result;

    public String getError() {
        return error;
    }

    public String getSub_error() {
        return sub_error;
    }

    public String getDesc() {
        return desc;
    }

    public RecogResult getOrigin_result() {
        return origin_result;
    }
}
