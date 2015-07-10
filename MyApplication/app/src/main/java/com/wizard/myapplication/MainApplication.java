package com.wizard.myapplication;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.navisdk.BaiduNaviManager;

/**
 * Created by Wizard on 2015/7/6.
 */
public class MainApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
    }
}
