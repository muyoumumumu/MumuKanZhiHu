package com.muyoumumumu.mumukanzhihu.bean;

/**
 * Created by amumu on 2016/10/22.
 */

public class GuokrPost {
    private String title;
    private String img;
    private String id;
    private String summary;

    public GuokrPost(String id, String title, String img, String summary) {
        this.title = title;
        this.img = img;
        this.id = id;
        this.summary = summary;
    }

    public String getId() {
        return id;
    }

    public String getImg() {
        return img;
    }

    public String getSummary() {
        return summary;
    }

    public String getTitle() {
        return title;
    }
}
