package com.wizard.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wizard.myapplication.entity.User;
import com.wizard.myapplication.util.UrlConfig;
import com.wizard.myapplication.util.WizardHTTP;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class PreferenceActivity extends Activity {

    private static final int SET_PRE_SUCCESS = 0;
    private static final int SET_PRE_FAIL = 1;
    private static final int GET_PRE_SUCCESS = 2;
    private static final int GET_PRE_FAIL = 3;

    private TextView sportText;
    private TextView foodText;
    private TextView viewText;
    private TextView historyText;
    private TextView academicText;
    private Button addButton;
    private Handler handler;

    private User user;
    private HashMap<String, View.OnClickListener> preMap
            = new HashMap<String, View.OnClickListener>();
    private HashSet<String> preferences = new HashSet<String>();
    private String toAdd = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_preference);

        Intent i = getIntent();
        user = (User) i.getSerializableExtra("user");

        sportText = (TextView) findViewById(R.id.sportText);
        foodText = (TextView) findViewById(R.id.foodText);
        viewText = (TextView) findViewById(R.id.viewText);
        historyText = (TextView) findViewById(R.id.historyText);
        academicText = (TextView) findViewById(R.id.academicText);
        addButton = (Button) findViewById(R.id.addPreferenceButton);
        TextView titlebar_name = (TextView) findViewById(R.id.titlebar_name);
        Button retButton = (Button) findViewById(R.id.titlebar_return);

        titlebar_name.setText("偏好");
        retButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                PreferenceActivity.this.handleMessage(msg);
            }
        };

        setAllGray();

        sportText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { sportTextOnClick(); }
        });
        foodText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { foodTextOnClick(); }
        });
        viewText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { viewTextOnClick(); }
        });
        historyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { historyTextOnClick(); }
        });
        academicText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { academicTextOnClick(); }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { addButtonOnClick(); }
        });

        preMap.put("SPORT", new View.OnClickListener() {
            @Override
            public void onClick(View v) { sportTextOnClick(); }
        });
        preMap.put("FOOD", new View.OnClickListener() {
            @Override
            public void onClick(View v) { foodTextOnClick(); }
        });
        preMap.put("SCENE", new View.OnClickListener() {
            @Override
            public void onClick(View v) { viewTextOnClick(); }
        });
        preMap.put("HiSTORY", new View.OnClickListener() {
            @Override
            public void onClick(View v) { historyTextOnClick(); }
        });
        preMap.put("ACADEMIC", new View.OnClickListener() {
            @Override
            public void onClick(View v) { academicTextOnClick(); }
        });

        new Thread(new Runnable() {
            @Override
            public void run() { threadGetPreference(); }
        }).start();
    }

    private void addButtonOnClick()
    {
        new Thread(new Runnable() {
            @Override
            public void run() { threadSetPreference(); }
        }).start();
    }

    private void academicTextOnClick()
    {
        setRed(academicText);
        preferences.add("ACADEMIC");
    }

    private void sportTextOnClick()
    {
        setRed(sportText);
        preferences.add("SPORT");
    }

    private void foodTextOnClick()
    {
        setRed(foodText);
        preferences.add("FOOD") ;
    }

    private void viewTextOnClick()
    {
        setRed(viewText);
        preferences.add("SCENE");
    }

    private void setRed(TextView view)
    {
        Resources resources = getBaseContext().getResources();
        Drawable borderDrawable = resources.getDrawable(R.drawable.textview_border_red);
        view.setBackgroundDrawable(borderDrawable);
        view.setTextColor(Color.RED);
    }

    private void historyTextOnClick()
    {
        setRed(historyText);
        preferences.add("HISTORY");
    }

    private void handleMessage(Message msg){
        Bundle b = msg.getData();
        int type = b.getInt("type");
        switch(type){
            case SET_PRE_SUCCESS:
                Toast.makeText(this, "设置成功！", Toast.LENGTH_SHORT).show();
                break;
            case SET_PRE_FAIL:
                Toast.makeText(this, "设置失败" + b.getString("errmsg"), Toast.LENGTH_SHORT).show();
                break;
            case GET_PRE_FAIL:
                Toast.makeText(this, "获取失败" + b.getString("errmsg"), Toast.LENGTH_SHORT).show();
                break;
            case GET_PRE_SUCCESS:
                for(String preference : preferences) {
                    if (preMap.containsKey(preference))
                        preMap.get(preference).onClick(null);
                }
                break;
        }
    }
    private void threadSetPreference(){
        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);
            http.setHeader("Content-Type", "application/json");

            for(String preference : preferences) {
                String retStr
                       = http.httpGet("http://" + UrlConfig.HOST + "/user/" + user.getId() + "/addpreference/" + preference);
            }

            Bundle b = new Bundle();
            b.putInt("type", SET_PRE_SUCCESS);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Bundle b = new Bundle();
            b.putInt("type", SET_PRE_FAIL);
            b.putSerializable("errmsg", ex.getMessage());
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
    }

    private void threadGetPreference(){
        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);
            http.setHeader("Content-Type", "application/json");
            String retStr
                    = http.httpGet("http://" + UrlConfig.HOST + "/user/" + user.getId() +"/preference/");
            JSONArray retArr = new JSONArray(retStr);
            for(int i = 0; i < retArr.length(); i++)
            {
                JSONObject json = retArr.getJSONObject(i);
                preferences.add(json.getJSONObject("preference").getString("type"));
            }

            Bundle b = new Bundle();
            b.putInt("type", GET_PRE_SUCCESS);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Bundle b = new Bundle();
            b.putInt("type", GET_PRE_FAIL);
            b.putSerializable("errmsg", ex.getMessage());
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
    }

    private void setAllGray(){
        Resources resources = getBaseContext().getResources();
        Drawable borderDrawable = resources.getDrawable(R.drawable.textview_border_gray);
        sportText.setBackgroundDrawable(borderDrawable);
        sportText.setTextColor(Color.GRAY);
        foodText.setBackgroundDrawable(borderDrawable);
        foodText.setTextColor(Color.GRAY);
        viewText.setBackgroundDrawable(borderDrawable);
        viewText.setTextColor(Color.GRAY);
        historyText.setBackgroundDrawable(borderDrawable);
        historyText.setTextColor(Color.GRAY);
        academicText.setBackgroundDrawable(borderDrawable);
        academicText.setTextColor(Color.GRAY);
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
