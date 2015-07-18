package com.wizard.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.wizard.myapplication.entity.Campus;
import com.wizard.myapplication.entity.Event;
import com.wizard.myapplication.entity.User;
import com.wizard.myapplication.util.UrlConfig;
import com.wizard.myapplication.util.WizardHTTP;

import org.json.JSONObject;

import java.util.Calendar;

public class AddEventActivity extends Activity {

    private TextView eventTitle;
    private TextView eventContent;
    private Button addButton;
    private Handler handler;

    private TextView dateText = null;
    private Button dateButton = null;
    private TextView timeText = null;
    private Button timeButton = null;
    private AlertDialog dateDialog;
    private AlertDialog timeDialog;
    private DatePicker datePicker;
    private TimePicker timePicker;

    private Calendar c = Calendar.getInstance();

    private User user;
    private Campus campus;
    private String name;
    private String content;

    private static final int ADD_EVENT_SUCCESS = 0;
    private static final int ADD_EVENT_FAIL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_event);

        Intent i = getIntent();
        user = (User) i.getSerializableExtra("user");
        campus = (Campus) i.getSerializableExtra("campus");

        TextView tv = (TextView) findViewById(R.id.titlebar_name);
        tv.setText("添加活动");
        Button returnButton = (Button) findViewById(R.id.titlebar_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });

        eventTitle = (TextView) findViewById(R.id.eventTitle);
        eventContent = (TextView) findViewById(R.id.eventContent);
        addButton = (Button) findViewById(R.id.addButton);
        dateText = (TextView) findViewById(R.id.showDate);
        dateButton = (Button) findViewById(R.id.pickDate);
        timeText = (TextView)findViewById(R.id.showTime);
        timeButton = (Button)findViewById(R.id.pickTime);

        timePicker = new TimePicker(this);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(c.get(Calendar.MINUTE));

        datePicker = new DatePicker(this);
        datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DATE), null);

        dateDialog = new AlertDialog.Builder(this).setTitle("请输入截止日期")
                .setView(datePicker)
                .setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    { dateDialogOkButtonOnClick(); }
                })
                .setNegativeButton("取消", null)
                .create();

        timeDialog = new AlertDialog.Builder(this).setTitle("请输入截止时间")
                .setView(timePicker)
                .setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    { timeDialogOkButtonOnClick(); }
                })
                .setNegativeButton("取消", null)
                .create();

        dateText.setText(String.format("%d-%d-%d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DATE)));
        timeText.setText(String.format("%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)));

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { addButtonOnClick(); }
        });
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { dateButtonOnClick(); }
        });
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { timeButtonOnClick(); }
        });

        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                AddEventActivity.this.handleMessage(msg);
            }
        };
    }

    private void addButtonOnClick()
    {
        name = eventTitle.getText().toString();
        content = eventContent.getText().toString();

        if(!name.equals("") && !content.equals("")){
            new Thread(new Runnable() {
                @Override
                public void run() { threadAddEvent(); }
            }).start();
        }
        else if(name.equals(""))
            Toast.makeText(AddEventActivity.this, "主题为空", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(AddEventActivity.this, "内容为空", Toast.LENGTH_SHORT).show();
    }

    private void dateDialogOkButtonOnClick()
    {
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int date = datePicker.getDayOfMonth();
        dateText.setText(String.format("%d-%d-%d", year, month + 1, date));
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DATE, date);
    }

    private void timeDialogOkButtonOnClick()
    {
        int hour = timePicker.getCurrentHour();
        int min = timePicker.getCurrentMinute();
        timeText.setText(String.format("%02d:%02d", hour, min));
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, min);
    }

    private void dateButtonOnClick()
    {
        datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), null);
        dateDialog.show();
    }

    private void timeButtonOnClick()
    {
        timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
        timeDialog.show();
    }

    private void handleMessage(Message msg){
        Bundle b = msg.getData();
        int type = b.getInt("type");
        switch(type){
            case ADD_EVENT_SUCCESS:
                Intent intent = new Intent();
                intent.putExtra("event", b.getSerializable("event"));
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case ADD_EVENT_FAIL:
                Toast.makeText(this, "添加活动失败", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private void threadAddEvent(){
        try {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);
            http.setHeader("Content-Type", "application/json");
            JSONObject json = new JSONObject();
            json.put("description", content);
            json.put("name", name);
            json.put("userId", user.getId());
            String date = String.format("%d%02d%02d%02d%02d00",
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
                    c.get(Calendar.DATE), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
            json.put("date", date);
            json.put("universityId", campus.getId());
            String postStr = json.toString();
            String retStr = http.httpPost("http://" + UrlConfig.HOST + "/activity/add/", postStr);
            JSONObject retJson = new JSONObject(retStr);

            Event e = new Event();
            e.setDate(date);
            e.setName(name);
            e.setContent(content);
            e.setId(retJson.getInt("id"));
            e.setUid(user.getId());
            e.setUn(user.getUn());
            Log.d("AddEvent", "id: " + e.getId() + " uid: " + e.getUid() +
                  " un: " + e.getUn() + " date: " + e.getDate());

            Bundle b = new Bundle();
            b.putInt("type", ADD_EVENT_SUCCESS);
            b.putSerializable("event", e);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
        catch(Exception exp){
            Bundle b = new Bundle();
            b.putInt("type", ADD_EVENT_FAIL);
            b.putSerializable("errmsg", exp.getMessage());
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
