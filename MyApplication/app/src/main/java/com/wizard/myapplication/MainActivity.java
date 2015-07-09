package com.wizard.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.*;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.wizard.myapplication.entity.Building;
import com.wizard.myapplication.entity.College;
import com.wizard.myapplication.entity.DataManager;
import com.wizard.myapplication.view.SlideMenu;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private static final int ACTIVITY_REG = 0;
    private static final int ACTIVITY_LOGIN = 1;

    private MapView mapView;
    private BaiduMap baiduMap;
    private List<Marker> markers = new ArrayList<Marker>();
    private LocationClient mLocationClient;

    private SlideMenu slideMenu;
    private ImageView menuButton;

    private TextView loginMenuItem;
    private TextView regMenuItem;
    private TextView collegeMenuItem;
    private TextView locMenuItem;
    private TextView userMenuItem;
    private TextView naviMenuItem;
    private TextView logoutMenuItem;
    private TextView followMenuItem;

    private College college = DataManager.getCollege("sjtu-mh");

    private String un;
    private boolean onFollow = false;
    private LatLng lastLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initSideBar();
        initBaiduMap();
        initLocator();
    }

    //初始化侧栏
    private void initSideBar()
    {
        slideMenu = (SlideMenu) findViewById(R.id.slide_menu);
        menuButton = (ImageView) findViewById(R.id.titlebar_menu_btn);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Menu");
                if(slideMenu.isMainScreenShowing())
                    slideMenu.openMenu();
                else
                    slideMenu.closeMenu();
            }
        });

        loginMenuItem = (TextView) slideMenu.findViewById(R.id.loginMenu);
        regMenuItem = (TextView) slideMenu.findViewById(R.id.regMenu);
        collegeMenuItem = (TextView) slideMenu.findViewById(R.id.collegeMenu);
        locMenuItem = (TextView) slideMenu.findViewById(R.id.locMenu);
        followMenuItem = (TextView) slideMenu.findViewById(R.id.followMenu);
        userMenuItem = (TextView) slideMenu.findViewById(R.id.userMenu);
        naviMenuItem = (TextView) slideMenu.findViewById(R.id.naviMenu);
        logoutMenuItem = (TextView) slideMenu.findViewById(R.id.logoutMenu);

        setMenuStatus(false);

        loginMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { loginMenuItemOnClick(); }
        });
        regMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { regMenuItemOnClick(); }
        });
        locMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { locMenuItemOnClick(); }
        });
        followMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { followMenuItemOnClick(); }
        });
        /*naviMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { naviMenuItemOnClick(); }
        });*/
        logoutMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { logoutMenuItemOnClick(); }
        });
    }

    //初始化地图
    private void initBaiduMap()
    {
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
    }

    // 初始化定位
    private void initLocator()
    {
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        //声明LocationClient类
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(new BDLocationListener(){
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                baiduMapOnReceiveLocation(bdLocation);
            }
        });
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    private void baiduMapOnReceiveLocation(BDLocation location)
    {
        //Log.v("Location", "Location");
        System.out.println("Location");

        LatLng ll = new LatLng(location.getLatitude(),
                location.getLongitude());
        lastLoc = ll;

        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        baiduMap.setMyLocationData(locData);

        if(onFollow)
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(ll));
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
        mLocationClient.start();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
        mLocationClient.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*getMenuInflater().inflate(R.menu.main, menu);

        loginMenuItem = menu.findItem(R.id.login_settings);
        regMenuItem = menu.findItem(R.id.reg_settings);
        selectMenuItem = menu.findItem(R.id.select_settings);
        locMenuItem = menu.findItem(R.id.loc_settings);
        userMenuItem = menu.findItem(R.id.user_settings);
        naviMenuItem = menu.findItem(R.id.navi_settings);
        logoutMenuItem = menu.findItem(R.id.logout_settings);
        followMenuItem = menu.findItem(R.id.follow_settings);
        setMenuStatus(false);*/

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
        if(lastLoc != null)
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(lastLoc));
        slideMenu.closeMenu();
    }

    private void followMenuItemOnClick()
    {
        onFollow = !onFollow;
        followMenuItem.setText(onFollow ? "关闭跟随" : "跟随模式");
        slideMenu.closeMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*Log.v("Menu", item.getTitle().toString());

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
        if(item.getItemId() == followMenuItem.getItemId())
        {
            followMenuItemOnClick();
            return true;
        }*/

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
        userMenuItem.setVisibility(isLogin? TextView.VISIBLE: TextView.GONE);
        logoutMenuItem.setVisibility(isLogin? TextView.VISIBLE: TextView.GONE);
        loginMenuItem.setVisibility(!isLogin? TextView.VISIBLE: TextView.GONE);
        regMenuItem.setVisibility(!isLogin? TextView.VISIBLE: TextView.GONE);
    }
}
