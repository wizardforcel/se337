package com.wizard.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wizard.myapplication.entity.BuildingType;
import com.wizard.myapplication.entity.Result;
import com.wizard.myapplication.entity.User;
import com.wizard.myapplication.util.Api;
import com.wizard.myapplication.util.WizardHTTP;

import java.util.ArrayList;
import java.util.List;

public class PreferenceActivity extends Activity {

    private static final int ADD_PRE_SUCCESS = 0;
    private static final int ADD_PRE_FAIL = 1;
    private static final int DEL_PRE_SUCCESS = 2;
    private static final int DEL_PRE_FAIL = 3;

    private Button addButton;
    private ListView preListView;
    private Spinner toAddSpinner;
    private Handler handler;

    private User user;
    private List<String> pres;
    private List<String> toAdds = new ArrayList<String>();
    private String toAdd = "";
    private int removeIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_preference);

        Intent i = getIntent();
        user = (User) i.getSerializableExtra("user");
        pres = user.getPres();

        addButton = (Button) findViewById(R.id.addButton);
        preListView = (ListView) findViewById(R.id.preListView);
        toAddSpinner = (Spinner) findViewById(R.id.toAddSpinner);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { addButtonOnClick(); }
        });

        TextView titlebar_name = (TextView) findViewById(R.id.titlebar_name);
        Button retButton = (Button) findViewById(R.id.titlebar_return);
        titlebar_name.setText("偏好");
        retButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toAddSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toAddSpinnerOnItemSelected(adapterView, view, i, l);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        preListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                preListViewOnItemLongClick(adapterView, view, i, l);
                return true;
            }
        });

        for(String s : BuildingType.TYPES)
        {
            if(!pres.contains(s))
                toAdds.add(s);
        }
        refreshWidget();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                PreferenceActivity.this.handleMessage(msg);
            }
        };
    }

    private void preListViewOnItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        removeIndex = i;
        new AlertDialog.Builder(this)
                .setTitle("确实要删除吗？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteDialogOkButtonOnClick(dialogInterface, i);
                    }
                })
                .setNegativeButton("否", null)
                .show();
    }

    private void deleteDialogOkButtonOnClick(DialogInterface dialogInterface, int i)
    {
        new Thread(new Runnable() {
            @Override
            public void run() { threadDelPreference(); }
        }).start();
    }

    private void threadDelPreference()
    {
        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);

            Result r = Api.delPres(http, user.getId(), pres.get(removeIndex));
            if(r.getErrno() != 0)
            {
                Bundle b = new Bundle();
                b.putInt("type", DEL_PRE_FAIL);
                b.putSerializable("errmsg", r.getErrmsg());
                Message msg = handler.obtainMessage();
                msg.setData(b);
                handler.sendMessage(msg);
            }

            Bundle b = new Bundle();
            b.putInt("type", DEL_PRE_SUCCESS);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Bundle b = new Bundle();
            b.putInt("type", DEL_PRE_FAIL);
            b.putSerializable("errmsg", ex.getMessage());
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
    }

    private void addButtonOnClick()
    {
        if(toAdd.equals(""))
        {
            Toast.makeText(this, "未选中任何偏好！", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() { threadAddPreference(); }
        }).start();
    }

    private void toAddSpinnerOnItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
    {
        //Toast.makeText(this, arg2 + "", Toast.LENGTH_SHORT).show();
        toAdd = toAdds.get(arg2);
        //arg0.setVisibility(View.VISIBLE);
    }

    private void handleMessage(Message msg){
        Bundle b = msg.getData();
        int type = b.getInt("type");
        switch(type){
            case ADD_PRE_SUCCESS:
                Toast.makeText(this, "添加成功！", Toast.LENGTH_SHORT).show();
                pres.add(toAdd);
                toAdds.remove(toAdd);
                toAdd = "";
                Intent i = new Intent();
                i.putExtra("pres", (java.io.Serializable) pres);
                setResult(RESULT_OK, i);
                refreshWidget();
                break;
            case ADD_PRE_FAIL:
                Toast.makeText(this, "添加失败" + b.getString("errmsg"), Toast.LENGTH_SHORT).show();
                break;
            case DEL_PRE_FAIL:
                Toast.makeText(this, "删除失败" + b.getString("errmsg"), Toast.LENGTH_SHORT).show();
                break;
            case DEL_PRE_SUCCESS:
                Toast.makeText(this, "删除成功！", Toast.LENGTH_SHORT).show();
                String toRm = pres.get(removeIndex);
                pres.remove(removeIndex);
                toAdds.add(toRm);
                Intent i2 = new Intent();
                i2.putExtra("pres", (java.io.Serializable) pres);
                setResult(RESULT_OK, i2);
                refreshWidget();
                break;
        }
    }

    private void refreshWidget()
    {
        List<String> presZh = new ArrayList<String>();
        for(String s : pres)
            presZh.add(BuildingType.enToZhMap.get(s));
        List<String> toAddsZh = new ArrayList<String>();
        for(String s :toAdds)
            toAddsZh.add(BuildingType.enToZhMap.get(s));

        preListView.setAdapter(
                new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, presZh));
        toAddSpinner.setAdapter(
                new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, toAddsZh));
    }

    private void threadAddPreference(){
        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);

            Result r = Api.addPres(http, user.getId(), toAdd);
            if(r.getErrno() != 0)
            {
                Bundle b = new Bundle();
                b.putInt("type", ADD_PRE_FAIL);
                b.putSerializable("errmsg", r.getErrmsg());
                Message msg = handler.obtainMessage();
                msg.setData(b);
                handler.sendMessage(msg);
            }

            Bundle b = new Bundle();
            b.putInt("type", ADD_PRE_SUCCESS);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Bundle b = new Bundle();
            b.putInt("type", ADD_PRE_FAIL);
            b.putSerializable("errmsg", ex.getMessage());
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
    }

    /*private void threadGetPreference(){
        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);
            http.setHeader("Content-Type", "application/json");
            String retStr
                    = http.httpGet("http://" + UrlConfig.HOST + "/user/" + user.getId() +"/preference/");
            JSONArray retArr = new JSONArray(retStr);
            for(int i = 0; i < retArr.length(); i++)
            {
                JSONObject json = retArr.getJSONObject(i);
                String type = json.getJSONObject("preference").getString("type");
                if(Arrays.asList(BuildingType.TYPES).contains(type))
                    pres.add(type);
            }

            Bundle b = new Bundle();
            b.putInt("type", GET_PRE_SUCCESS);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Bundle b = new Bundle();
            b.putInt("type", GET_PRE_FAIL);
            b.putSerializable("errmsg", ex.getMessage());
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
    }*/


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
