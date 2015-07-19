package com.wizard.myapplication.entity;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        this.type = Building.typeMap.get(id);
        if(this.type == null) this.type = "";
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Building)) return false;
        Building building = (Building) o;
        return id != building.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    private int id;
    private String name;
    private String content;
    private double latitude;
    private double longitude;
    private double radius;
    private String type;

    private static Map<Integer, String> typeMap
            = new HashMap<Integer, String>();

    static {
        typeMap.put(1, BuildingType.ACADAMIC);
        typeMap.put(2, BuildingType.SPORT);
        typeMap.put(3, BuildingType.SCENE);
        typeMap.put(4, BuildingType.SCENE);
        typeMap.put(5, BuildingType.SPORT);
        typeMap.put(6, BuildingType.ACADAMIC);
        typeMap.put(7, BuildingType.ACADAMIC);
        typeMap.put(8, BuildingType.SPORT);
        typeMap.put(9, BuildingType.ACADAMIC);
        typeMap.put(11, BuildingType.SCENE);
        typeMap.put(12, BuildingType.SPORT);
        typeMap.put(13, BuildingType.FOOD);
        typeMap.put(14, BuildingType.ACADAMIC);
        typeMap.put(15, BuildingType.HISTORY);
        typeMap.put(16, BuildingType.HISTORY);
    }
}
