package com.wizard.myapplication.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ProgressBar;

import com.wizard.myapplication.R;

/**
 * Created by yuhan on 15-7-9.
 */
public class GallaryLayout extends GridLayout {

    private ImageView mImgView = null;
    private TextView mTextView = null;
    private Context mContext;
    private ProgressBar progressBar = null;


    public GallaryLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        LayoutInflater.from(context).inflate(R.layout.gallary, this, true);
        mContext = context;
        mImgView = (ImageView)findViewById(R.id.img);
        mTextView = (TextView)findViewById(R.id.text);
        mImgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        setPadding(0,10,0,10);
    }



    /*设置图片接口*/
    public void setImageResource(int resId){
        mImgView.setImageResource(resId);
    }

    /*设置文字接口*/
    public void setText(String str){
        mTextView.setText(str);
    }
    /*设置文字大小*/
    public void setTextSize(float size){
        mTextView.setTextSize(size);
    }

    //public void setImageSize(float size){
      //  mImgView.setLayoutParams(new GridView.LayoutParams(200, 200));
    //}

    public void setProgressBar(int max, int num) {
        progressBar.setMax(max);
        progressBar.setProgress(num);
        mTextView.append("\n\n" + String.valueOf(num) + "/" + String.valueOf(max));
    }

//     /*设置触摸接口*/
//    public void setOnTouch(OnTouchListener listen){
//        mImgView.setOnTouchListener(listen);
//        //mTextView.setOnTouchListener(listen);
//    }

}
