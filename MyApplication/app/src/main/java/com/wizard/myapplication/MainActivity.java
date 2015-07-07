package com.wizard.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.wizard.myapplication.entity.College;
import com.wizard.myapplication.entity.DataManager;


public class MainActivity extends Activity {

    private MapView mapView;
    private BaiduMap baiduMap;

    private College college = DataManager.getCollege("sjtu-mh");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.bmapView);
        mapView.showScaleControl(false);
        mapView.showZoomControls(false);

        baiduMap = mapView.getMap();
        //普通地图
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        //设置中心点
        baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(college.getCenter(), 17));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
