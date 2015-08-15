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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.wizard.myapplication.entity.Campus;
import com.wizard.myapplication.entity.Event;
import com.wizard.myapplication.entity.User;
import com.wizard.myapplication.util.Api;
import com.wizard.myapplication.util.Common;
import com.wizard.myapplication.util.UrlConfig;
import com.wizard.myapplication.util.WizardHTTP;

import org.json.JSONObject;

import java.util.Calendar;

public class AddEventActivity extends Activity {

    private TextView eventTitle;
    private TextView eventContent;
    private Button addButton;
    private TextView maxEdit;
    private Spinner locSpinner;
    private TextView enrollStartDateText;
    private TextView enrollStartTimeText;
    private TextView enrollEndDateText;
    private TextView enrollEndTimeText;
    private TextView startDateText;
    private TextView startTimeText;
    private TextView endDateText;
    private TextView endTimeText;
    private Handler handler;

    private AlertDialog dateDialog;
    private AlertDialog timeDialog;
    private DatePicker datePicker;
    private TimePicker timePicker;

    private static final int ENROLL_START = 0;
    private static final int ENROLL_END = 1;
    private static final int START = 2;
    private static final int END = 3;

    private int currentType;

    private User user;
    private Campus campus;

    private String name;
    private String content;
    private int maxPeople;
    private int locIndex;
    private Calendar enrollStartCal = Calendar.getInstance();
    private Calendar enrollEndCal = Calendar.getInstance();
    private Calendar startCal = Calendar.getInstance();
    private Calendar endCal = Calendar.getInstance();


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
        maxEdit = (TextView) findViewById(R.id.maxEdit);
        locSpinner = (Spinner) findViewById(R.id.locSpinner);
        enrollStartDateText = (TextView) findViewById(R.id.enrollStartDateText);
        enrollStartTimeText = (TextView) findViewById(R.id.enrollStartTimeText);
        enrollEndDateText = (TextView) findViewById(R.id.enrollEndDateText);
        enrollEndTimeText = (TextView) findViewById(R.id.enrollEndTimeText);
        startDateText = (TextView) findViewById(R.id.startDateText);
        startTimeText = (TextView) findViewById(R.id.startTimeText);
        endDateText = (TextView) findViewById(R.id.endDateText);
        endTimeText = (TextView) findViewById(R.id.endTimeText);

        timePicker = new TimePicker(this);
        timePicker.setIs24HourView(true);
        datePicker = new DatePicker(this);

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

        enrollStartDateText.setText(Common.calToDateStr(startCal));
        enrollStartTimeText.setText(Common.calToTimeStr(startCal));
        enrollEndDateText.setText(Common.calToDateStr(startCal));
        enrollEndTimeText.setText(Common.calToTimeStr(startCal));
        startDateText.setText(Common.calToDateStr(startCal));
        startTimeText.setText(Common.calToTimeStr(startCal));
        endDateText.setText(Common.calToDateStr(startCal));
        endTimeText.setText(Common.calToTimeStr(startCal));

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { addButtonOnClick(); }
        });

        enrollStartDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { enrollStartDateTextOnClick(); }
        });
        enrollStartTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { enrollStartTimeTextOnClick(); }
        });
        enrollEndDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { enrollEndDateTextOnClick(); }
        });
        enrollEndTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { enrollEndTimeTextOnClick(); }
        });
        startDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { startDateTextOnClick(); }
        });
        startTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { startTimeTextOnClick(); }
        });
        endDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { endDateTextOnClick(); }
        });
        endTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { endTimeTextOnClick(); }
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
        if(name.equals("")) {
            Toast.makeText(AddEventActivity.this, "主题为空", Toast.LENGTH_SHORT).show();
            return;
        }
        content = eventContent.getText().toString();
        if(content.equals("")) {
            Toast.makeText(AddEventActivity.this, "内容为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String maxPeopleStr = maxEdit.getText().toString();
        if(!maxPeopleStr.matches("^\\d+$")) {
            Toast.makeText(AddEventActivity.this, "人数应为数字", Toast.LENGTH_SHORT).show();
            return;
        }
        maxPeople = Integer.parseInt(maxEdit.getText().toString());

        //TODO: more verify

        new Thread(new Runnable() {
            @Override
            public void run() { threadAddEvent(); }
        }).start();
    }

    private void enrollStartDateTextOnClick()
    {
        currentType = ENROLL_START;
        Common.calToDatePicker(enrollStartCal, datePicker);
        dateDialog.show();
    }

    private void enrollStartTimeTextOnClick()
    {
        currentType = ENROLL_START;
        Common.calToTimePicker(enrollStartCal, timePicker);
        timeDialog.show();
    }
    private void enrollEndDateTextOnClick()
    {
        currentType = ENROLL_END;
        Common.calToDatePicker(enrollEndCal, datePicker);
        dateDialog.show();
    }

    private void enrollEndTimeTextOnClick()
    {
        currentType = ENROLL_END;
        Common.calToTimePicker(enrollEndCal, timePicker);
        timeDialog.show();
    }

    private void startDateTextOnClick()
    {
        currentType = START;
        Common.calToDatePicker(startCal, datePicker);
        dateDialog.show();
    }

    private void startTimeTextOnClick()
    {
        currentType = START;
        Common.calToTimePicker(startCal, timePicker);
        timeDialog.show();
    }
    private void endDateTextOnClick()
    {
        currentType = END;
        Common.calToDatePicker(endCal, datePicker);
        dateDialog.show();
    }

    private void endTimeTextOnClick()
    {
        currentType = END;
        Common.calToTimePicker(endCal, timePicker);
        timeDialog.show();
    }

    private TextView getCurrentDateTextView()
    {
        if(currentType == ENROLL_START)
            return enrollStartDateText;
        else if(currentType == ENROLL_END)
            return enrollEndDateText;
        else if(currentType == START)
            return startDateText;
        else if(currentType == END)
            return endDateText;
        else return null;
    }

    private TextView getCurrentTimeTextView()
    {
        if(currentType == ENROLL_START)
            return enrollStartTimeText;
        else if(currentType == ENROLL_END)
            return enrollEndTimeText;
        else if(currentType == START)
            return startTimeText;
        else if(currentType == END)
            return endTimeText;
        else return null;
    }

    private Calendar getCurrentCal()
    {
        if(currentType == ENROLL_START)
            return enrollStartCal;
        else if(currentType == ENROLL_END)
            return enrollEndCal;
        else if(currentType == START)
            return startCal;
        else if(currentType == END)
            return endCal;
        else return null;
    }

    private void dateDialogOkButtonOnClick()
    {
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int date = datePicker.getDayOfMonth();
        TextView tv = getCurrentDateTextView();
        tv.setText(String.format("%d-%d-%d", year, month + 1, date));
        Calendar c = getCurrentCal();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DATE, date);
    }

    private void timeDialogOkButtonOnClick()
    {
        int hour = timePicker.getCurrentHour();
        int min = timePicker.getCurrentMinute();
        TextView tv = getCurrentTimeTextView();
        tv.setText(String.format("%02d:%02d", hour, min));
        Calendar c = getCurrentCal();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, min);
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

            Event e = Api.addActivity(http, campus.getId(), user, name, content,
                                      Common.calToDateNum(enrollStartCal),
                                      Common.calToDateNum(enrollEndCal),
                                      Common.calToDateNum(startCal),
                                      Common.calToDateNum(endCal),
                                      maxPeople, campus.getBuildings().get(locIndex));

            Bundle b = new Bundle();
            b.putInt("type", ADD_EVENT_SUCCESS);
            b.putSerializable("event", e);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
        catch(Exception exp){
            exp.printStackTrace();
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
