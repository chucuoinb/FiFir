package com.example.nam.minisn.Fragmen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nam.minisn.Activity.RequestFriendActivity;
import com.example.nam.minisn.Adapter.ListviewFriendAdapter;
import com.example.nam.minisn.ItemListview.Friend;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SQLiteDataController;
import com.example.nam.minisn.Util.SharedPrefManager;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Nam on 2/24/2017.
 */

public class FragmenFriend extends Fragment implements View.OnClickListener{
    private View rootView;
    private Bundle bundle = new Bundle();
    private ListView lvFriend;
    private ProgressDialog progressDialog;
    private ListviewFriendAdapter adapter;
    private ArrayList<Friend> friends = new ArrayList<>();
    private int use_id;
    private LinearLayout requestFriend;
    private TextView newRequestFriendSize;
    private LinearLayout newRequest;
    private SQLiteDataController database;
    private FloatingActionButton fabMain,fabConversation,fabFriend,fabRequestFriend;
    private Animation show_fab1, hide_fab1,show_fab2, hide_fab2,show_fab3, hide_fab3;
    private boolean isOpenSubMenu = false;
    public FragmenFriend() {
    }

    public static FragmenFriend newInstance(Bundle bundle){
        FragmenFriend fragmen = new FragmenFriend();

        fragmen.setArguments(bundle);
        return fragmen;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dialog);
//        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        rootView = inflater.inflate(R.layout.layout_tab_friend,container,false);
        bundle = getArguments();
//        friends = (ArrayList<Friend>)bundle.getSerializable(Const.DB_FRIEND);
        init();
        animation();
        return rootView;
    }

    public void init(){
        fabMain = (FloatingActionButton)rootView.findViewById(R.id.main_fab);
        fabConversation = (FloatingActionButton)rootView.findViewById(R.id.fab_1);
        fabFriend = (FloatingActionButton)rootView.findViewById(R.id.fab_2);
        fabRequestFriend = (FloatingActionButton)rootView.findViewById(R.id.fab_3);
        database = new SQLiteDataController(getActivity());
        requestFriend = (LinearLayout)rootView.findViewById(R.id.friend_request);
        newRequestFriendSize = (TextView)rootView.findViewById(R.id.friend_newRequest_count);
        newRequest = (LinearLayout)rootView.findViewById(R.id.friend_newRequest);
        use_id = SharedPrefManager.getInstance(getActivity()).getInt(Const.ID);
        lvFriend = (ListView)rootView.findViewById(R.id.tab_Friend_lvFriend);

        adapter = new ListviewFriendAdapter(getActivity(),R.layout.item_lvfriend,friends);
        lvFriend.setAdapter(adapter);
        requestFriend.setOnClickListener(requestFriendClick);
        fabConversation.setOnClickListener(this);
        fabMain.setOnClickListener(this);
        fabFriend.setOnClickListener(this);
        fabRequestFriend.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        database.openDataBase();
        int countRequest = database.getCountRequestFriend(use_id);
            newRequestFriendSize.setText(String.valueOf(countRequest));
        if (countRequest>0){
            newRequest.setVisibility(View.VISIBLE);
        }
        else
            newRequest.setVisibility(View.INVISIBLE);
        database.close();
        friends.clear();
        getListFriend();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
//        isOpenSubMenu = false;
//        hideSubMenu();
    }

    public void getListFriend(){
            database.openDataBase();
            String sql = Const.SELECT +
                    Const.FRIENDS_COL1 +
                    "," +
                    Const.FRIENDS_COL2 +
                    "," +
                    Const.FRIENDS_COL3 +
                    "," +
                    Const.FRIENDS_COL5 +
                    Const.FROM +
                    Const.DB_FRIEND +
                    Const.WHERE +
                    Const.FRIENDS_COL4 +
                    "= '" +
                    use_id +
                    "'";
            Cursor cursor = database.getDatabase().rawQuery(sql, null);
            while (cursor.moveToNext()) {
                int fri_id= cursor.getInt(cursor.getColumnIndex(Const.FRIENDS_COL1));
                String fri_username = cursor.getString(cursor.getColumnIndex(Const.FRIENDS_COL2));
                String fri_display = cursor.getString(cursor.getColumnIndex(Const.FRIENDS_COL3));
                Friend friend = new Friend(fri_id,fri_username,fri_display);
                friends.add(friend);
            }
            database.close();

            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
    }

    public View.OnClickListener requestFriendClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), RequestFriendActivity.class);
            startActivity(intent);
        }
    };

    AdapterView.OnItemClickListener lvFriendClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };
    public void animation(){
        show_fab1 = AnimationUtils.loadAnimation(getActivity(),R.anim.fab1_show);
        hide_fab1 = AnimationUtils.loadAnimation(getActivity(),R.anim.fab1_hide);
        show_fab2 = AnimationUtils.loadAnimation(getActivity(),R.anim.fab2_show);
        hide_fab2 = AnimationUtils.loadAnimation(getActivity(),R.anim.fab2_hide);
        show_fab3 = AnimationUtils.loadAnimation(getActivity(),R.anim.fab3_show);
        hide_fab3 = AnimationUtils.loadAnimation(getActivity(),R.anim.fab3_hide);


//        hideFab();
    }
    public void showSubMenu(){
        fabMain.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_forward));
        showEachFab(fabConversation,show_fab1,(float)1.7,(float)0.25);
        showEachFab(fabFriend,show_fab2,(float) 1.5,(float) 1.5);
        showEachFab(fabRequestFriend,show_fab3,(float)0.25,(float)1.7);
    }
    public void hideSubMenu(){
        fabMain.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_backward));
        hideEachFab(fabConversation,hide_fab1,(float)1.7,(float)0.25);
        hideEachFab(fabFriend,hide_fab2,(float) 1.5,(float) 1.5);
        hideEachFab(fabRequestFriend,hide_fab3,(float)0.25,(float)1.7);
    }
    public void showEachFab(FloatingActionButton fab, Animation anim, float x, float y){
        fab.startAnimation(anim);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)fab.getLayoutParams();
        layoutParams.rightMargin += (int) (fab.getWidth() * x);
        layoutParams.bottomMargin += (int) (fab.getHeight() * y);
        fab.setLayoutParams(layoutParams);
        fab.setVisibility(View.VISIBLE);
        fab.setClickable(true);
    }
    public void hideEachFab(FloatingActionButton fab, Animation anim,float x, float y){
        fab.startAnimation(anim);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab.getLayoutParams();
        layoutParams.rightMargin -= (int) (fab.getWidth() * x);
        layoutParams.bottomMargin -= (int) (fab.getHeight() * y);
        fab.setLayoutParams(layoutParams);
        fab.setVisibility(View.INVISIBLE);
        fab.setClickable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_fab:
                if (isOpenSubMenu){
                    hideSubMenu();
                    isOpenSubMenu = false;
                }
                else{
                    showSubMenu();
                    isOpenSubMenu = true;
                }
                break;
            case R.id.fab_3:
//                Intent intent = new Intent(getApplicationContext(),RequestFriendActivity.class);
//                startActivity(intent);
                break;
            case R.id.fab_2:
//                conversationTab.setVisibility(View.INVISIBLE);
                break;
            case R.id.fab_1:
//                fab1Click();
//                Toast.makeText(Main.this, String.valueOf(tabLayout.getSelectedTabPosition()), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
