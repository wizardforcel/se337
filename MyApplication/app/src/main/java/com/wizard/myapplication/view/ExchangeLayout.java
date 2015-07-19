package com.wizard.myapplication.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wizard.myapplication.R;

/**
 * Created by yuhan on 15-7-16.
 */
public class ExchangeLayout extends GridLayout {

    private ImageView mImgView = null;
    private TextView mTextView = null;
    private Context mContext;
    private Button button;


    public ExchangeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        LayoutInflater.from(context).inflate(R.layout.exchange, this, true);
        mContext = context;
        mImgView = (ImageView)findViewById(R.id.img);
        mTextView = (TextView)findViewById(R.id.text);
        mImgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        button = (Button)findViewById(R.id.button);
        //setPadding(0,10,0,10);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext).setTitle("系统提示")//设置对话框标题

                        .setMessage("是否确认兑换")//设置显示的内容

                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override

                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                                // TODO Auto-generated method stub


                            }
                        }).setNegativeButton("放弃", new DialogInterface.OnClickListener() {//添加返回按钮


                    @Override

                    public void onClick(DialogInterface dialog, int which) {//响应事件

                        // TODO Auto-generated method stub

                        Log.i("alertdialog", " 请保存数据！");

                    }

                }).show();
            }
        });
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



//     /*设置触摸接口*/
//    public void setOnTouch(OnTouchListener listen){
//        mImgView.setOnTouchListener(listen);
//        //mTextView.setOnTouchListener(listen);
//    }

}
