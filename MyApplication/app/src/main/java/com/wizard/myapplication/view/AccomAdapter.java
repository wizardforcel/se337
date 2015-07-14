package com.wizard.myapplication.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.wizard.myapplication.R;

/**
 * Created by yuhan on 15-7-9.
 */
public class AccomAdapter extends BaseAdapter {
    private Context mContext;

    public AccomAdapter(Context c)
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

        GallaryLayout MyIBtn1;

        if(convertView==null)
        {

            MyIBtn1 = new GallaryLayout(mContext,null);
            MyIBtn1.setImageResource(mThumbIds[position]);
            MyIBtn1.setText(mThumbNames[position]);
            //MyIBtn1.setTextSize(24.0f);
            MyIBtn1.setProgressBar(10, 5);
            MyIBtn1.setLayoutParams(new GridView.LayoutParams(350, 500));
        }
        else
        {
           MyIBtn1 =(GallaryLayout) convertView;
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
            "任务1","任务2",
            "任务3","任务4",
            "任务5","任务6",
            "任务6","任务7",
            "任务8","任务9",
            "任务10","任务11",
            "任务12","任务13",
            "任务14","任务15",
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