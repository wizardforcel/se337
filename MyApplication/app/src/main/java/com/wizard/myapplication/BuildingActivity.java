package com.wizard.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.wizard.myapplication.R;

public class BuildingActivity extends Activity {

    private TextView titleText;
    private TextView contentText;
    private ImageView image;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);

        titleText = (TextView) findViewById(R.id.titleText);
        contentText = (TextView) findViewById(R.id.contentText);
        image = (ImageView) findViewById(R.id.image);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        titleText.setText(intent.getStringExtra("name"));
        contentText.setText(intent.getStringExtra("content"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.building, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
