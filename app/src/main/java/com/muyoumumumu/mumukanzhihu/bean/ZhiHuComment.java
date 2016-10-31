package com.muyoumumumu.mumukanzhihu.bean;

/**
 * 评论
 * Created by Amumu on 2016/10/21.
 */

public class ZhiHuComment {
    private String author;
    private String content;
    private String avatar;
    private String time;
    private int likes;

    public ZhiHuComment(String author, String content, String avatar, String time, int likes){
        this.author = author;
        this.avatar = avatar;
        this.content = content;
        this.time = time;
        this.likes = likes;
    }

    public String getAuthor() {
        return author;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public int getLikes() {
        return likes;
    }

}
