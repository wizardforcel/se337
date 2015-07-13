package com.wizard.myapplication.entity;

import java.io.Serializable;

/**
 * Created by Wizard on 2015/7/13.
 */
public class User implements Serializable
{
    private String id;
    private String un;
    private String pw;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUn() {
        return un;
    }

    public void setUn(String un) {
        this.un = un;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
