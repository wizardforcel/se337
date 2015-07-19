package com.wizard.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.location.*;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.navisdk.BNaviEngineManager;
import com.baidu.navisdk.BaiduNaviManager;
import com.wizard.myapplication.entity.Building;
import com.wizard.myapplication.entity.Campus;
import com.wizard.myapplication.entity.Event;
import com.wizard.myapplication.entity.NaviNode;
import com.wizard.myapplication.entity.User;
import com.wizard.myapplication.util.UrlConfig;
import com.wizard.myapplication.util.WizardHTTP;
import com.wizard.myapplication.view.SlideMenu;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainActivity extends Activity {

    private static final int ACTIVITY_REG = 0;
    private static final int ACTIVITY_LOGIN = 1;
    private static final int ACTIVITY_BUILDING = 2;
    private static final int ACTIVITY_CAMPUS = 3;
    private static final int ACTIVITY_SEARCH = 4;
    private static final int ACTIVITY_HISTORY = 5;

    private static final int GET_CAMPUS_SUCCESS = 0;
    private static final int GET_CAMPUS_FAIL = 1;

    private MapView mapView;
    private BaiduMap baiduMap;
    private List<Marker> markers = new ArrayList<Marker>();
    private LocationClient mLocationClient;

    private Handler handler;
    private SlideMenu slideMenu;
    private ImageView menuButton;
    private ImageView searchButton;
    private LinearLayout mapLinear;
    private TextView buildingText;

    private TextView loginMenuItem;
    private TextView regMenuItem;
    private TextView campusMenuItem;
    private TextView locMenuItem;
    private TextView userMenuItem;
    private TextView naviMenuItem;
    private TextView logoutMenuItem;
    private TextView followMenuItem;
    private TextView sjtuBusMenuItem;
    private TextView historyMenuItem;

    private Campus campus;
    private User user;
    private boolean onFollow = false;
    private boolean firstLoc = true;
    private LatLng lastLoc;
    private Building currentBuilding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initSideBar();
        initBaiduMap();
        initLocator();
        initNavi();

        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                MainActivity.this.handleMessage(msg);
            }
        };
    }

    private void handleMessage(Message msg)
    {
        Bundle b = msg.getData();
        int type = b.getInt("type");
        switch(type)
        {
            case GET_CAMPUS_SUCCESS:
                Toast.makeText(this, "获取校园信息成功！", Toast.LENGTH_SHORT).show();
                setCampusOnMap();
                break;
            case GET_CAMPUS_FAIL:
                Toast.makeText(this, "获取校园信息失败！" + b.getString("errmsg"), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    //初始化侧栏
    private void initSideBar() {
        slideMenu = (SlideMenu) findViewById(R.id.slide_menu);
        menuButton = (ImageView) findViewById(R.id.titlebar_menu_btn);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Menu");
                if (slideMenu.isMainScreenShowing())
                    slideMenu.openMenu();
                else
                    slideMenu.closeMenu();
            }
        });
        searchButton = (ImageView) findViewById(R.id.titlebar_menu_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { searchMenuItemOnClick(); }
        });

        loginMenuItem = (TextView) slideMenu.findViewById(R.id.loginMenu);
        regMenuItem = (TextView) slideMenu.findViewById(R.id.regMenu);
        campusMenuItem = (TextView) slideMenu.findViewById(R.id.campusMenu);
        locMenuItem = (TextView) slideMenu.findViewById(R.id.locMenu);
        followMenuItem = (TextView) slideMenu.findViewById(R.id.followMenu);
        userMenuItem = (TextView) slideMenu.findViewById(R.id.userMenu);
        naviMenuItem = (TextView) slideMenu.findViewById(R.id.naviMenu);
        logoutMenuItem = (TextView) slideMenu.findViewById(R.id.logoutMenu);
        sjtuBusMenuItem = (TextView) findViewById(R.id.sjtuBusMenu);
        historyMenuItem = (TextView) findViewById(R.id.historyMenu);

        setMenuStatus(false);
        campusMenuItem.setVisibility(View.GONE);
        sjtuBusMenuItem.setVisibility(View.GONE);

        loginMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginMenuItemOnClick();
            }
        });
        regMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regMenuItemOnClick();
            }
        });
        locMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locMenuItemOnClick();
            }
        });
        followMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followMenuItemOnClick();
            }
        });
        naviMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                naviMenuItemOnClick();
            }
        });
        logoutMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutMenuItemOnClick();
            }
        });
        campusMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { campusMenuItemOnClick(); }
        });
        sjtuBusMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { sjtuBusMenuItemOnClick(); }
        });
        userMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { userMenuItemOnClick(); }
        });
        historyMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { historyMenuItemOnClick(); }
        });
    }

    //初始化地图
    private void initBaiduMap() {

        mapLinear = (LinearLayout) findViewById(R.id.mapLinear);
        buildingText = (TextView) findViewById(R.id.buildingText);
        buildingText.setVisibility(TextView.GONE);
        buildingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { buildingTextOnClick(); }
        });

        mapView = (MapView) findViewById(R.id.bmapView);
        mapView.showScaleControl(false);
        mapView.showZoomControls(false);
        View child = mapView.getChildAt(1);
        if(child != null && child instanceof  ImageView)
            child.setVisibility(View.GONE);

        baiduMap = mapView.getMap();
        //普通地图
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return baiduMapOnMarkerClick(marker);
            }
        });
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) { baiduMapOnMapClick(); }
            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) { return false; }
        });
        baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(17));
        baiduMap.setMaxAndMinZoomLevel(20, 17);

    }

    private void buildingTextOnClick()
    {
        Intent i = new Intent(this, BuildingActivity.class);
        i.putExtra("building", currentBuilding);
        i.putExtra("user", user);
        startActivityForResult(i, ACTIVITY_BUILDING);
    }

    private void setCampusOnMap()
    {
        campusMenuItem.setVisibility(View.VISIBLE);
        sjtuBusMenuItem.setVisibility(View.VISIBLE);

        //设置中心点
        double lat = campus.getLatitude();
        double lng = campus.getLongitude();
        double r = campus.getRadius();
        baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(
                new LatLng(lat, lng)));
        //设置范围
        LatLng northeast = new LatLng(lat + r, lng + r);
        LatLng southwest = new LatLng(lat - r, lng - r);
        LatLngBounds bounds = new LatLngBounds.Builder().include(northeast).include(southwest).build();

        List<Building> buildings = campus.getBuildings();
        for (Building b : buildings) {
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_mark);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(new LatLng(b.getLatitude(), b.getLongitude()))
                    .icon(bitmap)
                    .title(String.valueOf(b.getId()));
            //在地图上添加Marker，并显示
            Marker marker = (Marker) baiduMap.addOverlay(option);
            markers.add(marker);
        }
    }

    // 初始化定位
    private void initLocator() {
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        //声明LocationClient类
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(new BDLocationListener() {
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

    private void baiduMapOnReceiveLocation(BDLocation location) {

        LatLng ll = new LatLng(location.getLatitude(),
                location.getLongitude());
        lastLoc = ll;
        Log.d("Location", "lat: " + ll.latitude + " lng: " + ll.longitude);

        MyLocationData locData = new MyLocationData.Builder()
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .accuracy(location.getRadius()).direction(100)
                .latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        baiduMap.setMyLocationData(locData);

        if (onFollow) //跟随模式
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(ll));

        if(firstLoc) //首次定位时获取校园信息
        {
            firstLoc = false;
            new Thread(new Runnable() {
                @Override
                public void run() { threadGetCampus(); }
            }).start();
        }

        //记录游览历史
        if(campus != null && user != null)
        {
            Building b = null;
            for(Building b2 : campus.getBuildings())
            {
                double diff = Math.sqrt(Math.pow(b2.getLatitude() - ll.latitude, 2) +
                        Math.pow(b2.getLongitude() - ll.longitude, 2));
                if(diff < 0.0001) {
                    Log.d("Covered", "id: " + b2.getId() + " name: " + b.getName());
                    b = b2;
                    break;
                }
            }
            if(b != null)
            {
                currentBuilding = b;
                new Thread(new Runnable() {
                    @Override
                    public void run() { threadAddHistory(); }
                }).start();
            }
        }
    }

    private void threadAddHistory()
    {
        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader();

            String retStr = http.httpGet("http://" + UrlConfig.HOST + "/addusertoview/view/" + currentBuilding.getId() + "/user/" + user.getId());
            if(retStr.equals(""))
                Log.d("AddHistoryFail", "uid: " + user.getId() + " viewId: " + currentBuilding.getId());
            else
                Log.d("AddHistorySuccess", "uid: " + user.getId() + " viewId: " + currentBuilding.getId());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Log.d("AddHistoryFail", "uid: " + user.getId() + " viewId: " + currentBuilding.getId());
        }
    }

    private void threadGetCampus()
    {
        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);
            http.setHeader("Content-Type", "application/json");
            http.setCharset("utf-8");

            //获取校园信息
            String url = String.format("http://%s/university/findByGPS/longitude/%6f/latitude/%6f",
                                       UrlConfig.HOST, lastLoc.longitude, lastLoc.latitude);
            String retStr = http.httpGet(url);
            JSONArray retArr = new JSONArray(retStr);
            JSONObject retJson = retArr.getJSONObject(0);

            Campus c = new Campus();
            c.setId(retJson.getInt("id"));
            c.setName(retJson.getString("name"));
            c.setContent(retJson.getString("description"));
            c.setRadius(retJson.getDouble("radius"));
            c.setLatitude(retJson.getDouble("latitude"));
            c.setLongitude(retJson.getDouble("longitude"));
            Log.d("Campus", "id: " + c.getId() + " name: " + c.getName());

            //获取景点信息
            retStr = http.httpGet("http://" + UrlConfig.HOST + "/view/university/" + c.getId());
            retArr = new JSONArray(retStr);

            List<Building> buildings = new ArrayList<Building>();
            for(int i = 0; i < retArr.length(); i++)
            {
                JSONObject o = retArr.getJSONObject(i);
                Building b = new Building();
                b.setId(o.getInt("id"));
                b.setName(o.getString("name"));
                b.setContent(o.getString("description"));
                b.setLatitude(o.getDouble("latitude"));
                b.setLongitude(o.getDouble("longitude"));
                b.setRadius(o.getDouble("radius"));
                buildings.add(b);
                Log.d("Building", "id: " + b.getId() + " name: " + b.getName());
            }
            c.setBuildings(buildings);
            campus = c;

            Bundle b = new Bundle();
            b.putInt("type", GET_CAMPUS_SUCCESS);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Bundle b = new Bundle();
            b.putInt("type", GET_CAMPUS_FAIL);
            b.putSerializable("errmsg", ex.getMessage());
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
    }

    private boolean baiduMapOnMarkerClick(Marker m) {
        Log.v("MarkerOnClick", m.getTitle());
        Building b = null;
        for (Building b2 : campus.getBuildings()) {
            if (b2.getId() == Integer.parseInt(m.getTitle())) {
                b = b2;
                break;
            }
        }
        currentBuilding = b;
        buildingText.setText(b.getName());
        buildingText.setVisibility(TextView.VISIBLE);
        Log.v("location", String.format("(%6f, %6f)", b.getLatitude(), b.getLongitude()));

        InfoWindow infoWindow;
        TextView location = new TextView(getApplicationContext());
        location.setBackgroundResource(R.drawable.location_tips);
        location.setPadding(30, 20, 30, 50);
        location.setText(b.getName());
        LayoutInflater flater = LayoutInflater.from(this);
        LatLng ll = m.getPosition();
        Button btn1=new Button(this);
        infoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(location), ll, -47,
                new InfoWindow.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick() {
                        baiduMap.hideInfoWindow();
                    }
                });
        baiduMap.showInfoWindow(infoWindow);

        return false;
    }

    private void baiduMapOnMapClick()
    {
        baiduMap.hideInfoWindow();
        buildingText.setVisibility(TextView.GONE);
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
        return true;
    }

    private void searchMenuItemOnClick()
    {
        Intent i = new Intent(this, SearchActivity.class);
        i.putExtra("buildings", (Serializable) campus.getBuildings());
        startActivityForResult(i, ACTIVITY_SEARCH);
        slideMenu.closeMenu();
    }

    private void loginMenuItemOnClick() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivityForResult(i, ACTIVITY_LOGIN);
    }

    private void regMenuItemOnClick() {
        Intent i = new Intent(this, RegActivity.class);
        startActivityForResult(i, ACTIVITY_REG);
    }

    private void logoutMenuItemOnClick() {
        user = null;
        setMenuStatus(false);
    }

    private void locMenuItemOnClick() {
        if (lastLoc != null)
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(lastLoc));
        slideMenu.closeMenu();
    }

    private void followMenuItemOnClick() {
        onFollow = !onFollow;
        followMenuItem.setText(onFollow ? "关闭跟随" : "跟随模式");
        slideMenu.closeMenu();
    }

    private void campusMenuItemOnClick()
    {
        Intent i = new Intent(this, CampusActivity.class);
        i.putExtra("campus", campus);
        i.putExtra("user", user);
        startActivityForResult(i, ACTIVITY_CAMPUS);
    }

    private void naviMenuItemOnClick() {
        if (campus.getBuildings().size() == 0) {
            Toast.makeText(this, "无任何景点", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent i = new Intent(this, NaviSettingActivity.class);
        ArrayList<NaviNode> nodes = new ArrayList<NaviNode>();
        for (Building b : campus.getBuildings()) {
            NaviNode n = new NaviNode();
            n.setName(b.getName());
            n.setLat(b.getLatitude());
            n.setLng(b.getLongitude());
            nodes.add(n);
        }
        i.putExtra("nodes", nodes);
        NaviNode myLoc = new NaviNode();
        myLoc.setName("我的位置");
        myLoc.setLat(lastLoc.latitude);
        myLoc.setLng(lastLoc.longitude);
        i.putExtra("myLoc", myLoc);
        startActivity(i);
    }

    private void historyMenuItemOnClick()
    {
        if(campus == null)
        {
            Toast.makeText(this, "获取校园信息失败！", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(this, HistoryActivity.class);
        i.putExtra("campusId", campus.getId());
        i.putExtra("allCount", campus.getBuildings().size());
        i.putExtra("user", user);
        startActivityForResult(i, ACTIVITY_HISTORY);
    }

    private void userMenuItemOnClick()
    {
        Intent i = new Intent(this, UserActivity.class);
        i.putExtra("user", user);
        startActivity(i);
    }

    private void sjtuBusMenuItemOnClick()
    {
        Intent i = new Intent(this, WebActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        Log.v("Result", requestCode + " " + resultCode);

        if ((requestCode == ACTIVITY_REG || requestCode == ACTIVITY_LOGIN ||
             requestCode == ACTIVITY_BUILDING || requestCode == ACTIVITY_CAMPUS) &&
                resultCode == Activity.RESULT_OK) {
            user = (User) i.getSerializableExtra("user");
            setMenuStatus(true);
        }
        else if((requestCode == ACTIVITY_SEARCH || requestCode == ACTIVITY_HISTORY) &&
                 resultCode == RESULT_OK) {
            int resultId = i.getIntExtra("resultId", 0);
            searchActivityOnOk(resultId);
        }
    }

    private void setMenuStatus(boolean isLogin) {
        //登录后
        userMenuItem.setVisibility(isLogin ? TextView.VISIBLE : TextView.GONE);
        logoutMenuItem.setVisibility(isLogin ? TextView.VISIBLE : TextView.GONE);
        historyMenuItem.setVisibility(isLogin ? TextView.VISIBLE : TextView.GONE);

        //登录前
        loginMenuItem.setVisibility(!isLogin ? TextView.VISIBLE : TextView.GONE);
        regMenuItem.setVisibility(!isLogin ? TextView.VISIBLE : TextView.GONE);
    }

    private void initNavi() {
        //初始化导航引擎
//		BaiduNaviManager.getInstance().initEngine(this, getSdcardDir(),
//		        mNaviEngineInitListener, ACCESS_KEY, mKeyVerifyListener);
        BaiduNaviManager.getInstance().initEngine(this, MainApplication.getSdcardDir(),
                new BNaviEngineManager.NaviEngineInitListener() {
                    public void engineInitSuccess() {}
                    public void engineInitStart() {}
                    public void engineInitFail() {}
                }, new LBSAuthManagerListener() {
                    @Override
                    public void onAuthResult(int status, String msg) {
                        String str = null;
                        if (0 == status)
                            str = "key校验成功!";
                        else
                            str = "key校验失败, " + msg;
                        Toast.makeText(MainActivity.this, str,
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void searchActivityOnOk(int id)
    {
        Marker m = null;
        for(Marker m2 : markers)
        {
            if(Integer.parseInt(m2.getTitle()) == id)
            {
                m = m2;
                break;
            }
        }
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(m.getPosition()));
        baiduMapOnMarkerClick(m);
    }

}
