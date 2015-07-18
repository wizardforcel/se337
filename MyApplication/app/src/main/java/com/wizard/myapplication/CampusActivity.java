package com.wizard.myapplication;

import android.app.Activity;
import android.content.Intent;
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
import com.wizard.myapplication.entity.Event;
import com.wizard.myapplication.entity.User;
import com.wizard.myapplication.util.UrlConfig;
import com.wizard.myapplication.util.WizardHTTP;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CampusActivity extends Activity {

    private static final int LOAD_DATA_SUCCESS = 0;
    private static final int LOAD_DATA_FAIL = 1;

    private static final int ACTIVITY_BUILDING = 0;
    private static final int ACTIVITY_EVENT = 1;
    private static final int ACTIVITY_ADD_EVENT = 2;
    private static final int ACTIVITY_LOGIN = 3;

    private ImageView collegeImage;
    private TextView contentText;
    private LinearLayout buildingPage;
    private LinearLayout eventPage;
    private Handler handler;
    private TabHost tHost;
    private Button addEventButton;

    private Campus campus;
    private List<Building> buildings;
    private List<Event> events
            = new ArrayList<Event>();
    private User user;
    boolean loaded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_campus);

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
        tHost.addTab(tHost.newTabSpec("简介").setIndicator("简介").setContent(R.id.contentPage));
        tHost.addTab(tHost.newTabSpec("景点").setIndicator("景点").setContent(R.id.buildingsPage));
        tHost.addTab(tHost.newTabSpec("活动").setIndicator("活动").setContent(R.id.eventPage0));
        tHost.setCurrentTab(0);

        Intent i = getIntent();
        campus = (Campus) i.getSerializableExtra("campus");
        buildings = campus.getBuildings();
        contentText.setText(campus.getContent());
        setBuildings();
        user = (User) i.getSerializableExtra("user");

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

        if(!loaded) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    threadLoadData();
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
            case LOAD_DATA_SUCCESS:
                loaded = true;
                byte[] img = b.getByteArray("img");
                if(img != null)
                    collegeImage.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
                setEvents();
                break;
            case LOAD_DATA_FAIL:
                Toast.makeText(CampusActivity.this, "图片加载失败！" + b.getString("srrmag"), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setEvents()
    {
        for(int i = 0; i < events.size(); i++){
            final Event e = events.get(i);
            TextView tv
                    = (TextView) getLayoutInflater().inflate(R.layout.text, null);
            tv.setText(e.getName());
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { eventTextOnClick(e); }
            });
            eventPage.addView(tv);
        }
    }

    private void eventTextOnClick(Event e)
    {
        Intent intent = new Intent(CampusActivity.this, EventActivity.class);
        intent.putExtra("event", e);
        intent.putExtra("user", user);
        startActivityForResult(intent, 0);
    }

    private void threadLoadData()
    {
        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);
            http.setCharset("utf-8");
            String retStr = http.httpGet("http://" + UrlConfig.HOST + "/picture/university/" + campus.getId());
            JSONArray retArr = new JSONArray(retStr);

            byte[] imgData = null;
            if(retArr.length() != 0) {
                JSONObject retJson = retArr.getJSONObject(0);
                String imgPath = retJson.getString("path");
                imgPath = "http://" + UrlConfig.HOST + "/picture/" + imgPath.replace(".", "/");
                imgData  = http.httpGetData(imgPath);
                Log.d("CampusImg", imgPath);
            }

            String date = new java.text.SimpleDateFormat("yyyyMMddHHmmss")
                    .format(Calendar.getInstance().getTime());
            retStr = http.httpGet(
                    "http://" + UrlConfig.HOST + "/activity/university/" + campus.getId() + "/date/" + date);
            retArr = new JSONArray(retStr);
            events.clear();
            for(int i = 0; i < retArr.length(); i++) {
                JSONObject json = retArr.getJSONObject(i);
                Event event = new Event();
                event.setId(json.getInt("id"));
                event.setName(json.getString("name"));
                event.setContent(json.getString("description"));
                event.setDate(json.getString("date"));
                int uid = json.getInt("userId");
                event.setUid(uid);
                String un = http.httpGet("http://" + UrlConfig.HOST + "/user/" + uid + "/userName/");
                event.setUn(un);
                events.add(event);
                Log.d("Event", "id: " + event.getId() + " uid: " + event.getUid() +
                      " un: " + event.getUn() + " date: " + event.getDate());
            }

            Bundle b = new Bundle();
            b.putByteArray("img", imgData);
            b.putInt("type", LOAD_DATA_SUCCESS);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Bundle b = new Bundle();
            b.putInt("type", LOAD_DATA_FAIL);
            b.putString("errmsg", ex.getMessage());
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
    }

    private void setBuildings(){
        for(int row = 0; row < buildings.size(); row++){
            final Building building = buildings.get(row);
            Log.v("building", building.getName());

            TextView buildingText
                    = (TextView) getLayoutInflater().inflate(R.layout.text, null);
            buildingText.setText(building.getName());
            buildingText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { buildingTextOnClick(building); }
            });
            buildingPage.addView(buildingText);
        }
    }

    private void buildingTextOnClick(Building b)
    {
        Intent intent = new Intent(CampusActivity.this, BuildingActivity.class);
        intent.putExtra("building", b);
        intent.putExtra("user", user);
        startActivityForResult(intent, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if((requestCode == ACTIVITY_BUILDING || requestCode == ACTIVITY_EVENT ||
            requestCode == ACTIVITY_LOGIN) &&
            resultCode == Activity.RESULT_OK){
            user = (User) data.getSerializableExtra("user");
            setResult(Activity.RESULT_OK, data);
        }
        else if(requestCode == ACTIVITY_ADD_EVENT && resultCode == RESULT_OK)
        {
            final Event e = (Event) data.getSerializableExtra("event");
            TextView tv
                    = (TextView) getLayoutInflater().inflate(R.layout.text, null);
            tv.setText(e.getName());
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { eventTextOnClick(e); }
            });
            eventPage.addView(tv);
        }
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
