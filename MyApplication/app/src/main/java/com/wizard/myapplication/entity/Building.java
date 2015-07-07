package com.wizard.myapplication.entity;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wizard on 2015/7/7.
 */
public class Building
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

    public LatLng getCenter() {
        return center;
    }

    public void setCenter(LatLng center) {
        this.center = center;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    private String id;
    private String name;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;
    private LatLng center;
    private List<Comment> comments
            = new ArrayList<Comment>();

}
