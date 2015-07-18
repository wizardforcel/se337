package com.wizard.myapplication;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.Context;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.wizard.myapplication.entity.Building;
import com.wizard.myapplication.entity.Comment;
import com.wizard.myapplication.entity.User;
import com.wizard.myapplication.util.UrlConfig;
import com.wizard.myapplication.util.WizardHTTP;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BuildingActivity extends Activity {

    private static final int LOAD_DATA_SUCCESS = 0;
    private static final int LOAD_DATA_FAIL = 1;
    private static final int ADD_COMMENT_SUCCESS = 2;
    private static final int ADD_COMMENT_FAIL = 3;
    private static final int ZAN_SUCCESS = 4;
    private static final int ZAN_FAIL = 5;
    private static final int CAI_SUCCESS = 6;
    private static final int CAI_FAIL = 7;

    private final static int ACTIVITY_LOGIN = 1;

    private LinearLayout commentPage;
    private Button addCommentButton;
    private EditText commentInput;
    private TextView contentText;
    private ImageView image;
    private Handler handler;
    private TabHost tHost;
    private AlertDialog voteDialog;
    private TextView currentVoteText;

    private int campusId;
    private Building building;
    private User user;
    private List<Comment> comments
            = new ArrayList<Comment>();
    private boolean loaded = false;
    private String myComment;
    private Comment currentComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_building);

        Intent i = getIntent();
        building = (Building) i.getSerializableExtra("building");
        user = (User) i.getSerializableExtra("user");
        campusId = i.getIntExtra("campusId", -1);

        TextView title = (TextView) findViewById(R.id.titlebar_name);
        title.setText(building.getName());
        Button returnButton = (Button) findViewById(R.id.titlebar_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish(); }
        });

        commentPage = (LinearLayout) findViewById(R.id.commentPage);
        addCommentButton = (Button) findViewById(R.id.addComment);
        commentInput = (EditText) findViewById(R.id.commentInput);
        contentText = (TextView) findViewById(R.id.contentText);
        image = (ImageView) findViewById(R.id.buildingImage);

        tHost = (TabHost) findViewById(R.id.tabHost);
        tHost.setup();
        tHost.addTab(tHost.newTabSpec("简介").setIndicator("简介").setContent(R.id.contentPage));
        tHost.addTab(tHost.newTabSpec("评论").setIndicator("评论").setContent(R.id.commentPage0));
        tHost.setCurrentTab(0);

        LinearLayout voteLinear = (LinearLayout) getLayoutInflater().inflate(R.layout.vote_linear, null);
        Button zanButton = (Button) voteLinear.findViewById(R.id.zanButton);
        Button caiButton = (Button) voteLinear.findViewById(R.id.caiButton);
        zanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { voteDialogZanButtonOnClick(); }
        });
        caiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { voteDialogCaiButtonOnClick(); }
        });
        voteDialog = new AlertDialog.Builder(this)
                .setView(voteLinear)
                .setNegativeButton("取消", null)
                .create();

        closeKeyboard(); //强行隐藏键盘

        contentText.setText(building.getContent());
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { addCommentButtonOnClick(); }
        });

        handler = new Handler()
        {
            @Override
            public void handleMessage(android.os.Message msg) {
                BuildingActivity.this.handleMessage(msg);
            }
        };

        if(!loaded) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    threadLoadData();
                }
            }).start();
        }
    }

    private void closeKeyboard()
    {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(commentInput.getWindowToken(), 0);
    }

    private void handleMessage(android.os.Message msg)
    {
        Bundle b = msg.getData();
        int type = b.getInt("type");
        switch(type)
        {
            case LOAD_DATA_SUCCESS:
                loaded = true;
                byte[] img = b.getByteArray("img");
                if(img != null)
                    image.setImageBitmap(BitmapFactory.decodeByteArray(img, 0, img.length));
                for(Comment c : comments)
                    addComment(c);
                break;
            case LOAD_DATA_FAIL:
                Toast.makeText(BuildingActivity.this, "数据加载失败！" + b.getString("errmsg"), Toast.LENGTH_SHORT).show();
                break;
            case ADD_COMMENT_FAIL:
                Toast.makeText(BuildingActivity.this, "评论失败！" + b.getString("errmsg"), Toast.LENGTH_SHORT).show();
                break;
            case ADD_COMMENT_SUCCESS:
                Toast.makeText(BuildingActivity.this, "评论成功！", Toast.LENGTH_SHORT).show();
                addComment(comments.get(comments.size() - 1));
                closeKeyboard();
                commentInput.setText("");
                break;
            case ZAN_SUCCESS:
                Toast.makeText(BuildingActivity.this, "点赞成功！", Toast.LENGTH_SHORT).show();
                refreshCurrentVoteText();
                voteDialog.hide();
                break;
            case ZAN_FAIL:
                Toast.makeText(BuildingActivity.this, "点赞失败！" + b.getString("errmsg"), Toast.LENGTH_SHORT).show();
                break;
            case CAI_SUCCESS:
                Toast.makeText(BuildingActivity.this, "点踩成功！", Toast.LENGTH_SHORT).show();
                refreshCurrentVoteText();
                voteDialog.hide();
                break;
            case CAI_FAIL:
                Toast.makeText(BuildingActivity.this, "点踩失败！" + b.getString("errmsg"), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void refreshCurrentVoteText()
    {
        currentVoteText.setText(currentComment.getLike() + "/" + currentComment.getDislike());
    }

    private void addComment(Comment c)
    {
        LinearLayout linear = (LinearLayout) getLayoutInflater().inflate(R.layout.comment_linear, null);
        TextView unText = (TextView) linear.findViewById(R.id.unText);
        unText.setText(c.getUn() + ":");
        TextView coText = (TextView) linear.findViewById(R.id.contentText);
        coText.setText(c.getContent());
        TextView voteText = (TextView) linear.findViewById(R.id.voteText);
        voteText.setText(c.getLike() + "/" + c.getDislike());
        final Comment finalComment = c;
        final TextView finalVoteText = voteText;
        voteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentComment = finalComment;
                currentVoteText = finalVoteText;
                voteDialog.show();
            }
        });
        commentPage.addView(linear);
    }

    private void voteDialogZanButtonOnClick()
    {
        new Thread(new Runnable() {
            @Override
            public void run() { threadZan(); }
        }).start();
    }

    private void voteDialogCaiButtonOnClick()
    {
        new Thread(new Runnable() {
            @Override
            public void run() { threadCai(); }
        }).start();
    }

    private void addCommentButtonOnClick()
    {
        if (user == null) {
            Intent intent = new Intent(BuildingActivity.this, LoginActivity.class);
            intent.putExtra("campusId", campusId);
            startActivityForResult(intent, ACTIVITY_LOGIN);
            return;
        }

        myComment = commentInput.getText().toString();
        if (myComment.equals("")) {
            Toast.makeText(BuildingActivity.this, "请输入评论", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread((new Runnable() {
            @Override
            public void run() { threadAddComment(); }
        })).start();
    }

    private void threadZan()
    {
        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);
            http.setCharset("utf-8");

            String retStr = http.httpGet("http://" + UrlConfig.HOST + "/comment/like/" + currentComment.getId());
            currentComment.setLike(currentComment.getLike() + 1);

            Bundle b = new Bundle();
            b.putInt("type", ZAN_SUCCESS);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Bundle b = new Bundle();
            b.putInt("type", ZAN_FAIL);
            b.putString("errmag", ex.getMessage());
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
    }

    private void threadCai()
    {
        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);
            http.setCharset("utf-8");

            String retStr = http.httpGet("http://" + UrlConfig.HOST + "/comment/dislike/" + currentComment.getId());
            currentComment.setDislike(currentComment.getDislike() + 1);

            Bundle b = new Bundle();
            b.putInt("type", CAI_SUCCESS);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Bundle b = new Bundle();
            b.putInt("type", CAI_FAIL);
            b.putString("errmag", ex.getMessage());
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
    }

    private void threadAddComment()
    {
        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);
            http.setHeader("Content-Type", "application/json");

            JSONObject postJson = new JSONObject();
            postJson.put("viewId", building.getId());
            postJson.put("type", "view");
            postJson.put("content", myComment);
            postJson.put("userId", user.getId());

            String retStr = http.httpPost("http://" + UrlConfig.HOST + "/comment/add", postJson.toString());
            JSONObject retJson = new JSONObject(retStr);
            Comment c = new Comment();
            c.setId(retJson.getInt("id"));
            c.setUid(user.getId());
            c.setUn(user.getUn());
            c.setContent(myComment);
            comments.add(c);
            Log.d("BuildingAddComment",
                  "id: " + c.getId() + " uid: " + c.getUid() + " un: " + c.getUn());

            Bundle b = new Bundle();
            b.putInt("type", ADD_COMMENT_SUCCESS);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Bundle b = new Bundle();
            b.putInt("type", ADD_COMMENT_FAIL);
            b.putString("errmsg", ex.getMessage());
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
    }

    private void threadLoadData()
    {
        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);
            String retStr = http.httpGet("http://" + UrlConfig.HOST + "/picture/view/" + building.getId());
            JSONArray retArr = new JSONArray(retStr);

            byte[] imgData = null;
            if(retArr.length() != 0) {
                JSONObject retJson = retArr.getJSONObject(0);
                String imgPath = retJson.getString("path");
                imgPath = "http://" + UrlConfig.HOST + "/picture/" + imgPath.replace(".", "/");
                Log.d("BuildingImg", imgPath);
                imgData = http.httpGetData(imgPath);
            }

            http.setCharset("utf-8");
            retStr = http.httpGet("http://" + UrlConfig.HOST + "/comment/view/" + building.getId());
            retArr = new JSONArray(retStr);
            comments.clear();
            for(int i = 0; i < retArr.length(); i++)
            {
                JSONObject o = retArr.getJSONObject(i);
                Comment c = new Comment();
                c.setId(o.getInt("id"));
                int uid = o.getInt("userId");
                c.setUid(uid);
                String un = http.httpGet("http://" + UrlConfig.HOST + "/user/" + uid + "/userName/");
                c.setUn(un);
                c.setContent(o.getString("content"));
                c.setLike(o.getInt("likes"));
                c.setDislike(o.getInt("dislike"));
                comments.add(c);
                Log.d("BuildingComment", "id: " + c.getId() + " uid: " + c.getUid() + " un: " + c.getUn());
            }

            Bundle b = new Bundle();
            b.putByteArray("img", imgData);
            b.putInt("type", LOAD_DATA_SUCCESS);
            Message msg = handler.obtainMessage();
            msg.setData(b);
            handler.sendMessage(msg);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            Bundle b = new Bundle();
            b.putInt("type", LOAD_DATA_FAIL);
            b.putString("errmag", ex.getMessage());
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
