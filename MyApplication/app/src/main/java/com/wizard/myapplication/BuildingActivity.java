package com.wizard.myapplication;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.wizard.myapplication.R;
import com.wizard.myapplication.entity.Building;
import com.wizard.myapplication.entity.Campus;
import com.wizard.myapplication.entity.Comment;
import com.wizard.myapplication.entity.DataManager;
import com.wizard.myapplication.entity.User;

import java.util.List;

public class BuildingActivity extends Activity {

    private final static int ACTIVITY_LOGIN = 1;

    private TableLayout commentTable;
    private Button addComment;
    private EditText commentInput;
    private TextView contentText;
    private ImageView image;
    private Button returnButton;

    private Building building;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_building);

        Intent i = getIntent();
        building = (Building) i.getSerializableExtra("building");
        user = (User) i.getSerializableExtra("user");

        TextView title = (TextView) findViewById(R.id.titlebar_name);
        commentTable = (TableLayout) findViewById(R.id.commentTable);
        addComment = (Button) findViewById(R.id.addComment);
        commentInput = (EditText) findViewById(R.id.commentInput);
        contentText = (TextView) findViewById(R.id.contentText);
        image = (ImageView) findViewById(R.id.buildingImage);

        title.setText(building.getName());
        contentText.setText(building.getContent());

        List<Comment> comments = building.getComments();
        for(Comment comment: comments){
            TableRow tableRow = new TableRow(this);
            TextView un = new TextView(this);
            un.setText(comment.getUn() + ":");

            TextView content = new TextView(this);
            content.setText(comment.getContent());
            tableRow.addView(un);
            tableRow.addView(content);
            commentTable.addView(tableRow);
        }

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user == null){
                    Intent intent = new Intent();
                    intent.setClass(BuildingActivity.this, LoginActivity.class);
                    startActivityForResult(intent, ACTIVITY_LOGIN);
                }
                else {
                    String myComment = commentInput.getText().toString();
                    if (myComment.equals("")) {
                        Toast.makeText(BuildingActivity.this, "请输入评论", Toast.LENGTH_SHORT).show();
                    } else {
                        TableRow tableRow = new TableRow(BuildingActivity.this);
                        TextView name = new TextView(BuildingActivity.this);
                        TextView comment = new TextView(BuildingActivity.this);
                        name.setText("我:");
                        comment.setText(myComment);
                        tableRow.addView(name);
                        tableRow.addView(comment);
                        commentTable.addView(tableRow);


                        //添加到数据库（未实现）

                    }
                }
            }
        });

        returnButton = (Button) findViewById(R.id.titlebar_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish(); }
        });



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
