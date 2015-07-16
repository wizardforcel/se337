package com.wizard.myapplication;

import android.app.Activity;
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

import org.json.JSONObject;

public class PreferenceActivity extends Activity {

    private TextView sportText;
    private TextView foodText;
    private TextView viewText;
    private TextView historyText;
    private TextView academicText;
    private Button addButton;

    private User user;

    private String preference;

    private static final int ADD_PRE_SUCCESS = 0;
    private static final int ADD_PRE_FAIL = 1;
    private Handler handler;

    private TextView titlebar_name;
    private Button retButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_preference);

        Intent i = getIntent();
        user = (User) i.getSerializableExtra("user");

        preference = "UNKNOWN";
        sportText = (TextView) findViewById(R.id.sportText);
        foodText = (TextView) findViewById(R.id.foodText);
        viewText = (TextView) findViewById(R.id.viewText);
        historyText = (TextView) findViewById(R.id.historyText);
        academicText = (TextView) findViewById(R.id.academicText);
        addButton = (Button) findViewById(R.id.addPreferenceButton);
        titlebar_name = (TextView) findViewById(R.id.titlebar_name);
        retButton = (Button) findViewById(R.id.titlebar_return);

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

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sportText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllGray();
                Resources resources = getBaseContext().getResources();
                Drawable borderDrawable = resources.getDrawable(R.drawable.textview_border_red);
                sportText.setBackgroundDrawable(borderDrawable);
                sportText.setTextColor(Color.RED);
                preference = "SPORT";
            }
        });
        foodText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllGray();
                Resources resources = getBaseContext().getResources();
                Drawable borderDrawable = resources.getDrawable(R.drawable.textview_border_red);
                foodText.setBackgroundDrawable(borderDrawable);
                foodText.setTextColor(Color.RED);
                preference = "FOOD";
            }
        });
        viewText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllGray();
                Resources resources = getBaseContext().getResources();
                Drawable borderDrawable = resources.getDrawable(R.drawable.textview_border_red);
                viewText.setBackgroundDrawable(borderDrawable);
                viewText.setTextColor(Color.RED);
                preference = "SCENE";
            }
        });
        historyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllGray();
                Resources resources = getBaseContext().getResources();
                Drawable borderDrawable = resources.getDrawable(R.drawable.textview_border_red);
                historyText.setBackgroundDrawable(borderDrawable);
                historyText.setTextColor(Color.RED);
                preference = "HISTORY";
            }
        });
        academicText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllGray();
                Resources resources = getBaseContext().getResources();
                Drawable borderDrawable = resources.getDrawable(R.drawable.textview_border_red);
                academicText.setBackgroundDrawable(borderDrawable);
                academicText.setTextColor(Color.RED);
                preference = "ACADEMIC";
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        threadSetPreference();
                    }
                }).start();
            }
        });

    }

    private void handleMessage(Message msg){
        Bundle b = msg.getData();
        int type = b.getInt("type");
        switch(type){
            case ADD_PRE_SUCCESS:
                setResult(Activity.RESULT_OK);
                break;
            case ADD_PRE_FAIL:
                Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    private void threadSetPreference(){
        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);
            http.setHeader("Content-Type", "application/json");
            String retStr = http.httpGet("http://" + UrlConfig.HOST + "/user/" + user.getId() +"addpreference/" + preference);
            JSONObject retJson = new JSONObject(retStr);

            User user = new User();
            user.setId(retJson.getInt("id"));
            user.setUn(retJson.getString("username"));
            user.setPw(retJson.getString("password"));
            user.setName(retJson.getString("name"));

            Bundle b = new Bundle();
            b.putInt("type", ADD_PRE_SUCCESS);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
        catch(Exception ex)
        {
            Bundle b = new Bundle();
            b.putInt("type", ADD_PRE_FAIL);
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
