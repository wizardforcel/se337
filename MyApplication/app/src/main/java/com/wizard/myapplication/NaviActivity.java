package com.wizard.myapplication;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.comapi.mapcontrol.BNMapController;
import com.baidu.navisdk.comapi.routeplan.BNRoutePlaner;
import com.baidu.navisdk.comapi.tts.BNTTSPlayer;
import com.baidu.navisdk.comapi.tts.BNavigatorTTSPlayer;
import com.baidu.navisdk.comapi.tts.IBNTTSPlayerListener;
import com.baidu.navisdk.model.datastruct.LocData;
import com.baidu.navisdk.model.datastruct.SensorData;
import com.baidu.navisdk.ui.routeguide.BNavigator;
import com.baidu.navisdk.ui.routeguide.IBNavigatorListener;
import com.baidu.navisdk.ui.widget.RoutePlanObserver;
import com.baidu.nplatform.comapi.map.MapGLSurfaceView;
import com.wizard.myapplication.R;

public class NaviActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //创建mapView
        MapGLSurfaceView nMapView = BaiduNaviManager.getInstance().createNMapView(this);

        //创建导航视图
        View navigatorView = BNavigator.getInstance().init(NaviActivity.this, getIntent().getExtras(), nMapView);

        //填充视图
        setContentView(navigatorView);
        BNavigator.getInstance().setListener(new IBNavigatorListener() {
            @Override
            public void onYawingRequestSuccess() {
                // TODO 偏航请求成功
            }
            @Override
            public void onYawingRequestStart() {
                // TODO 开始偏航请求
            }
            @Override
            public void onPageJump(int jumpTiming, Object arg) {
                // TODO 页面跳转回调
                if(IBNavigatorListener.PAGE_JUMP_WHEN_GUIDE_END == jumpTiming){
                    finish();
                }else if(IBNavigatorListener.PAGE_JUMP_WHEN_ROUTE_PLAN_FAIL == jumpTiming){
                    finish();
                }
            }
            @Override
            public void notifyGPSStatusData(int arg0) {
                // TODO Auto-generated method stub
            }
            @Override
            public void notifyLoacteData(LocData arg0) {
                // TODO Auto-generated method stub
            }
            @Override
            public void notifyNmeaData(String arg0) {
                // TODO Auto-generated method stub
            }
            @Override
            public void notifySensorData(SensorData arg0) {
                // TODO Auto-generated method stub
            }
            @Override
            public void notifyStartNav() {
                // TODO Auto-generated method stub
                BaiduNaviManager.getInstance().dismissWaitProgressDialog();
            }
            @Override
            public void notifyViewModeChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });
        BNavigator.getInstance().startNav();

        // 初始化TTS. 开发者也可以使用独立TTS模块，不用使用导航SDK提供的TTS
        BNTTSPlayer.initPlayer();
        //设置TTS播放回调
        BNavigatorTTSPlayer.setTTSPlayerListener(new IBNTTSPlayerListener() {
            @Override
            public int playTTSText(String arg0, int arg1) {
                //开发者可以使用其他TTS的API
                return BNTTSPlayer.playTTSText(arg0, arg1);
            }
            @Override
            public void phoneHangUp() {
                //手机挂断
            }
            @Override
            public void phoneCalling() {
                //通话中
            }
            @Override
            public int getTTSState() {
                //开发者可以使用其他TTS的API,
                return BNTTSPlayer.getTTSState();
            }
        });

        BNRoutePlaner.getInstance().setObserver(new RoutePlanObserver(this, new RoutePlanObserver.IJumpToDownloadListener() {
            @Override
            public void onJumpToDownloadOfflineData() {
                // TODO Auto-generated method stub
            }
        }));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.navi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        BNavigator.getInstance().resume();
        super.onResume();
        BNMapController.getInstance().onResume();
    };

    @Override
    public void onPause() {
        BNavigator.getInstance().pause();
        super.onPause();
        BNMapController.getInstance().onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        BNavigator.getInstance().onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    public void onBackPressed(){
        BNavigator.getInstance().onBackPressed();
    }

    @Override
    public void onDestroy(){
        BNavigator.destory();
        BNRoutePlaner.getInstance().setObserver(null);
        super.onDestroy();
    }
}
