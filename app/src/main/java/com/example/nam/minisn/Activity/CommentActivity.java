package com.example.nam.minisn.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nam.minisn.Adapter.CommentAdapter;
import com.example.nam.minisn.ItemListview.Comment;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SharedPrefManager;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {
    private CircleImageView avatar;
    private TextView nameUserStatus;
    private TextView timeStatus;
    private TextView status;
    private ImageView btLikeStatus;
    private ImageView btGoChat;
    private ImageView btSend;
    private EditText edInput;
    private ListView lvComment;
    private String token;
    private int useId;
    private int idStatus;
    private Intent intent;
    private Bundle bundle;
    private ArrayList<Comment> data;
    private CommentAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_comment);
        init();
    }

    public void init(){
        avatar = (CircleImageView)findViewById(R.id.comment_ava_status);
        nameUserStatus = (TextView)findViewById(R.id.comment_name);
        timeStatus = (TextView)findViewById(R.id.comment_status_time);
        status = (TextView)findViewById(R.id.comment_status);
        btLikeStatus = (ImageView)findViewById(R.id.comment_bt_like);
        btGoChat = (ImageView)findViewById(R.id.btn_go_chat);
        btSend = (ImageView)findViewById(R.id.comment_bt_send);
        edInput = (EditText)findViewById(R.id.comment_input);
        lvComment = (ListView)findViewById(R.id.comment_lv);

        token = SharedPrefManager.getInstance(getApplicationContext()).getString(Const.TOKEN);
        useId = SharedPrefManager.getInstance(getApplicationContext()).getInt(Const.ID);
        intent = getIntent();
        bundle = intent.getBundleExtra(Const.PACKAGE);
        idStatus = bundle.getInt(Const.ID);

        data = new ArrayList<>();
        Comment comment = new Comment(1,1,"Lại Văn Nam","hihihi",System.currentTimeMillis()/1000);
        data.add(comment);
        adapter = new CommentAdapter(this,R.layout.item_lv_comment,data);
        lvComment.setAdapter(adapter);
    }
}
