package com.mengyuan1998.finger_dancing.Utilities.info_rv_adapter_item;


import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

public class BaseItem implements Serializable {

    private int type = -1;
    //图片或者视频的url
    private String url;
    //多张图片时需用到
    private List<String> urls;
    //文字描述
    private String info_text;

    private String headImg_url;
    private String user_name;
    private int praise_num;
    //表示是否对此条信息点赞
    private boolean praise_state;
    private int share_num;
    private Bitmap bitmap = null;
    //是否加载过图片
    private boolean getImgState;
    //在数据库中的编号
    public String id;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    //返回视频地址
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public String getInfo_text() {
        return info_text;
    }

    public void setInfo_text(String info_text) {
        this.info_text = info_text;
    }

    public String getHeadImg_url() {
        return headImg_url;
    }

    public void setHeadImg_url(String headImg_url) {
        this.headImg_url = headImg_url;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getPraise_num() {
        return praise_num;
    }

    public void setPraise_num(int praise_num) {
        this.praise_num = praise_num;
    }

    public boolean isPraise_state() {
        return praise_state;
    }

    public void setPraise_state(boolean praise_state) {
        this.praise_state = praise_state;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isGetImgState() {
        return getImgState;
    }

    public void setGetImgState(boolean getImgState) {
        this.getImgState = getImgState;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getShare_num() {
        return share_num;
    }

    public void setShare_num(int share_num) {
        this.share_num = share_num;
    }
}
