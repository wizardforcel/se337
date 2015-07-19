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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import com.wizard.myapplication.entity.Building;
import com.wizard.myapplication.entity.User;
import com.wizard.myapplication.util.UrlConfig;
import com.wizard.myapplication.util.WizardHTTP;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends Activity {

    private static final int ACTIVITY_REG = 0;

    private EditText usernameText, passwordText;
    private Button loginButton, regButton;
    private Handler handler;

    private String un;
    private String pw;

    private static final int LOGIN_SUCCESS = 0;
    private static final int LOGIN_FAIL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        Button returnButton = (Button) findViewById(R.id.titlebar_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish(); }
        });
        TextView titlebarText = (TextView) findViewById(R.id.titlebar_name);
        titlebarText.setText("登录");

        usernameText = (EditText) findViewById(R.id.unText);
        passwordText = (EditText) findViewById(R.id.passWord);
        loginButton = (Button) findViewById(R.id.login);
        regButton = (Button) findViewById(R.id.signup);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButtonOnClick();
            }
        });
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { regButtonOnClick(); }
        });

        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                LoginActivity.this.handleMessage(msg);
            }
        };
    }

    private void regButtonOnClick()
    {
        Intent i = new Intent(this, RegActivity.class);
        startActivityForResult(i, ACTIVITY_REG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        if(requestCode == ACTIVITY_REG && resultCode == Activity.RESULT_OK)
        {
            setResult(Activity.RESULT_OK, i);
            finish();
        }
    }

    private void handleMessage(Message msg)
    {
        Bundle b = msg.getData();
        int type = b.getInt("type");
        switch(type)
        {
            case LOGIN_SUCCESS:
                Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show();
                User u = (User) b.getSerializable("user");
                Intent i = new Intent();
                i.putExtra("user", u);
                setResult(Activity.RESULT_OK, i);
                finish();
                break;
            case LOGIN_FAIL:
                Toast.makeText(this, "登录失败！" + b.getString("errmsg"), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void loginButtonOnClick(){
        un = usernameText.getText().toString();
        pw = passwordText.getText().toString();

        if(un.equals("")) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(pw.equals("")) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() { threadLogin(); }
        }).start();
        //new Thread(this::threadLogin).start();
    }

    private void threadLogin()
    {
        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);
            http.setHeader("Content-Type", "application/json");
            http.setCharset("utf-8");

            //登录
            JSONObject json = new JSONObject();
            json.put("username", un);
            json.put("password", pw);
            String postStr = json.toString();
            String retStr = http.httpPost("http://" + UrlConfig.HOST + "/user/login/", postStr);
            if(retStr.equals(""))
            {
                Bundle b = new Bundle();
                b.putInt("type", LOGIN_FAIL);
                b.putSerializable("errmsg", "用户名或密码错误");
                Message msg = handler.obtainMessage();
                msg.setData(b);
                handler.sendMessage(msg);
                return;
            }
            JSONObject retJson = new JSONObject(retStr);

            User user = new User();
            user.setId(retJson.getInt("id"));
            user.setUn(retJson.getString("username"));
            user.setPw(retJson.getString("password"));
            Log.d("UserLogin", "id: " + user.getId() + " un: " + user.getUn() + " pw: " + user.getPw());

            Bundle b = new Bundle();
            b.putInt("type", LOGIN_SUCCESS);
            b.putSerializable("user", user);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
        catch(Exception ex)
        {
            Bundle b = new Bundle();
            b.putInt("type", LOGIN_FAIL);
            b.putSerializable("errmsg", ex.getMessage());
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.login, menu);
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
