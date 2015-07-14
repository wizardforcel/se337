package com.wizard.myapplication.entity;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wizard on 2015/7/7.
 */
public class Building implements Serializable
{

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private int id;
    private String name;
    private String content;
    private double latitude;
    private double longitude;
    private double radius;

}
