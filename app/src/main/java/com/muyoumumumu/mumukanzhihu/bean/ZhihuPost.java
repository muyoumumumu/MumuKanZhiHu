package com.muyoumumumu.mumukanzhihu.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 这是一个知乎内容的类
 *
 * Created by amumu on 2016/8/26.
 */
public class ZhihuPost {

    private String title;
    private List<String> images=new ArrayList<>();
    private String type;
    private String id;

    //getset方法
    public ZhihuPost (String title, List<String> images, String type, String id){
        this.id=id;
        this.images=images;
        this.type=type;
        this.title=title;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getImages() {
        return images;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getFirstImg(){
        if(images.isEmpty())
            return null;
        return images.get(0);
    }

}
