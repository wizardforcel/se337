package com.wizard.myapplication.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wizard.myapplication.R;

/**
 * Created by yuhan on 15-7-14.
 */
public class Header extends RelativeLayout {
    private ImageView imgView;
    private TextView nameView;
    private TextView scoreView;
    private TextView unvView;
    private Context mContext;

    public Header(Context context, AttributeSet attrs) {
        super(context, attrs);
        //LayoutInflater.from(context).inflate(R.layout.header, this, true);
        mContext = context;
        imgView = (ImageView)findViewById(R.id.imageView);
        nameView = (TextView)findViewById(R.id.nameView);
        scoreView = (TextView)findViewById(R.id.scoreView);
        unvView = (TextView)findViewById(R.id.unvView);

    }

    public void setImgView(int resId) {
        imgView.setImageResource(resId);
    }

    public void setNameView(String name) {
        nameView.setText(name);
    }

    public void setScoreView(String score) {
        scoreView.setText("积分：" + score);
    }

    public void setUnvView(String unv) {
        unvView.setText(unv);
    }
}
