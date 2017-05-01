package com.example.nam.minisn.Fragmen;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nam.minisn.Activity.Main;
import com.example.nam.minisn.Adapter.ListviewFriendAdapter;
import com.example.nam.minisn.ItemListview.Conversation;
import com.example.nam.minisn.ItemListview.Friend;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SQLiteDataController;
import com.example.nam.minisn.Util.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.SocketHandler;

/**
 * Created by Nam on 2/24/2017.
 */

public class FragmenFriend extends Fragment {
    private View rootView;
    private Bundle bundle = new Bundle();
    private ListView lvFriend;
    private ProgressDialog progressDialog;
    private ListviewFriendAdapter adapter;
    private ArrayList<Friend> friends = new ArrayList<>();
    private int use_id;
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
        return rootView;
    }

    public void init(){
        use_id = SharedPrefManager.getInstance(getActivity()).getInt(Const.ID);
        lvFriend = (ListView)rootView.findViewById(R.id.tab_Friend_lvFriend);

        adapter = new ListviewFriendAdapter(getActivity(),R.layout.item_lvfriend,friends);
        lvFriend.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        int index = Main.tabLayout.getSelectedTabPosition();
        Log.d(Const.TAG,"indexTab: "+String.valueOf(index));
        friends.clear();
        getListFriend();
        adapter.notifyDataSetChanged();
    }

    public void getListFriend(){
        SQLiteDataController db = new SQLiteDataController(getActivity());
        try {
            db.isCreatedDatabase();
            db.openDataBase();
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
            Cursor cursor = db.getDatabase().rawQuery(sql, null);
            while (cursor.moveToNext()) {
                int fri_id= cursor.getInt(cursor.getColumnIndex(Const.FRIENDS_COL1));
                String fri_username = cursor.getString(cursor.getColumnIndex(Const.FRIENDS_COL2));
                String fri_display = cursor.getString(cursor.getColumnIndex(Const.FRIENDS_COL3));
                Friend friend = new Friend(fri_id,fri_username,fri_display);
                friends.add(friend);
            }
            db.getDatabase().close();

            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(Const.TAG, e.getMessage());
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
        }
    }

    AdapterView.OnItemClickListener lvFriendClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };
}
