package com.wizard.myapplication.entity;

import com.baidu.mapapi.model.LatLng;

import java.util.List;

/**
 * Created by Wizard on 2015/7/7.
 */
public class DataManager
{
    public static String[] getCollegeList()
    {
        return new String[]{"sjtu-mh"};
    }

    public static College getCollege(String id)
    {
        College college = new College();
        college.setId("sjtu-mh");
        college.setName("上海交通大学 闵行校区");
        college.setContent("上海交通大学 闵行校区");
        college.setCenter(new LatLng(31.031231, 121.442522));
        List<Building> buildings = college.getBuildings();

        return college;
    }

}
