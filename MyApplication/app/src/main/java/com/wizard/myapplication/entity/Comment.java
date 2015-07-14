package com.wizard.myapplication.entity;

import java.io.Serializable;

/**
 * Created by Wizard on 2015/7/7.
 */
public class Comment implements Serializable
{

    private int id;
    private int uid;
    private String un;
    private String content;
    private int like;

    public String getUn() {
        return un;
    }

    public String getContent() {
        return content;
    }

    public void setUn(String un) {
        this.un = un;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getDislike() {
        return dislike;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
    }

    private int dislike;
}
