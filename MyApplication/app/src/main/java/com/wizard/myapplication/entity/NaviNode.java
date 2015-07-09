package com.wizard.myapplication.entity;

import java.io.Serializable;

/**
 * Created by Wizard on 2015/7/9.
 */
public class NaviNode implements Serializable {

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private double lat;
    private double lng;
    private String name = "";
}
