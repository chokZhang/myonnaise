package it.ncorti.emgvisualizer.recognize;


public class RecogResult {
    private String error;
    private String sub_error;
    private String sn;
    private String desc;

    public String getSn() {
        return sn;
    }

    public String getDesc() {
        return desc;
    }

    public String getError() {
        return error;
    }

    public String getSub_error() {
        return sub_error;
    }
}
