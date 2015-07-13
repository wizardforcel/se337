package com.wizard.myapplication.entity;

import java.io.Serializable;

/**
 * Created by Wizard on 2015/7/7.
 */
public class Comment implements Serializable
{
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

    private String un;
    private String content;
}
