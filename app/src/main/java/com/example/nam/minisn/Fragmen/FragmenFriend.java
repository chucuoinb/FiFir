package com.example.nam.minisn.Fragmen;

import android.app.ProgressDialog;
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
import com.example.nam.minisn.Adapter.ListviewFriendAdapter;
import com.example.nam.minisn.ItemListview.Friend;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
    public FragmenFriend() {
    }

    public static FragmenFriend newInstance(Bundle bundle){
        FragmenFriend fragmen = new FragmenFriend();

        fragmen.setArguments(bundle);
        return fragmen;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_tab_friend,container,false);
        bundle = getArguments();
        init();
        return rootView;
    }

    public void init(){
        lvFriend = (ListView)rootView.findViewById(R.id.tab_Friend_lvFriend);
        progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        adapter = new ListviewFriendAdapter(getActivity(),R.layout.item_lvfriend,friends);
        lvFriend.setAdapter(adapter);
        getListFriend();
    }

    public void getListFriend(){
        RequestQueue request = Volley.newRequestQueue(getActivity());
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, Const.URL_GET_LIST_FRIEND + "/?"+
                Const.TOKEN +"="+bundle.getString(Const.TOKEN)
                ,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try{
                    if (Const.CODE_OK == jsonObject.getInt(Const.CODE)){
                        JSONArray listFriend = jsonObject.getJSONArray(Const.DATA);
                        for (int i = 0;i<listFriend.length();i++){
                            JSONObject obj = listFriend.getJSONObject(i);
                            Friend friend = new Friend();
                            friend.setUsername(obj.getString(Const.USERNAME));
                            friend.setDisplayName(obj.getString(Const.DISPLAY_NAME));
                            friend.setGender(obj.getInt(Const.GENDER));
                            friends.add(friend);
                        }
                        adapter.notifyDataSetChanged();
                    }else
                        Toast.makeText(getActivity(),"Co loi xay ra",Toast.LENGTH_SHORT).show();
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
        progressDialog.dismiss();

    }

    AdapterView.OnItemClickListener lvFriendClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };
}
