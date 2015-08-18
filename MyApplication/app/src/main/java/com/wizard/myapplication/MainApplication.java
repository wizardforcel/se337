package com.wizard.myapplication;

import android.app.Application;
import android.content.res.Resources;
import android.os.Environment;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.navisdk.BaiduNaviManager;
import com.wizard.myapplication.util.TestData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

        try {
            TestData.AVATAR = loadRsrcDataById(R.drawable.avatar);
            TestData.CAMPUS_PIC = loadRsrcDataById(R.drawable.sjtu);
        } catch (IOException e)
        {   e.printStackTrace(); }


    }

    public static String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(
                Environment.MEDIA_MOUNTED))
            return Environment.getExternalStorageDirectory().toString();

        return null;
    }

    private byte[] loadRsrcDataById(int id)
            throws IOException {
        InputStream in = getResources().openRawResource(id);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        while (true) {
            int len = in.read(buffer);
            if (len == -1) break;
            os.write(buffer, 0, len);
        }
        return os.toByteArray();
    }


}
