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



public class LoginActivity extends Activity {
    private EditText userName, passWord;
    private Button login, signup;


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

        userName = (EditText) findViewById(R.id.userName);
        passWord = (EditText) findViewById(R.id.passWord);
        login = (Button) findViewById(R.id.login);
        signup = (Button) findViewById(R.id.signup);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButtonOnClick();
            }
        });
    }

    private boolean login(String un, String pw)
    {
        return true;
    }

    private void loginButtonOnClick(){
        String un = userName.getText().toString();
        String pw = passWord.getText().toString();

        if(un.equals(""))
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
        else if(pw.equals(""))
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
        else if(login(un, pw)){
                Intent intent = new Intent();
                intent.putExtra("un", un);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        else{
            Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
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
