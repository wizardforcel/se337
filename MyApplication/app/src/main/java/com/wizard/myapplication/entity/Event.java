package com.wizard.myapplication.entity;

import java.io.Serializable;

/**
 * Created by Wizard on 2015/7/16.
 */
public class Event implements Serializable
{
    private int id;
    private String name;
    private byte[] avatar;
    private String content;
    private int uid;
    private String un;
    private String enrollStartDate;
    private String enrollEndDate;
    private String startDate;
    private String endDate;
    private double lat;
    private double lng;
    private String location;
    private int maxPeople;

    public String getEnrollStartDate() {
        return enrollStartDate;
    }

    public void setEnrollStartDate(String enrollStartDate) {
        this.enrollStartDate = enrollStartDate;
    }

    public String getEnrollEndDate() {
        return enrollEndDate;
    }

    public void setEnrollEndDate(String enrollEndDate) {
        this.enrollEndDate = enrollEndDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getMaxPeople() {
        return maxPeople;
    }

    public void setMaxPeople(int maxPeople) {
        this.maxPeople = maxPeople;
    }


    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUn() {
        return un;
    }

    public void setUn(String un) {
        this.un = un;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
