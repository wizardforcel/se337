package com.wizard.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.baidu.location.*;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.wizard.myapplication.entity.Building;
import com.wizard.myapplication.entity.College;
import com.wizard.myapplication.entity.DataManager;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private static final int ACTIVITY_REG = 0;
    private static final int ACTIVITY_LOGIN = 1;

    private MapView mapView;
    private BaiduMap baiduMap;
    private List<Marker> markers = new ArrayList<Marker>();
    private LocationClient mLocationClient;

    private MenuItem loginMenuItem;
    private MenuItem regMenuItem;
    private MenuItem selectMenuItem;
    private MenuItem locMenuItem;
    private MenuItem userMenuItem;
    private MenuItem naviMenuItem;
    private MenuItem logoutMenuItem;

    private College college = DataManager.getCollege("sjtu-mh");

    private String un;
    private boolean onLocation = false;
    private boolean isFirstLoc = true;

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
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return baiduMapOnMarkerClick(marker);
            }
        });

        //设置中心点
        baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(college.getCenter(), 17));
        List<Building> buildings = college.getBuildings();
        for(Building b : buildings)
        {
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_mark);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(b.getCenter())
                    .icon(bitmap)
                    .title(b.getId());
            //在地图上添加Marker，并显示
            Marker marker = (Marker) baiduMap.addOverlay(option);
            markers.add(marker);
        }

        //定位
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new BDLocationListener(){
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                baiduMapOnReceiveLocation(bdLocation);
            }
        });
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(200);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    private void baiduMapOnReceiveLocation(BDLocation location)
    {
        //Log.v("Location", "Location");
        System.out.println("Location");

        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        baiduMap.setMyLocationData(locData);

        if(isFirstLoc) {
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(ll));
            isFirstLoc = false;
        }
    }

    private boolean baiduMapOnMarkerClick(Marker m)
    {
        Log.v("MarkerOnClick", m.getTitle());
        Building b = null;
        for(Building b2 : college.getBuildings())
        {
            if(b2.getId().equals(m.getTitle()))
            {
                b = b2;
                break;
            }
        }

        Intent intent = new Intent(this, BuildingActivity.class);
        intent.putExtra("name", b.getName());
        intent.putExtra("id", b.getId());
        intent.putExtra("content", b.getContent());
        startActivity(intent);

        return false;
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
        getMenuInflater().inflate(R.menu.main, menu);

        loginMenuItem = menu.findItem(R.id.login_settings);
        regMenuItem = menu.findItem(R.id.reg_settings);
        selectMenuItem = menu.findItem(R.id.select_settings);
        locMenuItem = menu.findItem(R.id.loc_settings);
        userMenuItem = menu.findItem(R.id.user_settings);
        naviMenuItem = menu.findItem(R.id.navi_settings);
        logoutMenuItem = menu.findItem(R.id.logout_settings);
        setMenuStatus(false);

        return true;
    }

    private void loginMenuItemOnClick()
    {
        Intent i = new Intent(this, LoginActivity.class);
        startActivityForResult(i, ACTIVITY_LOGIN);
    }

    private void regMenuItemOnClick()
    {
        Intent i = new Intent(this, RegActivity.class);
        startActivityForResult(i, ACTIVITY_REG);
    }

    private void logoutMenuItemOnClick()
    {
        un = "";
        setMenuStatus(false);
    }

    private void locMenuItemOnClick()
    {
        onLocation = !onLocation;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.v("Menu", item.getTitle().toString());

        if(item.getItemId() == loginMenuItem.getItemId())
        {
            loginMenuItemOnClick();
            return true;
        }
        if(item.getItemId() == regMenuItem.getItemId())
        {
            regMenuItemOnClick();
            return true;
        }
        if(item.getItemId() == logoutMenuItem.getItemId())
        {
            logoutMenuItemOnClick();
            return true;
        }
        if(item.getItemId() == locMenuItem.getItemId())
        {
            locMenuItemOnClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i)
    {
        Log.v("Result", requestCode + " " + resultCode);

        if((requestCode == ACTIVITY_REG || requestCode == ACTIVITY_LOGIN) &&
            resultCode == Activity.RESULT_OK)
        {
            un = i.getStringExtra("un");
            setMenuStatus(true);
        }
    }

    private void setMenuStatus(boolean isLogin)
    {
        userMenuItem.setVisible(isLogin);
        logoutMenuItem.setVisible(isLogin);
        loginMenuItem.setVisible(!isLogin);
        regMenuItem.setVisible(!isLogin);
    }
}
