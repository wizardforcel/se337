package com.wizard.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
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
import com.wizard.myapplication.entity.Result;
import com.wizard.myapplication.entity.User;
import com.wizard.myapplication.util.Api;
import com.wizard.myapplication.util.Common;
import com.wizard.myapplication.util.DistanceUtil;
import com.wizard.myapplication.util.TabUtil;
import com.wizard.myapplication.util.WizardHTTP;
import com.wizard.myapplication.view.CircularImage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    // activity request code table
    private static final int ACTIVITY_REG = 0;
    private static final int ACTIVITY_LOGIN = 1;
    private static final int ACTIVITY_BUILDING = 2;
    //private static final int ACTIVITY_CAMPUS = 3;
    private static final int ACTIVITY_SEARCH = 4;
    private static final int ACTIVITY_HISTORY = 5;
    private static final int ACTIVITY_PRE = 6;
    private static final int ACTIVITY_EVENT = 7;
    private static final int ACTIVITY_ADD_EVENT = 8;
    private static final int ACTIVITY_MY_EVENT = 9;
    private static final int ACTIVITY_CAMERA = 10;
    private static final int ACTIVITY_ALBUM = 11;

    // handler message table
    private static final int GET_CAMPUS_SUCCESS = 0;
    private static final int GET_CAMPUS_FAIL = 1;
    private static final int UPLOAD_AVATAR_SUCC = 2;
    private static final int UPLOAD_AVATAR_FAIL = 3;

    //tab
    private TabHost mainTab;

    //Page 1
    private MapView mapView;
    private BaiduMap baiduMap;
    private List<Marker> markers = new ArrayList<Marker>();
    private Polyline path;
    private LocationClient mLocationClient;
    private Handler handler;
    private LinearLayout mapLinear;

    private TextView locText;
    private TextView mapTypeText;
    private TextView routeText;
    private TextView busText;
    private TextView naviText;
    private TextView searchText;
    private TextView buildingText;
    private TextView scanText;

    //Page 2
    private ImageView collegeImage;
    private TextView contentText;
    private LinearLayout buildingPage;
    private LinearLayout eventPage;
    //private Handler handler;
    private TabHost tHost;
    private Button addEventButton;

    //Page 3
    private CircularImage userImage;
    private TextView unText;
    private TextView preText;
    private TextView rankText;
    private TextView taskText;
    private TextView exchangeText;
    private TextView loginText;
    private TextView logoutText;
    private TextView regText;
    private TextView hisText;
    private TextView myEventText;


    /*private SlideMenu slideMenu;
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
    private TextView presMenuItem;
    private TextView mapTypeMenuItem;*/

    // map options dialog
    private AlertDialog presDialog;
    private AlertDialog mapOptionsDialog;
    private Button _2DButton;
    private Button _3DButton;
    private Button normalButton;
    private Button satiButton;

    // avatar upload dialog
    private AlertDialog avatarUploadDialog;
    private Button albumButton;
    private Button cameraButton;

    private Campus campus;
    private List<Event> events
            = new ArrayList<Event>();
    private User user;
    private boolean onFollow = false;
    private boolean firstLoc = true;
    private boolean presShown = false;
    private LatLng lastLoc;
    private Building currentBuilding;
    private byte[] avatarToUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //init map dialog
        LinearLayout mapOptionsLinear
                = (LinearLayout) getLayoutInflater().inflate(R.layout.map_options_linear, null);
        normalButton = (Button) mapOptionsLinear.findViewById(R.id.normalButton);
        satiButton = (Button) mapOptionsLinear.findViewById(R.id.satiButton);
        _2DButton = (Button) mapOptionsLinear.findViewById(R.id._2DButton);
        _3DButton = (Button) mapOptionsLinear.findViewById(R.id._3DButton);
        normalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { normalButtonOnClick(); }
        });
        satiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { satiButtonOnClick(); }
        });
        _2DButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { _2DButtonOnClick(); }
        });
        _3DButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { _3DButtonOnClick(); }
        });

        mapOptionsDialog = new AlertDialog.Builder(this)
                            .setView(mapOptionsLinear)
                            .setNegativeButton("取消", null)
                            .create();

        //init avatar dialog
        LinearLayout avatarUploadLinear
                = (LinearLayout) getLayoutInflater().inflate(R.layout.avatar_upload_linear, null);
        cameraButton = (Button) avatarUploadLinear.findViewById(R.id.cameraButton);
        albumButton = (Button) avatarUploadLinear.findViewById(R.id.albumButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { cameraButtonOnClick(); }
        });
        albumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { albumButtonOnClick(); }
        });
        avatarUploadDialog = new AlertDialog.Builder(this)
                .setView(avatarUploadLinear)
                .setNegativeButton("取消", null)
                .create();


        //initSideBar();
        initTab();
        initCampusPage();
        initUserPage();
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
                setCampusOnPage2();
                break;
            case GET_CAMPUS_FAIL:
                Toast.makeText(this, "获取校园信息失败！" + b.getString("errmsg"), Toast.LENGTH_SHORT).show();
                break;
            case UPLOAD_AVATAR_FAIL:
                Toast.makeText(this, "头像上传失败！" + b.getString("errmsg"), Toast.LENGTH_SHORT).show();
                break;
            case UPLOAD_AVATAR_SUCC:
                Toast.makeText(this, "头像上传成功！", Toast.LENGTH_SHORT).show();
                userImage.setImageBitmap(BitmapFactory.decodeByteArray(user.getAvatar(), 0, user.getAvatar().length));
                break;
        }
    }

    private void initTab()
    {
        mainTab = (TabHost) findViewById(R.id.mainTab);
        mainTab.setup();
        mainTab.addTab(mainTab.newTabSpec("地图").setIndicator("地图").setContent(R.id.mainPage1));
        mainTab.addTab(mainTab.newTabSpec("校区").setIndicator("校区").setContent(R.id.mainPage2));
        mainTab.addTab(mainTab.newTabSpec("用户").setIndicator("用户").setContent(R.id.mainPage3));
        mainTab.setCurrentTab(0);
        TabUtil.updateTab(mainTab);
        mainTab.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) { TabUtil.updateTab(MainActivity.this.mainTab); }
        });
    }

    //==========================================page3===========================================

    private void initUserPage()
    {
        userImage = (CircularImage) findViewById(R.id.userImage);
        unText = (TextView) findViewById(R.id.unText);
        preText = (TextView) findViewById(R.id.preText);
        rankText = (TextView) findViewById(R.id.rankText);
        taskText = (TextView) findViewById(R.id.taskText);
        exchangeText = (TextView) findViewById(R.id.exchangeText);
        loginText = (TextView) findViewById(R.id.loginText);
        logoutText = (TextView) findViewById(R.id.logoutText);
        regText = (TextView) findViewById(R.id.regText);
        hisText = (TextView) findViewById(R.id.hisText);
        myEventText= (TextView) findViewById(R.id.myEventText);

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { avatarImageOnClick(); }
        });
        preText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preTextOnClick();
            }
        });
        rankText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rankTextOnClick();
            }
        });
        rankText.setVisibility(View.GONE);
        exchangeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exchangeTextOnClick();
            }
        });
        taskText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTextOnClick();
            }
        });
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginMenuItemOnClick();
            }
        });
        logoutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutMenuItemOnClick();
            }
        });
        regText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regMenuItemOnClick();
            }
        });
        hisText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                historyMenuItemOnClick();
            }
        });
        myEventText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myEventMenuItemOnClick();
            }
        });

        setLoginStatus(false);
    }

    private void preTextOnClick(){
        Intent i = new Intent(this, PreferenceActivity.class);
        i.putExtra("user", user);
        startActivityForResult(i, ACTIVITY_PRE);
    }

    private void exchangeTextOnClick()
    {
        Intent i = new Intent(this, ExchangeActivity.class);
        startActivity(i);
    }

    private void taskTextOnClick()
    {
        Intent i = new Intent(this, AccomActivity.class);
        startActivity(i);
    }

    private void rankTextOnClick()
    {
        Intent i = new Intent(this, RankActivity.class);
        startActivity(i);
    }

    //=======================================end page3===========================================

    //===========================================page2===========================================

    private void initCampusPage()
    {
        contentText = (TextView) findViewById(R.id.contentText);
        buildingPage = (LinearLayout) findViewById(R.id.buildingsPage);
        eventPage = (LinearLayout) findViewById(R.id.eventPage);
        collegeImage = (ImageView) findViewById(R.id.collegeImage);
        addEventButton = (Button) findViewById(R.id.addEventButton);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { addEventButtonOnClick(); }
        });

        tHost = (TabHost) findViewById(R.id.tabHost);
        tHost.setup();
        tHost.addTab(tHost.newTabSpec("简介").setIndicator("简介").setContent(R.id.contentPage0));
        tHost.addTab(tHost.newTabSpec("景点").setIndicator("景点").setContent(R.id.buildingsPage0));
        tHost.addTab(tHost.newTabSpec("活动").setIndicator("活动").setContent(R.id.eventPage0));
        tHost.setCurrentTab(0);
        TabUtil.updateTab(tHost);
        tHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) { TabUtil.updateTab(tHost); }
        });
    }

    private void addEventButtonOnClick()
    {
        if(user == null)
        {
            Intent i = new Intent(this, LoginActivity.class);
            startActivityForResult(i, ACTIVITY_LOGIN);
        }
        else {
            Intent i = new Intent(this, AddEventActivity.class);
            i.putExtra("user", user);
            i.putExtra("campus", campus);
            startActivityForResult(i, ACTIVITY_ADD_EVENT);
        }

    }

    private void setCampusOnPage2()
    {
        contentText.setText(campus.getContent());
        collegeImage.setImageBitmap(BitmapFactory.decodeByteArray(campus.getAvatar(), 0, campus.getAvatar().length));
        setEvents();
        setBuildings();
    }

    private void setEvents()
    {
        for(int i = 0; i < events.size(); i++){
            final Event e = events.get(i);
            addEventToView(e);
        }
    }

    private void eventTextOnClick(Event e)
    {
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra("event", e);
        intent.putExtra("user", user);
        intent.putExtra("campusId", campus.getId());
        startActivityForResult(intent, ACTIVITY_EVENT);
    }

    private void setBuildings(){
        List<Building> buildings  = campus.getBuildings();
        for(int row = 0; row < buildings.size(); row++){
            final Building building = buildings.get(row);
            Log.v("building", building.getName());

            LinearLayout linear
                    = (LinearLayout) getLayoutInflater().inflate(R.layout.building_linear, null);
            TextView buildingText
                    = (TextView) linear.findViewById(R.id.buildingText);
            buildingText.setText(building.getName());
            buildingText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { buildingTextOnClick(building); }
            });
            buildingPage.addView(linear);
        }
    }

    private void buildingTextOnClick(Building b)
    {
        Intent intent = new Intent(this, BuildingActivity.class);
        intent.putExtra("building", b);
        intent.putExtra("user", user);
        intent.putExtra("campusId", campus.getId());
        startActivityForResult(intent, 0);
    }

    //============================================end page 2======================================

    //初始化侧栏
    /*private void initSideBar() {
        slideMenu = (SlideMenu) findViewById(R.id.slide_menu);
        ImageView menuButton = (ImageView) findViewById(R.id.titlebar_menu_btn);
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
        ImageView searchButton = (ImageView) findViewById(R.id.titlebar_menu_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchMenuItemOnClick();
            }
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
        presMenuItem = (TextView) findViewById(R.id.presMenu);
        mapTypeMenuItem = (TextView) findViewById(R.id.mapTypeMenu);

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
        presMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { presMenuItemOnClick(); }
        });
        mapTypeMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { mapTypeMenuItemOnClick(); }
        });
    }*/

    //初始化地图
    private void initBaiduMap() {

        mapLinear = (LinearLayout) findViewById(R.id.mapLinear);
        buildingText = (TextView) findViewById(R.id.buildingText);
        buildingText.setVisibility(TextView.GONE);
        buildingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { buildingTextOnClick(); }
        });

        busText = (TextView) findViewById(R.id.busText);
        locText = (TextView) findViewById(R.id.locText);
        naviText = (TextView) findViewById(R.id.naviText);
        routeText = (TextView) findViewById(R.id.routeText);
        mapTypeText = (TextView) findViewById(R.id.mapTypeText);
        searchText = (TextView) findViewById(R.id.searchText);
        scanText = (TextView) findViewById(R.id.scanText);

        busText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sjtuBusMenuItemOnClick();
            }
        });
        locText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locMenuItemOnClick();
            }
        });
        naviText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                naviMenuItemOnClick();
            }
        });
        routeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presMenuItemOnClick();
            }
        });
        mapTypeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapTypeMenuItemOnClick();
            }
        });
        searchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchMenuItemOnClick();
            }
        });
        scanText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanMenuItemOnClick();
            }
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
        //campusMenuItem.setVisibility(View.VISIBLE);
        //sjtuBusMenuItem.setVisibility(View.VISIBLE);

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

            if(!Api.addHistory(http, user.getId(), campus.getId()))
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

            campus = Api.getCampus(http, lastLoc.latitude, lastLoc.longitude);
            events = Api.getActiivity(http, campus.getId());

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
    }

    private void locMenuItemOnClick() {
        if (lastLoc != null)
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(lastLoc));
    }

   /*private void followMenuItemOnClick() {
        onFollow = !onFollow;
        followMenuItem.setText(onFollow ? "关闭跟随" : "跟随模式");
        slideMenu.closeMenu();
    }*/

    /*private void campusMenuItemOnClick()
    {
        Intent i = new Intent(this, CampusActivity.class);
        i.putExtra("campus", campus);
        i.putExtra("user", user);
        startActivityForResult(i, ACTIVITY_CAMPUS);
        slideMenu.closeMenu();
    }*/

    private void naviMenuItemOnClick() {
        if(campus == null)
        {
            Toast.makeText(this, "获取校园信息失败", Toast.LENGTH_SHORT).show();
            return;
        }
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

    private void scanMenuItemOnClick()
    {
        // 这些代码是启动另外的一个应用程序的主Activity
        ComponentName componetName = new ComponentName(
                // 这个是另外一个应用程序的包名 ,androidmanifest.xml中的package值！！！
                "com.hy.ProductName",
                // 这个参数是要启动的Activity （主activity）
                "com.unity3d.player.UnityPlayerNativeActivity");
        try {
            Intent intent = new Intent();
            intent.setComponent(componetName);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"没有找到应用程序", Toast.LENGTH_SHORT).show();
        }
    }

    private void presMenuItemOnClick()
    {
        if(campus == null)
        {
            Toast.makeText(this, "校园信息获取失败！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(user == null)
        {
            Toast.makeText(this, "用户未登录！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(user.getPres().size() == 0)
        {
            Toast.makeText(this, "无用户偏好。", Toast.LENGTH_SHORT).show();
            return;
        }
        if(lastLoc == null)
        {
            Toast.makeText(this, "无法获取当前位置。", Toast.LENGTH_SHORT).show();
            return;
        }

        if(presShown)
        {
            hidePres();
            routeText.setText("推荐路线");
            presShown = !presShown;
        }
        else
        {
            showPres();
        }
    }



    /*private void userMenuItemOnClick()
    {
        Intent i = new Intent(this, UserActivity.class);
        i.putExtra("user", user);
        startActivityForResult(i, ACTIVITY_PRE);
        slideMenu.closeMenu();
    }*/

    private void sjtuBusMenuItemOnClick()
    {
        Intent i = new Intent(this, WebActivity.class);
        startActivity(i);
    }

    private void myEventMenuItemOnClick()
    {
        if(campus == null)
        {
            Toast.makeText(this, "获取校园信息失败！", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(this, MyEventActivity.class);
        i.putExtra("campus", campus);
        i.putExtra("user", user);
        startActivityForResult(i, ACTIVITY_MY_EVENT);
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
        setLoginStatus(false);
    }

    private void setLoginStatus(boolean isLogin)
    {
        //登录后
        logoutText.setVisibility(isLogin ? TextView.VISIBLE : TextView.GONE);
        preText.setVisibility(isLogin ? TextView.VISIBLE : TextView.GONE);
        hisText.setVisibility(isLogin ? TextView.VISIBLE : TextView.GONE);
        taskText.setVisibility(isLogin ? TextView.VISIBLE : TextView.GONE);
        exchangeText.setVisibility(isLogin ? TextView.VISIBLE : TextView.GONE);
        myEventText.setVisibility(isLogin ? TextView.VISIBLE : TextView.GONE);

        //登录前
        loginText.setVisibility(!isLogin ? TextView.VISIBLE : TextView.GONE);
        regText.setVisibility(!isLogin ? TextView.VISIBLE : TextView.GONE);

        if(isLogin)
        {
            unText.setText(user.getUn());
            userImage.setImageBitmap(BitmapFactory.decodeByteArray(user.getAvatar(), 0, user.getAvatar().length));
        }
        else {
            unText.setText("请登录");
            userImage.setImageBitmap(BitmapFactory.decodeByteArray(new byte[0], 0, 0));
        }
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
             requestCode == ACTIVITY_BUILDING || requestCode == ACTIVITY_EVENT ||
             requestCode == ACTIVITY_MY_EVENT) &&
             resultCode == RESULT_OK) {
            user = (User) i.getSerializableExtra("user");
            setLoginStatus(true);
        }
        else if((requestCode == ACTIVITY_SEARCH || requestCode == ACTIVITY_HISTORY) &&
                 resultCode == RESULT_OK) {
            mainTab.setCurrentTab(0);
            int resultId = i.getIntExtra("resultId", 0);
            searchActivityOnOk(resultId);
        }
        else if(requestCode == ACTIVITY_PRE && resultCode == RESULT_OK)
        {
            List<String> pres = (List<String>) i.getSerializableExtra("pres");
            user.setPres(pres);
        }
        else if(requestCode == ACTIVITY_ADD_EVENT && resultCode == RESULT_OK)
        {
            final Event e = (Event) i.getSerializableExtra("event");
            events.add(e);
            addEventToView(e);
        }
        else if((requestCode == ACTIVITY_ALBUM || requestCode == ACTIVITY_CAMERA) &&
                 resultCode == RESULT_OK)
        {
            if (i == null) return;
            //取得返回的Uri,基本上选择照片的时候返回的是以Uri形式，但是在拍照中有得机子呢Uri是空的，所以要特别注意
            Uri mImageCaptureUri = i.getData();
            //返回的Uri不为空时，那么图片信息数据都会在Uri中获得。如果为空，那么我们就进行下面的方式获取
            if (mImageCaptureUri != null) {
                try {
                    //这个方法是根据Uri获取Bitmap图片的静态方法
                    Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
                    if (image == null) return;
                    avatarToUpload = Common.bmpToByteArr(image);
                    new Thread(new Runnable() {
                        @Override
                        public void run() { threadUploadAvatar(); }
                    }).start();
                } catch (Exception e)
                { e.printStackTrace(); }

            } else {
                Bundle extras = i.getExtras();
                if (extras == null) return;
                //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
                Bitmap image = extras.getParcelable("data");
                if (image == null) return;
                avatarToUpload = Common.bmpToByteArr(image);
                new Thread(new Runnable() {
                    @Override
                    public void run() { threadUploadAvatar(); }
                }).start();
            }
        }
    }

    private void addEventToView(final Event e)
    {
        LinearLayout linear
                = (LinearLayout) getLayoutInflater().inflate(R.layout.event_linear, null);
        TextView unText = (TextView) linear.findViewById(R.id.unText);
        TextView titleText = (TextView) linear.findViewById(R.id.titleText);
        ImageView avatarImage = (ImageView) linear.findViewById(R.id.avatarImage);
        titleText.setText(e.getName());
        unText.setText(e.getUn());
        avatarImage.setImageBitmap(BitmapFactory.decodeByteArray(e.getAvatar(), 0, e.getAvatar().length));
        linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { eventTextOnClick(e); }
        });
        eventPage.addView(linear);
    }

    /*private void setMenuStatus(boolean isLogin) {
        //登录后
        userMenuItem.setVisibility(isLogin ? TextView.VISIBLE : TextView.GONE);
        logoutMenuItem.setVisibility(isLogin ? TextView.VISIBLE : TextView.GONE);
        historyMenuItem.setVisibility(isLogin ? TextView.VISIBLE : TextView.GONE);
        presMenuItem.setVisibility(isLogin ? TextView.VISIBLE : TextView.GONE);

        //登录前
        loginMenuItem.setVisibility(!isLogin ? TextView.VISIBLE : TextView.GONE);
        regMenuItem.setVisibility(!isLogin ? TextView.VISIBLE : TextView.GONE);
    }*/

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

    private void showPres()
    {
        /*Map<Integer, Building> buildingIndexMap = new HashMap<Integer, Building>();
        for(Building b : campus.getBuildings())
            buildingIndexMap.put(b.getId(), b);
        Map<String, Integer> typeToIconMap = new HashMap<String, Integer>();
        typeToIconMap.put(BuildingType.HISTORY, R.drawable.icon_mark_history);
        typeToIconMap.put(BuildingType.ACADAMIC, R.drawable.icon_mark_academic);
        typeToIconMap.put(BuildingType.FOOD, R.drawable.icon_mark_food);
        typeToIconMap.put(BuildingType.SCENE, R.drawable.icon_mark_scene);
        typeToIconMap.put(BuildingType.SPORT, R.drawable.icon_mark_sports);

        for(Marker m : markers)
        {
            Building b = buildingIndexMap.get(Integer.parseInt(m.getTitle()));
            if(b == null) continue;
            if(!typeToIconMap.containsKey(b.getType()))
                continue;
            if(!user.getPres().contains(b.getType()))
                continue;
            int resId = typeToIconMap.get(b.getType());
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(resId);
            m.setIcon(bitmap);
        }*/

        presDialog = new AlertDialog.Builder(this)
                .setSingleChoiceItems(user.getPres().toArray(new String[0]), -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                presAlertDialogOnItemClick(dialogInterface, i);
            }
        }).setNegativeButton("取消", null).create();

        presDialog.show();
    }

    private void presAlertDialogOnItemClick(DialogInterface dialogInterface, int i)
    {
        String pre = user.getPres().get(i);
        LatLng myLoc = lastLoc;
        List<Building> li = new ArrayList<Building>();
        for(Building b : campus.getBuildings())
        {
            if(b.getType().equals(pre))
                li.add(b);
        }
        li = DistanceUtil.sort(myLoc.latitude, myLoc.longitude, li);

        List<LatLng> pts = new ArrayList<LatLng>();
        pts.add(myLoc);
        for(Building b : li)
            pts.add(new LatLng(b.getLatitude(), b.getLongitude()));
        PolylineOptions options = new PolylineOptions().width(15).color(0xAAFF9966).points(pts);
        path = (Polyline) baiduMap.addOverlay(options);

        routeText.setText("隐藏推荐");
        presShown = !presShown;
        presDialog.hide();
    }

    private void hidePres()
    {
        /*BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_mark);
        for(Marker m : markers)
            m.setIcon(bitmap);*/

        path.remove();
        path = null;
    }

    private void mapTypeMenuItemOnClick()
    {
        mapOptionsDialog.show();
    }

    private void _2DButtonOnClick()
    {
        baiduMap.setMapStatus(
                MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().overlook(0).build()));
        mapOptionsDialog.hide();
    }

    private void _3DButtonOnClick()
    {
        baiduMap.setMapStatus(
                MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().overlook(-45).build()));
        mapOptionsDialog.hide();
    }
    private void normalButtonOnClick()
    {
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mapOptionsDialog.hide();
    }

    private void satiButtonOnClick()
    {
        baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        mapOptionsDialog.hide();
    }

    private void avatarImageOnClick()
    {
        if(user == null) return;
        avatarUploadDialog.show();
    }


    private void cameraButtonOnClick()
    {
        try {
            //拍照我们用Action为MediaStore.ACTION_IMAGE_CAPTURE，
            //有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
            //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent, ACTIVITY_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        avatarUploadDialog.hide();
    }

    private void albumButtonOnClick()
    {
        try {
            //选择照片的时候也一样，我们用Action为Intent.ACTION_GET_CONTENT，
            //有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, ACTIVITY_ALBUM);
        } catch (Exception e) {
            e.printStackTrace();
        }
        avatarUploadDialog.hide();
    }

    private void threadUploadAvatar()
    {

        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader();
            http.setCharset("utf-8");

            Result r = Api.uploadAvatar(http, user.getId(), avatarToUpload);

            if(r.getErrno() == 0)
            {
                user.setAvatar(avatarToUpload);
                Bundle b = new Bundle();
                b.putInt("type", UPLOAD_AVATAR_SUCC);
                Message msg = handler.obtainMessage();
                msg.setData(b);
                handler.sendMessage(msg);
            }
            else
            {
                Bundle b = new Bundle();
                b.putInt("type", UPLOAD_AVATAR_FAIL);
                b.putSerializable("errmsg", r.getErrmsg());
                Message msg = handler.obtainMessage();
                msg.setData(b);
                handler.sendMessage(msg);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Bundle b = new Bundle();
            b.putInt("type", UPLOAD_AVATAR_FAIL);
            b.putSerializable("errmsg", ex.getMessage());
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
    }
}
