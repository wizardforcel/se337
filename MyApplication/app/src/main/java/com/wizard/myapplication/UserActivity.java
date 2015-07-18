package com.wizard.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.wizard.myapplication.view.CircularImage;

public class UserActivity extends Activity {

    private CircularImage userImage;
    private TextView unText;
    private TextView preText;
    private TextView rankText;
    private TextView taskText;
    private TextView exchangeText;

    private User user;
    private Handler handler;

    private static final int LOAD_IMG_SUCCESS = 0;
    private static final int LOAD_IMG_FAIL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        TextView titlebar_name = (TextView) findViewById(R.id.titlebar_name);
        Button retButton = (Button) findViewById(R.id.titlebar_return);
        titlebar_name.setText("我的信息");
        retButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        userImage = (CircularImage) findViewById(R.id.userImage);
        unText = (TextView) findViewById(R.id.unText);
        preText = (TextView) findViewById(R.id.preText);
        rankText = (TextView) findViewById(R.id.rankText);
        taskText = (TextView) findViewById(R.id.taskText);
        exchangeText = (TextView) findViewById(R.id.exchangeText);

        unText.setText(user.getUn());
        preText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preTextOnClick();
            }
        });
        rankText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rankTextOnClick();
            }
        });
        exchangeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exchangeTextOnClick();
            }
        });
        taskText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTextOnClick();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                threadGetUserPhoto();
            }
        }).start();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                UserActivity.this.handleMessage(msg);
            }
        };
    }

    private void handleMessage(Message msg){
        Bundle b = msg.getData();
        int type = b.getInt("type");
        switch (type){
            case LOAD_IMG_SUCCESS:
                byte[] img = b.getByteArray("img");
                if(img != null)
                    userImage.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
                    Toast.makeText(UserActivity.this, "获取头像成功", Toast.LENGTH_SHORT).show();
                break;
            case LOAD_IMG_FAIL:
                Toast.makeText(UserActivity.this, "获取头像失败", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void preTextOnClick(){
        Intent i = new Intent(this, PreferenceActivity.class);
        i.putExtra("user", user);
        startActivity(i);
    }

    private void threadGetUserPhoto(){
        try {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);
            http.setHeader("Content-Type", "application/json");
            byte[] imgData = null;
            imgData = http.httpGetData("http://" + UrlConfig.HOST + "/avatar/user/" + user.getId());

            Bundle b = new Bundle();
            b.putByteArray("img", imgData);
            b.putInt("type", LOAD_IMG_SUCCESS);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }catch (Exception exp){
            Bundle b = new Bundle();
            b.putInt("type", LOAD_IMG_FAIL);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
    }

    private void exchangeTextOnClick()
    {
        Intent i = new Intent(this, ExchangeActivity.class);
        startActivity(i);
    }

    private void taskTextOnClick()
    {
        Intent i = new Intent(this, AccomActivity.class);
        startActivity(i);
    }

    private void rankTextOnClick()
    {
        Intent i = new Intent(this, RankActivity.class);
        startActivity(i);
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
