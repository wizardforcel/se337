package com.wizard.myapplication;

import android.app.Activity;

import android.content.Intent;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.wizard.myapplication.entity.Building;
import com.wizard.myapplication.entity.Comment;
import com.wizard.myapplication.entity.User;
import com.wizard.myapplication.util.UrlConfig;
import com.wizard.myapplication.util.WizardHTTP;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class BuildingActivity extends Activity {

    private static final int GET_IMAGE_SUCCESS = 0;
    private static final int GET_IMAGE_FAIL = 1;

    private final static int ACTIVITY_LOGIN = 1;

    private LinearLayout commentList;
    private Button addCommentButton;
    private EditText commentInput;
    private TextView contentText;
    private ImageView image;
    private Button returnButton;
    private Handler handler;

    private Building building;
    private User user;
    boolean imgLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_building);

        Intent i = getIntent();
        building = (Building) i.getSerializableExtra("building");
        user = (User) i.getSerializableExtra("user");

        TextView title = (TextView) findViewById(R.id.titlebar_name);
        commentList = (LinearLayout) findViewById(R.id.commentList);
        addCommentButton = (Button) findViewById(R.id.addComment);
        commentInput = (EditText) findViewById(R.id.commentInput);
        contentText = (TextView) findViewById(R.id.contentText);
        image = (ImageView) findViewById(R.id.buildingImage);

        //焦点设置在按钮上 防止输入框获取焦点弹出键盘
        addCommentButton.setFocusable(true);
        addCommentButton.setFocusableInTouchMode(true);
        addCommentButton.requestFocus();
        addCommentButton.requestFocusFromTouch();

        title.setText(building.getName());
        contentText.setText(building.getContent());

        List<Comment> comments = building.getComments();
        for(Comment comment: comments){
            addComment(comment.getUn(), comment.getContent());
        }

        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { addCommentButtonOnClick(); }
        });

        returnButton = (Button) findViewById(R.id.titlebar_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish(); }
        });

        handler = new Handler()
        {
            @Override
            public void handleMessage(android.os.Message msg) {
                BuildingActivity.this.handleMessage(msg);
            }
        };

        if(!imgLoaded) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    threadGetImage();
                }
            }).start();
        }
    }

    private void handleMessage(android.os.Message msg)
    {
        Bundle b = msg.getData();
        int type = b.getInt("type");
        switch(type)
        {
            case GET_IMAGE_SUCCESS:
                imgLoaded = true;
                byte[] img = b.getByteArray("img");
                image.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
                break;
            case GET_IMAGE_FAIL:
                Toast.makeText(BuildingActivity.this, "图片加载失败！" + b.getString("srrmag"), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void addComment(String un, String co)
    {
        LinearLayout linear = new LinearLayout(this);
        linear.setOrientation(LinearLayout.VERTICAL);
        linear.setPadding(10, 10, 10, 10);
        linear.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView unText = new TextView(this);
        unText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        unText.setText(un + ":");

        TextView coText = new TextView(this);
        coText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        coText.setText(co);

        linear.addView(unText);
        linear.addView(coText);
        commentList.addView(linear);
    }

    private void addCommentButtonOnClick()
    {
        if (user == null) {
            Intent intent = new Intent();
            intent.setClass(BuildingActivity.this, LoginActivity.class);
            startActivityForResult(intent, ACTIVITY_LOGIN);
            return;
        }

        String myComment = commentInput.getText().toString();
        if (myComment.equals("")) {
            Toast.makeText(BuildingActivity.this, "请输入评论", Toast.LENGTH_SHORT).show();
        } else {
            addComment(user.getUn(), myComment);
            //TODO: POST
        }
    }

    private void threadGetImage()
    {
        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);
            String retStr = http.httpGet("http://" + UrlConfig.HOST + "/picture/view/" + building.getId());
            JSONArray retArr = new JSONArray(retStr);
            if(retArr.length() == 0)
                return;

            JSONObject retJson = retArr.getJSONObject(0);
            String imgPath = retJson.getString("path");
            imgPath = "http://" + UrlConfig.HOST + "/picture/" + imgPath.replace(".", "/");
            Log.d("img", imgPath);
            byte[] imgData = http.httpGetData(imgPath);

            Bundle b = new Bundle();
            b.putByteArray("img", imgData);
            b.putInt("type", GET_IMAGE_SUCCESS);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Bundle b = new Bundle();
            b.putInt("type", GET_IMAGE_FAIL);
            b.putString("errmsg", ex.getMessage());
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ACTIVITY_LOGIN && resultCode == Activity.RESULT_OK){
            user = (User) data.getSerializableExtra("user");
            setResult(Activity.RESULT_OK, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.building, menu);
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
