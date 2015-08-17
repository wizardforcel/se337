package com.wizard.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.wizard.myapplication.entity.Comment;
import com.wizard.myapplication.entity.Event;
import com.wizard.myapplication.entity.User;
import com.wizard.myapplication.util.Api;
import com.wizard.myapplication.util.Common;
import com.wizard.myapplication.util.TabUtil;
import com.wizard.myapplication.util.WizardHTTP;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends Activity {

    private static final int LOAD_DATA_SUCCESS = 0;
    private static final int LOAD_DATA_FAIL = 1;
    private static final int ADD_COMMENT_FAIL = 2;
    private static final int ADD_COMMENT_SUCCESS = 3;
    private static final int ZAN_SUCCESS = 4;
    private static final int ZAN_FAIL = 5;
    private static final int CAI_SUCCESS = 6;
    private static final int CAI_FAIL = 7;


    private static final int ACTIVITY_LOGIN = 0;

    private LinearLayout commentPage;
    private Button addCommentButton;
    private EditText commentInput;
    private TabHost tHost;
    private AlertDialog voteDialog;
    private Handler handler;
    private TextView currentVoteText;

    private User user;
    private Event e;
    private String myComment;
    private List<Comment> comments
            = new ArrayList<Comment>();
    private Comment currentComment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_event);

        Intent i = getIntent();
        user = (User) i.getSerializableExtra("user");
        e = (Event) i.getSerializableExtra("event");

        commentPage = (LinearLayout) findViewById(R.id.commentPage);
        addCommentButton = (Button) findViewById(R.id.addComment);
        commentInput = (EditText) findViewById(R.id.commentInput);
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { addCommentButtonOnClick(); }
        });

        tHost = (TabHost) findViewById(R.id.tabHost);
        tHost.setup();
        tHost.addTab(tHost.newTabSpec("简介").setIndicator("简介").setContent(R.id.contentPage0));
        tHost.addTab(tHost.newTabSpec("评论").setIndicator("评论").setContent(R.id.commentPage0));
        tHost.setCurrentTab(0);
        TabUtil.updateTab(tHost);
        tHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) { TabUtil.updateTab(EventActivity.this.tHost); }
        });

        TextView unText = (TextView) findViewById(R.id.unText);
        unText.setText(e.getUn());
        TextView contentText = (TextView) findViewById(R.id.contentText);
        contentText.setText(e.getContent());
        TextView titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText(e.getName());
        TextView locText = (TextView) findViewById(R.id.locText);
        locText.setText(e.getLocation());
        TextView maxText = (TextView) findViewById(R.id.maxText);
        maxText.setText(e.getMaxPeople() + "");
        TextView startText = (TextView) findViewById(R.id.startText);
        startText.setText(Common.dateFormat(e.getStartDate()));
        TextView endText = (TextView) findViewById(R.id.endText);
        endText.setText(Common.dateFormat(e.getEndDate()));
        TextView enrollStartText = (TextView) findViewById(R.id.enrollStartText);
        enrollStartText.setText(Common.dateFormat(e.getEnrollStartDate()));
        TextView enrollEndText = (TextView) findViewById(R.id.enrollEndText);
        enrollEndText.setText(Common.dateFormat(e.getEnrollEndDate()));

        TextView tv = (TextView) findViewById(R.id.titlebar_name);
        tv.setText(e.getName());
        Button returnButton = (Button) findViewById(R.id.titlebar_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });

        closeKeyboard();

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

        handler = new Handler()
        {
            @Override
            public void handleMessage(android.os.Message msg) {
                EventActivity.this.handleMessage(msg);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() { threadLoadData(); }
        }).start();
    }

    private void handleMessage(android.os.Message msg)
    {
        Bundle b = msg.getData();
        int type = b.getInt("type");
        switch(type) {
            case LOAD_DATA_SUCCESS:
                for (Comment c : comments)
                    addCommentToView(c);
                break;
            case LOAD_DATA_FAIL:
                Toast.makeText(EventActivity.this, "数据加载失败！" + b.getString("errmsg"), Toast.LENGTH_SHORT).show();
                break;
            case ADD_COMMENT_FAIL:
                Toast.makeText(EventActivity.this, "评论失败！" + b.getString("errmsg"), Toast.LENGTH_SHORT).show();
                break;
            case ADD_COMMENT_SUCCESS:
                Toast.makeText(EventActivity.this, "评论成功！", Toast.LENGTH_SHORT).show();
                addCommentToView(comments.get(comments.size() - 1));
                closeKeyboard();
                commentInput.setText("");
                break;
            case ZAN_SUCCESS:
                Toast.makeText(EventActivity.this, "点赞成功！", Toast.LENGTH_SHORT).show();
                refreshCurrentVoteText();
                voteDialog.hide();
                break;
            case ZAN_FAIL:
                Toast.makeText(EventActivity.this, "点赞失败！" + b.getString("errmsg"), Toast.LENGTH_SHORT).show();
                break;
            case CAI_SUCCESS:
                Toast.makeText(EventActivity.this, "点踩成功！", Toast.LENGTH_SHORT).show();
                refreshCurrentVoteText();
                voteDialog.hide();
                break;
            case CAI_FAIL:
                Toast.makeText(EventActivity.this, "点踩失败！" + b.getString("errmsg"), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void refreshCurrentVoteText()
    {
        currentVoteText.setText(currentComment.getLike() + "/" + currentComment.getDislike());
    }

    private void closeKeyboard()
    {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(commentInput.getWindowToken(), 0);
    }

    private void addCommentButtonOnClick()
    {
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, ACTIVITY_LOGIN);
            return;
        }

        myComment = commentInput.getText().toString();
        if (myComment.equals("")) {
            Toast.makeText(this, "请输入评论", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread((new Runnable() {
            @Override
            public void run() { threadAddComment(); }
        })).start();
    }

    private void addCommentToView(final Comment c)
    {
        LinearLayout linear = (LinearLayout) getLayoutInflater().inflate(R.layout.comment_linear, null);
        TextView unText = (TextView) linear.findViewById(R.id.unText);
        unText.setText(c.getUn().replace("\n", "") + ":");
        TextView coText = (TextView) linear.findViewById(R.id.contentText);
        coText.setText(c.getContent());
        final TextView voteText = (TextView) linear.findViewById(R.id.voteText);
        voteText.setText(c.getLike() + "/" + c.getDislike());
        ImageView avatarImage = (ImageView) linear.findViewById(R.id.avatarImage);
        avatarImage.setImageBitmap(BitmapFactory.decodeByteArray(c.getAvatar(), 0, c.getAvatar().length));
        voteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentComment = c;
                currentVoteText = voteText;
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

    private void threadAddComment()
    {
        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);

            Comment c = Api.addActivityComment(http, e.getId(), user, myComment);
            comments.add(c);

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
            http.setCharset("utf-8");

            comments = Api.getActivityComment(http, e.getId());

            Bundle b = new Bundle();
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

    private void threadZan()
    {
        try
        {
            WizardHTTP http = new WizardHTTP();
            http.setDefHeader(false);
            http.setCharset("utf-8");

            Api.commentLike(http, currentComment.getId());
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

            Api.commentDislike(http, currentComment.getId());
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
