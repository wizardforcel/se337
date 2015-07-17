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
            //MyIBtn1.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        else
        {
            MyIBtn1 =(GallaryLayout) convertView;
        }

        return MyIBtn1;
    }
    private Integer[] mThumbIds={//显示的图片数组
            R.drawable.meishi,R.drawable.xueba,
            R.drawable.wenyi,R.drawable.yundong,
            R.drawable.zhainan,R.drawable.xiaoyuan,
            R.drawable.shenghuo,R.drawable.shejiao,

    };

    private String[] mThumbNames ={//显示的图片数组
            "美食家\n品尝食堂美食\n3/5","大学霸\n在图书馆学习3次\n1/3",
            "文艺青年\n大礼堂签到3次\n0/3","运动健将\n体育场签到3次\n3/3",
            "阳光御宅\n1天未在寝室楼以外签到\n0/1","校园达人\n去过10个以上建筑\n3/10",
            "热爱生活\n教育超市签到3次\n2/3","社交达人\n小组活动室签到3次\n0/3",
    };
}