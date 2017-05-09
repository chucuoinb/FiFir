package com.example.nam.minisn.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nam.minisn.Adapter.ListviewRequestFriendAdapter;
import com.example.nam.minisn.ItemListview.Friend;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SQLiteDataController;
import com.example.nam.minisn.Util.SharedPrefManager;

import java.util.ArrayList;

public class RequestFriendActivity extends AppCompatActivity {
    private ListView lvRequestFriend;
    private static ListviewRequestFriendAdapter adapter;

    private static ArrayList<Friend> friends = new ArrayList<Friend>();
    private LinearLayout btnBack;
    private SQLiteDataController database;
    private int useId;
    private TextView toolbarText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_request_friend);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        friends.clear();
        database.openDataBase();
        friends.addAll(database.getListRequestFriend(useId));
        database.close();
        adapter.notifyDataSetChanged();
    }

    public void init(){
        toolbarText = (TextView)findViewById(R.id.toolbar_text);
        toolbarText.setText("Lời mời kết bạn");
        useId = SharedPrefManager.getInstance(getApplicationContext()).getInt(Const.ID);
        database = new SQLiteDataController(getApplicationContext());
        btnBack = (LinearLayout) findViewById(R.id.toolbar_btnback);
        lvRequestFriend = (ListView)findViewById(R.id.request_friend_lv);
        adapter = new ListviewRequestFriendAdapter(this,R.layout.item_listview_request_friend,friends);
        lvRequestFriend.setAdapter(adapter);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static ArrayList<Friend> getFriends() {
        return friends;
    }
    public static ListviewRequestFriendAdapter getAdapter() {
        return adapter;
    }
}
