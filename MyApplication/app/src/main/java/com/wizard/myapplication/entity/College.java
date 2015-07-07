package com.wizard.myapplication.entity;

import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wizard on 2015/7/7.
 */
public class College
{
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public LatLng getCenter() {
        return center;
    }

    public void setCenter(LatLng center) {
        this.center = center;
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(List<Building> buildings) {
        this.buildings = buildings;
    }

    public List<LatLng> getCovered() {
        return covered;
    }

    public void setCovered(List<LatLng> covered) {
        this.covered = covered;
    }

    private String id;
    private String name;
    private String content;
    private LatLng center;
    private List<Building> buildings
            = new ArrayList<Building>();
    private List<LatLng> covered
            = new ArrayList<LatLng>();
}
