package com.wizard.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.navisdk.BNaviPoint;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams;
import com.wizard.myapplication.entity.NaviNode;

import java.util.ArrayList;
import java.util.List;

public class NaviSettingActivity extends Activity {

    private static final double DIFF = 0.0065;

    private Button srcButton;
    private Button destButton;
    private Button okButton;

    private AlertDialog srcDialog;
    private AlertDialog destDialog;

    private int currentSrc = 0;
    private int currentDest = 0;

    private ArrayList<NaviNode> nodes;
    private NaviNode myLoc;

    private List<String> srcList;
    private List<String> destList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_navi_setting);

        Button returnButton = (Button) findViewById(R.id.titlebar_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish(); }
        });
        TextView titlebarText = (TextView) findViewById(R.id.titlebar_name);
        titlebarText.setText("导航设置");

        srcButton = (Button) findViewById(R.id.naviSrcButton);
        destButton = (Button) findViewById(R.id.naviDestButton);
        okButton = (Button) findViewById(R.id.naviOkButton);

        srcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { srcButtonOnClick(); }
        });
        destButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { destButtonOnClick(); }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { okButtonOnClick(); }
        });

        Intent i = getIntent();
        nodes = (ArrayList<NaviNode>) i.getSerializableExtra("nodes");
        myLoc = (NaviNode) i.getSerializableExtra("myLoc");

        srcList = new ArrayList<String>();
        destList = new ArrayList<String>();
        for(NaviNode n : nodes)
        {
            srcList.add(n.getName());
            destList.add(n.getName());
        }
        srcList.add(myLoc.getName());
        srcButton.setText(srcList.get(currentSrc));
        destButton.setText(destList.get(currentDest));

        srcDialog = new AlertDialog.Builder(this).setTitle("请选择出发地点")
                .setSingleChoiceItems(srcList.toArray(new String[0]), currentSrc,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            { srcDialogOkButtonOnClick(dialog, which); }
                        })
                .setNegativeButton("取消", null)
                .create();

        destDialog = new AlertDialog.Builder(this).setTitle("请选择目标地点")
                .setSingleChoiceItems(destList.toArray(new String[0]), currentDest,
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            { destDialogOkButtonOnClick(dialog, which); }
                        })
                .setNegativeButton("取消", null)
                .create();
    }

    private void srcDialogOkButtonOnClick(DialogInterface dialog, int which)
    {
        currentSrc = which;
        srcButton.setText(srcList.get(which));
        dialog.dismiss();
    }

    private void destDialogOkButtonOnClick(DialogInterface dialog, int which)
    {
        currentDest = which;
        destButton.setText(destList.get(which));
        dialog.dismiss();
    }

    private void srcButtonOnClick()
    {
        srcDialog.show();
    }

    private void destButtonOnClick()
    {
        destDialog.show();
    }

    private void okButtonOnClick()
    {
        NaviNode src = currentSrc == nodes.size()? myLoc: nodes.get(currentSrc);
        NaviNode dest = nodes.get(currentDest);

        /*Intent intent = new Intent();
        intent.putExtra("src", src);
        intent.putExtra("dest", dest);
        setResult(Activity.RESULT_OK, intent);
        finish();*/

        BNaviPoint startPoint = new BNaviPoint(src.getLng() - DIFF, src.getLat() - DIFF, src.getName());
        BNaviPoint endPoint = new BNaviPoint(dest.getLng() - DIFF, dest.getLat() - DIFF, dest.getName());

        BaiduNaviManager.getInstance().launchNavigator(this, startPoint, endPoint,
                RoutePlanParams.NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_DIST, 		 //算路方式
                true, 									   		 //真实导航
                BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, //在离线策略
                new BaiduNaviManager.OnStartNavigationListener() {				 //跳转监听
                    @Override
                    public void onJumpToNavigator(Bundle configParams) {
                        baiduNaviOnJumpToNavigator(configParams);
                    }
                    @Override
                    public void onJumpToDownloader() {
                    }
                });
    }

    private void baiduNaviOnJumpToNavigator(Bundle bundle)
    {
        Intent intent = new Intent(NaviSettingActivity.this, NaviActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.navi_setting, menu);
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
