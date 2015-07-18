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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.wizard.myapplication.R;
import com.wizard.myapplication.entity.Building;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends Activity {

    private List<Building> buildings;
    private List<Integer> resultId
            = new ArrayList<Integer>();

    private EditText searchInput;
    private Button searchButton;
    private ListView resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);

        Intent i = getIntent();
        buildings = (List<Building>) i.getSerializableExtra("buildings");

        Button returnButton = (Button) findViewById(R.id.titlebar_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish(); }
        });
        TextView titlebarText = (TextView) findViewById(R.id.titlebar_name);
        titlebarText.setText("搜索");

        searchInput = (EditText) findViewById(R.id.searchInput);
        searchButton = (Button) findViewById(R.id.searchButton);
        resultList = (ListView) findViewById(R.id.resultList);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { searchButtonOnClick(); }
        });
        resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                resultlistOnItemClick(adapterView, view, i, l);
            }
        });
    }


    private void searchButtonOnClick()
    {
        String searchText = searchInput.getText().toString();
        if(searchText.equals(""))
        {
            Toast.makeText(this, "请输入检索内容！", Toast.LENGTH_SHORT).show();
            return;
        }

        resultId.clear();
        List<String> resultText = new ArrayList<String>();
        for(Building b : buildings)
        {
            if(b.getName().contains(searchText) ||
               b.getContent().contains(searchText))
            {
                resultId.add(b.getId());
                resultText.add(b.getName());
            }
        }

        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, resultText);
        resultList.setAdapter(adapter);

        if(resultText.size() == 0)
            Toast.makeText(this, "无结果", Toast.LENGTH_SHORT).show();
    }

    private void resultlistOnItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        //Toast.makeText(this, i + "", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("resultId", resultId.get(i));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
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
