package com.wizard.myapplication.util;

import com.wizard.myapplication.entity.Building;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wizard on 2015/7/29.
 */
public class DistanceUtil {

    public static double distance(double lat1, double lng1, double lat2, double lng2)
    {
        return Math.sqrt(Math.pow(lat1 - lat2, 2) + Math.pow(lng1 - lng2, 2));
    }

    public static List<Building> sort(double myLat, double myLng, List<Building> li)
    {
        boolean[] visited = new boolean[li.size()];
        List<Building> result = new ArrayList<Building>();
        double curLat = myLat;
        double curLng = myLng;

        for(int i = 0; i < li.size(); i++)
        {
            double min = Double.POSITIVE_INFINITY;
            int index = -1;
            for(int j = 0; j < li.size(); j++) {
                if(visited[j]) continue;
                Building b = li.get(j);
                double distance = distance(b.getLatitude(), b.getLongitude(), curLat, curLng);
                if(distance < min)
                {
                    min = distance;
                    index = j;
                }
            }
            Building nearest = li.get(index);
            visited[index] = true;
            result.add(nearest);
            curLat = nearest.getLatitude();
            curLng = nearest.getLongitude();
        }

        return result;
    }

}
