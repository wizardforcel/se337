package com.wizard.myapplication.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wizard on 2015/7/19.
 */
public class BuildingType {
    public final static String SPORT = "SPORT";
    public final static String ACADAMIC = "ACADAMIC";
    public final static String SCENE = "SCENE";
    public final static String HISTORY = "HISTORY";
    public final static String FOOD = "FOOD";

    public final static String[] TYPES = new String[] {SPORT, ACADAMIC, SCENE, HISTORY, FOOD};

    public static Map<String, String> enToZhMap = new HashMap<String, String>();

    static
    {
        enToZhMap.put(SPORT, "运动");
        enToZhMap.put(ACADAMIC, "美食");
        enToZhMap.put(SCENE, "风景");
        enToZhMap.put(HISTORY, "历史");
        enToZhMap.put(FOOD, "学术");
    }
}
