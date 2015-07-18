package com.wizard.myapplication.util;

import android.graphics.Color;
import android.view.*;
import android.widget.*;

/**
 * Created by Wizard on 2015/7/18.
 */
public class TabUtil {

    public static void updateTab(final TabHost tabHost)
    {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            View view = tabHost.getTabWidget().getChildAt(i);
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            if (tabHost.getCurrentTab() == i) {//选中
                view.setBackgroundColor(Color.WHITE);
                tv.setTextColor(Color.parseColor("#ff9966"));
            } else {//不选中
                view.setBackgroundColor(Color.WHITE);
                tv.setTextColor(Color.parseColor("#999999"));
            }
        }
    }

}
