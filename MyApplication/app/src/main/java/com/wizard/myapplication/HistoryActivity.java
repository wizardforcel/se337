package com.wizard.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wizard.myapplication.entity.Building;
import com.wizard.myapplication.entity.User;
import com.wizard.myapplication.util.Api;
import com.wizard.myapplication.util.UrlConfig;
import com.wizard.myapplication.util.WizardHTTP;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends Activity {

    private List<Building> covered
            = new ArrayList<Building>();
    private User user;
    private int campusId;
    private int allCount;

    private TextView rateText;
    private ListView coveredList;
    private Handler handler;

    private static final int GET_HIS_SUCCESS = 0;
    private static final int GET_HIS_FAIL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_history);

        Button returnButton = (Button) findViewById(R.id.titlebar_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish(); }
        });
        TextView titlebarText = (TextView) findViewById(R.id.titlebar_name);
        titlebarText.setText("游览历史");

        Intent i = getIntent();
        user = (User) i.getSerializableExtra("user");
        campusId = i.getIntExtra("campusId", -1);
        allCount = i.getIntExtra("allCount", Integer.MAX_VALUE);

        rateText = (TextView) findViewById(R.id.rateText);
        coveredList = (ListView) findViewById(R.id.buildingList);
        coveredList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                coveredListOnItemClick(adapterView, view, i, l);
            }
        });

        handler = new Handler()
        {
            @Override
            public void handleMessage(android.os.Message msg)
            {
                HistoryActivity.this.handleMessage(msg);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() { threadGetHistory(); }
        }).start();
    }

    private void handleMessage(android.os.Message msg)
    {
        Bundle b = msg.getData();
        int type = b.getInt("type");
        switch(type)
        {
            case GET_HIS_SUCCESS:
                List<String> coveredText = new ArrayList<String>();
                for(Building building : covered)
                    coveredText.add(building.getName());
                coveredList.setAdapter(
                        new ArrayAdapter<String>(HistoryActivity.this, android.R.layout.simple_expandable_list_item_1, coveredText));
                int rate = covered.size() * 100 / allCount;
                rateText.setText(rate + "%");
                break;
            case GET_HIS_FAIL:
                Toast.makeText(HistoryActivity.this, "历史获取失败！" + b.getString("errmsg"), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void threadGetHistory()
    {
        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);
            http.setHeader("Content-Type", "application/json");
            http.setCharset("utf-8");

            covered = Api.getHistory(http, user.getId(), campusId);

            Bundle b = new Bundle();
            b.putInt("type", GET_HIS_SUCCESS);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Bundle b = new Bundle();
            b.putInt("type", GET_HIS_FAIL);
            b.putSerializable("errmsg", ex.getMessage());
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }

    }

    private void coveredListOnItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        int id = covered.get(i).getId();
        Intent intent = new Intent();
        intent.putExtra("resultId", id);
        setResult(RESULT_OK, intent);
        finish();
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
