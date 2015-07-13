package com.wizard.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wizard.myapplication.R;
import com.wizard.myapplication.entity.Building;

import java.io.Serializable;

public class BuildingActivity extends Activity {

    private TextView titleText;
    private TextView contentText;
    private ImageView image;
    private Building building;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_building);

        Button returnButton = (Button) findViewById(R.id.titlebar_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish(); }
        });
        TextView titlebarText = (TextView) findViewById(R.id.titlebar_name);
        titlebarText.setText("景点");

        titleText = (TextView) findViewById(R.id.titleText);
        contentText = (TextView) findViewById(R.id.contentText);
        image = (ImageView) findViewById(R.id.image);

        Intent intent = getIntent();
        building = (Building) intent.getSerializableExtra("building");
        titleText.setText(building.getName());
        contentText.setText(building.getContent());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.building, menu);
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
