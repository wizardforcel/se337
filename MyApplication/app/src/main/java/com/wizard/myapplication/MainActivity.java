package com.wizard.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.baidu.mapapi.map.*;
import com.wizard.myapplication.entity.Building;
import com.wizard.myapplication.entity.College;
import com.wizard.myapplication.entity.DataManager;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private MapView mapView;
    private BaiduMap baiduMap;

    private College college = DataManager.getCollege("sjtu-mh");

    private List<Marker> markers = new ArrayList<Marker>();

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
                return BaiduMapOnMarkerClick(marker);
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

    private boolean BaiduMapOnMarkerClick(Marker m)
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
