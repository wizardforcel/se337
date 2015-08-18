package com.wizard.myapplication.entity;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wizard on 2015/7/13.
 */
public class User implements Serializable
{
    private int id;
    private String un;
    private String pw;
    private List<String> pres
            = new ArrayList<String>();
    private byte[] avatar;

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public List<String> getPres() {
        return pres;
    }

    public void setPres(List<String> pres) {
        this.pres = pres;
    }

}
