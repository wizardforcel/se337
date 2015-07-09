package com.wizard.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class RegActivity extends Activity {
    private EditText userName, passWord, passWord2;
    private Button signup;


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

        userName = (EditText) findViewById(R.id.userName);
        passWord = (EditText) findViewById(R.id.passWord);
        passWord2 = (EditText) findViewById(R.id.passWord2);
        signup = (Button) findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSignup();
            }
        });
    }

    private boolean reg(String un, String pw)
    {
        return true;
    }

    private void checkSignup(){

        String un = userName.getText().toString();
        String pw = passWord.getText().toString();
        String pw2 = passWord2.getText().toString();

        if(un.equals(""))
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
        else if(pw.equals(""))
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
        else if(pw2.equals(""))
            Toast.makeText(this, "请重复输入密码", Toast.LENGTH_SHORT).show();
        else if(!pw.equals(pw2))
            Toast.makeText(this, "两次密码输入不同", Toast.LENGTH_SHORT).show();
        else if(reg(un, pw))
        {
            Intent i = new Intent();
            i.putExtra("un", un);
            setResult(Activity.RESULT_OK, i);
            finish();
        }
        else {
            Toast.makeText(RegActivity.this, "用户名已被占用", Toast.LENGTH_SHORT).show();
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
