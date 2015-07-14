package com.wizard.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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

import com.wizard.myapplication.entity.Building;
import com.wizard.myapplication.entity.Campus;
import com.wizard.myapplication.entity.User;
import com.wizard.myapplication.util.UrlConfig;
import com.wizard.myapplication.util.WizardHTTP;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;

public class CampusActivity extends Activity {

    private static final int GET_IMAGE_SUCCESS = 0;
    private static final int GET_IMAGE_FAIL = 1;

    private ImageView collegeImage;
    private TextView contentText;
    private LinearLayout buildingPage;
    private Handler handler;
    private TabHost tHost;

    private Campus campus;
    private List<Building> buildings;
    private User user;
    boolean imgLoaded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_campus);

        contentText = (TextView) findViewById(R.id.contentText);
        buildingPage = (LinearLayout) findViewById(R.id.buildingsPage);
        collegeImage = (ImageView) findViewById(R.id.collegeImage);

        tHost = (TabHost) findViewById(R.id.tabHost);
        tHost.setup();
        tHost.addTab(tHost.newTabSpec("简介").setIndicator("简介").setContent(R.id.contentPage));
        tHost.addTab(tHost.newTabSpec("景点").setIndicator("景点").setContent(R.id.buildingsPage));
        tHost.addTab(tHost.newTabSpec("活动").setIndicator("活动").setContent(R.id.activityPage));
        tHost.setCurrentTab(0);

        Intent i = getIntent();
        campus = (Campus) i.getSerializableExtra("campus");
        buildings = campus.getBuildings();
        user = (User) i.getSerializableExtra("user");
        contentText.setText(campus.getContent());
        setBuildingTable();

        TextView tv = (TextView) findViewById(R.id.titlebar_name);
        tv.setText(campus.getName());
        Button returnButton = (Button) findViewById(R.id.titlebar_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });

        handler = new Handler()
        {
            @Override
            public void handleMessage(android.os.Message msg) {
                CampusActivity.this.handleMessage(msg);
            }
        };


        if(!imgLoaded) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    threadGetImage();
                }
            }).start();
        }
    }

    private void handleMessage(android.os.Message msg)
    {
        Bundle b = msg.getData();
        int type = b.getInt("type");
        switch(type)
        {
            case GET_IMAGE_SUCCESS:
                imgLoaded = true;
                byte[] img = b.getByteArray("img");
                collegeImage.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
                break;
            case GET_IMAGE_FAIL:
                Toast.makeText(CampusActivity.this, "图片加载失败！" + b.getString("srrmag"), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void threadGetImage()
    {
        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);
            String retStr = http.httpGet("http://" + UrlConfig.HOST + "/picture/university/" + campus.getId());
            JSONArray retArr = new JSONArray(retStr);
            if(retArr.length() == 0)
                return;

            JSONObject retJson = retArr.getJSONObject(0);
            String imgPath = retJson.getString("path");
            imgPath = "http://" + UrlConfig.HOST + "/picture/" + imgPath.replace(".", "/");
            byte[] imgData = http.httpGetData(imgPath);

            Bundle b = new Bundle();
            b.putByteArray("img", imgData);
            b.putInt("type", GET_IMAGE_SUCCESS);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Bundle b = new Bundle();
            b.putInt("type", GET_IMAGE_FAIL);
            b.putString("errmsg", ex.getMessage());
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
    }

    private void setBuildingTable(){
        for(int row = 0; row < buildings.size(); row++){
            final Building building = buildings.get(row);
            Log.v("building", building.getName());

            TextView buildingText = new TextView(this);
            buildingText.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            buildingText.setPadding(10, 10, 10, 10);
            buildingText.setText(building.getName());
            buildingText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buildingTextOnClick(building);
                }
            });
            buildingPage.addView(buildingText);
        }
    }

    private void buildingTextOnClick(Building b)
    {
        Log.d("building", b.getName());
        Intent intent = new Intent(CampusActivity.this, BuildingActivity.class);
        intent.putExtra("building", b);
        intent.putExtra("user", user);
        startActivityForResult(intent, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            setResult(Activity.RESULT_OK, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }
}
