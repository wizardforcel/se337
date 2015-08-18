package com.wizard.myapplication.util;

import com.wizard.myapplication.entity.Building;
import com.wizard.myapplication.entity.Campus;
import com.wizard.myapplication.entity.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2015/8/17.
 */
public class TestData {

    public static byte[] AVATAR = null;
    public static byte[] CAMPUS_PIC = null;

    public static Campus getCampus()
    {
        Campus college = new Campus();
        college.setId(1);
        college.setName("上海交通大学 闵行校区");
        college.setContent("上海交通大学 闵行校区");
        college.setLatitude(31.031231);
        college.setLongitude(121.442522);

        List<Building> buildings = college.getBuildings();

        Building se = new Building();
        se.setId(1);
        se.setName("软件学院");
        se.setContent("软件学院");
        se.setLatitude(31.028698);
        se.setLongitude(121.448751);
        buildings.add(se);

        Building swGate = new Building();
        swGate.setId(2);
        swGate.setName("拖鞋门");
        swGate.setContent("拖鞋门");
        se.setLatitude(31.023893);
        se.setLongitude(121.437446);
        buildings.add(swGate);

        Building newGym = new Building();
        newGym.setId(3);
        newGym.setName("新体育馆");
        newGym.setContent("新体育馆");
        se.setLatitude(31.033131);
        se.setLongitude(121.430754);
        buildings.add(newGym);

        Building newLib = new Building();
        newLib.setId(4);
        se.setLatitude(31.032334);
        se.setLongitude(121.444013);
        newLib.setName("新图书馆");
        newLib.setContent("新图书馆");
        buildings.add(newLib);

        return college;
    }

    public static List<Event> getEvents()
    {
        Event e = new Event();
        e.setId(1);
        e.setName("测试活动");
        e.setContent("测试活动。。。");
        e.setLocation("location");
        e.setStartDate("20150817000000");
        e.setEndDate("20150817000000");
        e.setEnrollStartDate("20150817000000");
        e.setEnrollEndDate("20150817000000");
        e.setMaxPeople(100);
        List<Event> events= new ArrayList<Event>();
        for(int i = 0; i < 10; i++)
            events.add(e);
        return events;
    }
}
