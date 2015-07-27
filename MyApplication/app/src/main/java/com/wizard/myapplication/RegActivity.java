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

import com.wizard.myapplication.entity.User;
import com.wizard.myapplication.util.UrlConfig;
import com.wizard.myapplication.util.WizardHTTP;

import org.json.JSONObject;


public class RegActivity extends Activity {
    private EditText usernameText, passwordText, password2Text;
    private Button regButton;
    private Handler handler;

    private String un;
    private String pw;

    private static final int REG_SUCCESS = 0;
    private static final int REG_FAIL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_reg);

        Button returnButton = (Button) findViewById(R.id.titlebar_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish(); }
        });
        TextView titlebarText = (TextView) findViewById(R.id.titlebar_name);
        titlebarText.setText("注册");

        usernameText = (EditText) findViewById(R.id.unText);
        passwordText = (EditText) findViewById(R.id.passWord);
        password2Text = (EditText) findViewById(R.id.passWord2);
        regButton = (Button) findViewById(R.id.signup);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regButtonOnClick();
            }
        });

        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                RegActivity.this.handleMessage(msg);
            }
        };
    }

    private void handleMessage(Message msg) {
        Bundle b = msg.getData();
        int type = b.getInt("type");
        switch(type)
        {
            case REG_SUCCESS:
                Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show();
                User u = (User) b.getSerializable("user");
                Intent i = new Intent();
                i.putExtra("user", u);
                setResult(Activity.RESULT_OK, i);
                finish();
                break;
            case REG_FAIL:
                Toast.makeText(this, "注册失败！" + b.getString("errmsg"), Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private void regButtonOnClick(){

        un = usernameText.getText().toString();
        pw = passwordText.getText().toString();
        String pw2 = password2Text.getText().toString();

        if(un.equals("")) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(pw.equals("")) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!pw.equals(pw2)) {
            Toast.makeText(this, "两次密码输入不同", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() { threadReg(); }
        }).start();
    }

    private void threadReg()
    {
        try {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);
            http.setHeader("Content-Type", "application/json");

            //注册
            JSONObject json = new JSONObject();
            json.put("username", un);
            json.put("password", pw);
            String postStr = json.toString();
            String retStr = http.httpPost("http://" + UrlConfig.HOST + "/user/register/", postStr);
            if(retStr.equals(""))
            {
                Bundle b = new Bundle();
                b.putInt("type", REG_FAIL);
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
            Log.d("UserReg", "id: " + user.getId() + " un: " + user.getUn() + " pw: " + user.getPw());

            //获取头像
            byte[] imgData
                    = http.httpGetData("http://" + UrlConfig.HOST + "/avatar/user/" + user.getId());
            user.setAvatar(imgData);

            Bundle b = new Bundle();
            b.putInt("type", REG_SUCCESS);
            b.putSerializable("user", user);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
        catch(Exception ex)
        {
            Bundle b = new Bundle();
            b.putInt("type", REG_FAIL);
            b.putSerializable("errmsg", ex.getMessage());
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.reg, menu);
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
