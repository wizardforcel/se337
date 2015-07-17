package com.wizard.myapplication.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;

import com.wizard.myapplication.R;

/**
 * Created by yuhan on 15-7-9.
 */
public class ExchangeAdapter extends BaseAdapter {
    private Context mContext;

    public ExchangeAdapter(Context c)
    {
        mContext=c;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mThumbNames.length;
    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ExchangeLayout MyIBtn1;

        if(convertView==null)
        {

            MyIBtn1 = new ExchangeLayout(mContext,null);
            MyIBtn1.setImageResource(mThumbIds[position]);
            MyIBtn1.setText(mThumbNames[position]);
            MyIBtn1.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            //MyIBtn1.setLayoutParams(new GridView.LayoutParams(350, 500));
        }
        else
        {
            MyIBtn1 =(ExchangeLayout) convertView;
        }

        return MyIBtn1;
    }



    private Integer[] mThumbIds={//显示的图片数组
            R.drawable.ic_launcher,R.drawable.ic_launcher,
            R.drawable.ic_launcher,R.drawable.ic_launcher,
            R.drawable.ic_launcher,R.drawable.ic_launcher,
            R.drawable.ic_launcher,R.drawable.ic_launcher,
            R.drawable.ic_launcher,R.drawable.ic_launcher,
            R.drawable.ic_launcher,R.drawable.ic_launcher,
            R.drawable.ic_launcher,R.drawable.ic_launcher,
            R.drawable.ic_launcher,R.drawable.ic_launcher,
            R.drawable.ic_launcher,R.drawable.ic_launcher,
            R.drawable.ic_launcher,R.drawable.ic_launcher,
            R.drawable.ic_launcher,R.drawable.ic_launcher,
            R.drawable.ic_launcher,R.drawable.ic_launcher,
            R.drawable.ic_launcher,R.drawable.ic_launcher,
            R.drawable.ic_launcher,R.drawable.ic_launcher,
            R.drawable.ic_launcher,R.drawable.ic_launcher,
            R.drawable.ic_launcher,R.drawable.ic_launcher,
    };

    private String[] mThumbNames ={//显示的图片数组
            "校徽\n积分：50","纪念明信片\n积分：200",
            "水杯\n积分：500","纪念衫\n积分：1000",
            "校徽\n积分：50","纪念明信片\n积分：200",
            "水杯\n积分：500","纪念衫\n积分：1000",
            "校徽\n积分：50","纪念明信片\n积分：200",
            "水杯\n积分：500","纪念衫\n积分：1000",
            "校徽\n积分：50","纪念明信片\n积分：200",
            "水杯\n积分：500","纪念衫\n积分：1000",
            "ic_launcher","ic_launcher",
            "ic_launcher","ic_launcher",
            "ic_launcher","ic_launcher",
            "ic_launcher","ic_launcher",
            "ic_launcher","ic_launcher",
            "ic_launcher","ic_launcher",
            "ic_launcher","ic_launcher",
            "ic_launcher","ic_launcher",
    };
}