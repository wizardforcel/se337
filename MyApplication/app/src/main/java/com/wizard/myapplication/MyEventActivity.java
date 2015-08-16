package com.wizard.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import com.wizard.myapplication.entity.Campus;
import com.wizard.myapplication.entity.Event;
import com.wizard.myapplication.entity.User;
import com.wizard.myapplication.util.Api;
import com.wizard.myapplication.util.TabUtil;
import com.wizard.myapplication.util.WizardHTTP;

import java.util.List;


public class MyEventActivity extends Activity {

    private static final int ACTIVITY_EVENT = 0;

    private static final int LOAD_EVENT_SUCC = 0;
    private static final int LOAD_EVENT_FAIL = 1;

    private LinearLayout sentEventPage;
    private LinearLayout joinedEventPage;
    private TabHost tHost;
    private android.os.Handler handler;

    private User user;
    private Campus campus;
    private List<Event> sentEvents;
    private List<Event> joinedEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_event);

        Intent i = getIntent();
        user = (User) i.getSerializableExtra("user");
        campus = (Campus) i.getSerializableExtra("campus");

        TextView tv = (TextView) findViewById(R.id.titlebar_name);
        tv.setText("我的活动");
        Button returnButton = (Button) findViewById(R.id.titlebar_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });

        sentEventPage = (LinearLayout) findViewById(R.id.sentEventPage);
        joinedEventPage = (LinearLayout) findViewById(R.id.joinedEventPage);
        tHost = (TabHost) findViewById(R.id.tabHost);

        tHost = (TabHost) findViewById(R.id.tabHost);
        tHost.setup();
        tHost.addTab(tHost.newTabSpec("我发布的").setIndicator("我发布的").setContent(R.id.sentEventPage));
        tHost.addTab(tHost.newTabSpec("我参加的").setIndicator("我参加的").setContent(R.id.joinedEventPage));
        tHost.setCurrentTab(0);
        TabUtil.updateTab(tHost);
        tHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) { TabUtil.updateTab(MyEventActivity.this.tHost); }
        });

        handler = new android.os.Handler()
        {
            @Override
            public void handleMessage(Message msg) { MyEventActivity.this.handleMessage(msg); }
        };

        new Thread(new Runnable() {
            @Override
            public void run() { threadLoadEvent();}
        }).start();
    }

    private void handleMessage(Message msg)
    {
        Bundle b = msg.getData();
        int type = b.getInt("type");
        switch(type)
        {
            case LOAD_EVENT_FAIL:
                Toast.makeText(this, "获取活动失败！" + b.getString("errmsg"), Toast.LENGTH_SHORT).show();
                break;
            case LOAD_EVENT_SUCC:
                for(Event e : sentEvents)
                    addSentEventToView(e);
                for(Event e : joinedEvents)
                    addJoinedEventToView(e);
                break;
        }
    }

    private void addSentEventToView(final Event e)
    {
        LinearLayout linear
                = (LinearLayout) getLayoutInflater().inflate(R.layout.event_linear, null);
        TextView unText = (TextView) linear.findViewById(R.id.unText);
        TextView titleText = (TextView) linear.findViewById(R.id.titleText);
        ImageView avatarImage = (ImageView) linear.findViewById(R.id.avatarImage);
        titleText.setText(e.getName());
        unText.setText(e.getUn());
        avatarImage.setImageBitmap(BitmapFactory.decodeByteArray(e.getAvatar(), 0, e.getAvatar().length));
        linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { eventTextOnClick(e); }
        });
        sentEventPage.addView(linear);
    }

    private void addJoinedEventToView(final Event e)
    {
        LinearLayout linear
                = (LinearLayout) getLayoutInflater().inflate(R.layout.event_linear, null);
        TextView unText = (TextView) linear.findViewById(R.id.unText);
        TextView titleText = (TextView) linear.findViewById(R.id.titleText);
        ImageView avatarImage = (ImageView) linear.findViewById(R.id.avatarImage);
        titleText.setText(e.getName());
        unText.setText(e.getUn());
        avatarImage.setImageBitmap(BitmapFactory.decodeByteArray(e.getAvatar(), 0, e.getAvatar().length));
        linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { eventTextOnClick(e); }
        });
        joinedEventPage.addView(linear);
    }

    private void eventTextOnClick(Event e)
    {
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra("event", e);
        intent.putExtra("user", user);
        intent.putExtra("campusId", campus.getId());
        startActivityForResult(intent, ACTIVITY_EVENT);
    }

    private void threadLoadEvent()
    {
        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader();

            sentEvents = Api.getSentActiivity(http, campus.getId(), user.getId());
            joinedEvents = Api.getJoinedActiivity(http, campus.getId(), user.getId());

            Bundle b = new Bundle();
            b.putInt("type", LOAD_EVENT_SUCC);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Bundle b = new Bundle();
            b.putInt("type", LOAD_EVENT_FAIL);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        Log.v("Result", requestCode + " " + resultCode);
        if(requestCode == ACTIVITY_EVENT &&
           resultCode == Activity.RESULT_OK)
        {
            user = (User) i.getSerializableExtra("user");
            setResult(RESULT_OK, i);
        }
    }
}
