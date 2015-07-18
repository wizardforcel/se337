package com.wizard.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.wizard.myapplication.entity.Building;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends Activity {

    private List<Building> covered;
    private int rate;

    private TextView rateText;
    private ListView coveredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_history);

        Button returnButton = (Button) findViewById(R.id.titlebar_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish(); }
        });
        TextView titlebarText = (TextView) findViewById(R.id.titlebar_name);
        titlebarText.setText("游览历史");

        Intent i = getIntent();
        covered = (List<Building>) i.getSerializableExtra("covered");
        rate = i.getIntExtra("rate", 0);

        rateText = (TextView) findViewById(R.id.rateText);
        coveredList = (ListView) findViewById(R.id.buildingList);
        rateText.setText(rate + "%");
        List<String> coveredText = new ArrayList<String>();
        for(Building b : covered)
            coveredText.add(b.getName());
        coveredList.setAdapter(
                new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, coveredText));
        coveredList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                coveredListOnItemClick(adapterView, view, i, l);
            }
        });
    }

    private void coveredListOnItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        int id = covered.get(i).getId();
        Intent intent = new Intent();
        intent.putExtra("resultId", id);
        setResult(RESULT_OK, intent);
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
