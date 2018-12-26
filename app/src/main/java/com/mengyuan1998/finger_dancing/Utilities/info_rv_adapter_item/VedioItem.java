package com.mengyuan1998.finger_dancing.Utilities.info_rv_adapter_item;

import org.json.JSONObject;

public class VedioItem extends BaseItem{

    private int thumbs;
    private String vedio_link;
    private String img_link;
    private String info;
    private String create_time;
    //private String baseUrl = "http://";

    public VedioItem(JSONObject jsonObject) throws Exception {
        id = jsonObject.getString("id");
        thumbs = Integer.valueOf(jsonObject.getString("thumbs")).intValue();
        vedio_link = jsonObject.getString("video_link");
        vedio_link = vedio_link.replaceAll("https", "http");
        img_link = jsonObject.getString("img_link");
        info = jsonObject.getString("info");
        create_time = jsonObject.getString("create_time");
    }

    public VedioItem(){
        vedio_link = "http://sign-resource.oss-cn-beijing.aliyuncs.com/%E5%A4%A7%E5%B8%85%E5%BA%9C2%20%283%29.mp4?spm=5176.8466032.0.dopenurl.25691450dPvgc0&file=%E5%A4%A7%E5%B8%85%E5%BA%9C2%20%283%29.mp4";
        //vedio_link = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
        info = "大帅府的介绍。";
    }

    public int getThumbs() {
        return thumbs;
    }

    public void setThumbs(int thumbs) {
        this.thumbs = thumbs;
    }

    public String getVedio_link() {
        return vedio_link;
    }

    public void setVedio_link(String vedio_link) {
        this.vedio_link = vedio_link;
    }

    public String getImg_link() {
        return img_link;
    }

    public void setImg_link(String img_link) {
        this.img_link = img_link;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getUrl() {
        return vedio_link;
    }
    @Override
    public int getPraise_num(){
        return thumbs;
    }
    public int getType() {
        return 0;
    }

    public String getInfo_text(){
        return info;
    }
    public String getUser_name() {
        return "JASON";
    }
}
