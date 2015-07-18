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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wizard.myapplication.entity.User;
import com.wizard.myapplication.util.UrlConfig;
import com.wizard.myapplication.util.WizardHTTP;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class PreferenceActivity extends Activity {

    private static final int SET_PRE_SUCCESS = 0;
    private static final int SET_PRE_FAIL = 1;
    private static final int GET_PRE_SUCCESS = 2;
    private static final int GET_PRE_FAIL = 3;

    private Button addButton;
    private ListView preListView;
    private Spinner toAddSpinner;
    private Handler handler;

    private User user;
    private List<String> pres = new ArrayList<String>();
    private List<String> toAdds = new ArrayList<String>();
    private String toAdd = "";

    private static final Map<String, String> enToZhMap =
            new HashMap<String, String>();

    static
    {
        enToZhMap.put("SPORT", "运动");
        enToZhMap.put("FOOD", "美食");
        enToZhMap.put("SCENE", "风景");
        enToZhMap.put("HISTORY", "历史");
        enToZhMap.put("ACADEMIC", "学术");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_preference);

        Intent i = getIntent();
        user = (User) i.getSerializableExtra("user");

        addButton = (Button) findViewById(R.id.addButton);
        preListView = (ListView) findViewById(R.id.preListView);
        toAddSpinner = (Spinner) findViewById(R.id.toAddSpinner);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { addButtonOnClick(); }
        });

        TextView titlebar_name = (TextView) findViewById(R.id.titlebar_name);
        Button retButton = (Button) findViewById(R.id.titlebar_return);
        titlebar_name.setText("偏好");
        retButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toAddSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toAddSpinnerOnItemSelected(adapterView, view, i, l);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                PreferenceActivity.this.handleMessage(msg);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() { threadGetPreference(); }
        }).start();
    }

    private void addButtonOnClick()
    {
        if(toAdd.equals(""))
        {
            Toast.makeText(this, "未选中任何偏好！", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() { threadSetPreference(); }
        }).start();
    }

    private void toAddSpinnerOnItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
    {
        Toast.makeText(this, arg2 + "", Toast.LENGTH_SHORT).show();
        toAdd = toAdds.get(arg2);
        arg0.setVisibility(View.VISIBLE);
    }

    private void handleMessage(Message msg){
        Bundle b = msg.getData();
        int type = b.getInt("type");
        switch(type){
            case SET_PRE_SUCCESS:
                Toast.makeText(this, "设置成功！", Toast.LENGTH_SHORT).show();
                pres.add(toAdd);
                toAdds.remove(toAdd);
                toAdd = "";
                refreshWidget();
                break;
            case SET_PRE_FAIL:
                Toast.makeText(this, "设置失败" + b.getString("errmsg"), Toast.LENGTH_SHORT).show();
                break;
            case GET_PRE_FAIL:
                Toast.makeText(this, "获取失败" + b.getString("errmsg"), Toast.LENGTH_SHORT).show();
                break;
            case GET_PRE_SUCCESS:
                for(String s : enToZhMap.keySet())
                {
                    if(!pres.contains(s))
                        toAdds.add(s);
                }
                refreshWidget();
                break;
        }
    }

    private void refreshWidget()
    {
        List<String> presZh = new ArrayList<String>();
        for(String s : pres)
            presZh.add(enToZhMap.get(s));
        List<String> toAddsZh = new ArrayList<String>();
        for(String s :toAdds)
            toAddsZh.add(s);

        preListView.setAdapter(
                new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, presZh));
        toAddSpinner.setAdapter(
                new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, toAddsZh));
    }

    private void threadSetPreference(){
        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);
            http.setHeader("Content-Type", "application/json");

            String retStr
                   = http.httpGet("http://" + UrlConfig.HOST + "/user/" + user.getId() + "/addpreference/" + toAdd);

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
                String type = json.getJSONObject("preference").getString("type");
                if(enToZhMap.containsKey(type))
                    pres.add(type);
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
