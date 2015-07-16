package com.wizard.myapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.wizard.myapplication.R;
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
    private TextView titlebar_name;

    private User user;
    private Campus campus;
    private String name;
    private String content;

    private Handler handler;

    private static final int ADD_EVENT_SUCCESS = 0;
    private static final int ADD_EVENT_FAIL = 1;

    private TextView showDate = null;
    private Button pickDate = null;
    private TextView showTime = null;
    private Button pickTime = null;

    private static final int SHOW_DATAPICK = 2;
    private static final int DATE_DIALOG_ID = 3;
    private static final int SHOW_TIMEPICK = 4;
    private static final int TIME_DIALOG_ID = 5;

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
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
        showDate = (TextView) findViewById(R.id.showDate);
        pickDate = (Button) findViewById(R.id.pickDate);
        showTime = (TextView)findViewById(R.id.showTime);
        pickTime = (Button)findViewById(R.id.pickTime);

        pickDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Message msg = new Message();
                if (pickDate.equals((Button) v)) {
                    msg.what = AddEventActivity.SHOW_DATAPICK;
                }
                AddEventActivity.this.dateandtimeHandler.sendMessage(msg);
            }
        });

        pickTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Message msg = new Message();
                if (pickTime.equals((Button) v)) {
                    msg.what = AddEventActivity.SHOW_TIMEPICK;
                }
                AddEventActivity.this.dateandtimeHandler.sendMessage(msg);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { addButtonOnClick(); }
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

    /**
     * 设置日期
     */
    private void setDateTime(){
        final Calendar c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        updateDateDisplay();
    }

    /**
     * 更新日期显示
     */
    private void updateDateDisplay(){
        showDate.setText(new StringBuilder().append(mYear).append("-")
                .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)).append("-")
                .append((mDay < 10) ? "0" + mDay : mDay));
    }

    /**
     * 日期控件的事件
     */
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;

            updateDateDisplay();
        }
    };

    /**
     * 设置时间
     */
    private void setTimeOfDay(){
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        updateTimeDisplay();
    }

    /**
     * 更新时间显示
     */
    private void updateTimeDisplay(){
        showTime.setText(new StringBuilder().append(mHour).append(":")
                .append((mMinute < 10) ? "0" + mMinute : mMinute));
    }

    /**
     * 时间控件事件
     */
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;

            updateTimeDisplay();
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
                        mDay);
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, true);
        }

        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
                break;
            case TIME_DIALOG_ID:
                ((TimePickerDialog) dialog).updateTime(mHour, mMinute);
                break;
        }
    }

    /**
     * 处理日期和时间控件的Handler
     */
    Handler dateandtimeHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AddEventActivity.SHOW_DATAPICK:
                    showDialog(DATE_DIALOG_ID);
                    break;
                case AddEventActivity.SHOW_TIMEPICK:
                    showDialog(TIME_DIALOG_ID);
                    break;
            }
        }

    };

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
                Toast.makeText(this, "添加活动失败", Toast.LENGTH_SHORT);
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
            String date = new StringBuilder().append(mYear)
                    .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
                    .append((mDay < 10) ? "0" + mDay : mDay).append(mHour)
                    .append((mMinute < 10) ? "0" + mMinute : mMinute).toString();
            json.put("date", date);
            json.put("universityId", campus.getId());
            String postStr = json.toString();
            String retStr = http.httpPost("http://" + UrlConfig.HOST + "/activity/add/", postStr);
            //JSONObject retJson = new JSONObject(retStr);

            Event e = new Event();
            e.setDate(date);
            e.setName(name);
            e.setContent(content);

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
