package com.wizard.myapplication.entity;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wizard on 2015/7/7.
 */
public class Campus implements Serializable
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

    public List<Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(List<Building> buildings) {
        this.buildings = buildings;
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

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    private int id;
    private String name;
    private String content;
    private double latitude;
    private double longitude;
    private double radius;
    private byte[] avatar;
    private List<Building> buildings
            = new ArrayList<Building>();
    private List<Event> events
            = new ArrayList<Event>();

}
