package com.example.nam.minisn.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nam.minisn.Adapter.ListviewRequestFriendAdapter;
import com.example.nam.minisn.ItemListview.Friend;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RequestFriendActivity extends AppCompatActivity {
    private ListView lvRequestFriend;
    private ListviewRequestFriendAdapter adapter;
    private ArrayList<Friend> friends = new ArrayList<Friend>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_request_friend);
        Friend friend = new Friend("namlv","Abc",1);
        friends.add(friend);
        friends.add(new Friend("namlv","Abc",1));

        init();
    }

    public void init(){
        lvRequestFriend = (ListView)findViewById(R.id.request_friend_lv);
        adapter = new ListviewRequestFriendAdapter(this,R.layout.item_listview_request_friend,friends);
        lvRequestFriend.setAdapter(adapter);
//        friends.add(icon_new Friend("namlv","Abc",1));
//        adapter.notifyDataSetChanged();
        Log.d(Const.TAG,"here");
//        getListRequestFriend();
    }
    public void getListRequestFriend(){
        String token = SharedPrefManager.getInstance(getApplicationContext()).getString(Const.TOKEN);
        RequestQueue request = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, Const.URL_GET_REQUEST_FRIEND +
                Const.TOKEN +"="+token
                ,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try{
                    if (Const.CODE_OK == jsonObject.getInt(Const.CODE)){
                        Log.d(Const.TAG,"request success");
                        JSONArray listRequest = jsonObject.getJSONArray(Const.DATA);
                        for (int i = 0;i<listRequest.length();i++){
                            JSONObject obj = listRequest.getJSONObject(i);
                            Friend friend = new Friend();
                            friend.setUsername(obj.getString(Const.USERNAME));
                            friend.setDisplayName(obj.getString(Const.DISPLAY_NAME));
                            friend.setGender(obj.getInt(Const.ID));
                            friends.add(friend);
                        }
                        adapter.notifyDataSetChanged();
                    }else
                        Toast.makeText(getApplicationContext(),"Co loi xay ra",Toast.LENGTH_SHORT).show();
                }catch (JSONException e){
                    e.printStackTrace();
                    Log.d(Const.TAG,"JSON error: "+ e.getMessage());
                }

            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(Const.TAG,"Request Error");
            }
        });

        request.add(objectRequest);
//        progressDialog.dismiss();
    }
}
