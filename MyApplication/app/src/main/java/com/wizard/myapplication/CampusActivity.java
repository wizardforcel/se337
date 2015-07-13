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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.wizard.myapplication.entity.Building;
import com.wizard.myapplication.entity.Campus;

import java.util.List;

public class CampusActivity extends Activity {

    private ImageView collegeImage;
    private TextView collegeInfo;
    private TableLayout buildingTable;

    private Campus campus;
    private List<Building> buildings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_campus);

        collegeInfo = (TextView) findViewById(R.id.collegeInfo);
        buildingTable = (TableLayout) findViewById(R.id.buildingTable);


        Intent i = getIntent();
        campus = (Campus) i.getSerializableExtra("campus");
        buildings = campus.getBuildings();
        collegeInfo.setText(campus.getContent());
        setBuildingTable();

        TextView tv = (TextView) findViewById(R.id.titlebar_name);
        tv.setText(campus.getName());
        Button returnButton = (Button) findViewById(R.id.titlebar_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });
    }

    private void setBuildingTable(){
        for(int row = 0; row < buildings.size(); row++){
            final Building building = buildings.get(row);
            TextView tv = new TextView(this);
            TableRow tableRow = new TableRow(this);
            tv.setText(building.getName());
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CampusActivity.this, BuildingActivity.class);
                    intent.putExtra("building", building);
                    startActivityForResult(intent, 0);
                }
            });
            tableRow.addView(tv);
            buildingTable.addView(tableRow);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            setResult(Activity.RESULT_OK, data);
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
