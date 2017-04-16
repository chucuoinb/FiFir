package com.example.nam.minisn.Fragmen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.nam.minisn.Activity.ChatActivity;
import com.example.nam.minisn.Adapter.ListviewConversationAdapter;
import com.example.nam.minisn.ItemListview.Friend;
import com.example.nam.minisn.ItemListview.ItemListviewConversation;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nam on 2/21/2017.
 */

public class FragmenConversation extends Fragment {
    private View rootView;
    private ListView lvConversation;
    private ArrayList<ItemListviewConversation> data = new ArrayList<>();
    private Bundle bundle;
    private ListviewConversationAdapter adapter;
    private ProgressDialog progressDialog;
    private Intent intent;
    public FragmenConversation(){

    }

    public static FragmenConversation newInstance(Bundle bundle){
        FragmenConversation fragmen = new FragmenConversation();

        fragmen.setArguments(bundle);
        return fragmen;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_tab_conversation,container,false);
        bundle = getArguments();
        init();
        return rootView;
    }

    public void init(){
        lvConversation = (ListView)rootView.findViewById(R.id.tab_Conversation_lv);
        progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        adapter = new ListviewConversationAdapter(getActivity(),R.layout.item_lvconversation,data);
        lvConversation.setAdapter(adapter);
        getListConversation(bundle.getString(Const.TOKEN));
        lvConversation.setOnItemClickListener(itemLvConversationCkick);
    }

    public void getListConversation(String token ){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, Const.URL_GET_LIST_CONVERSATION + "/?"+
                Const.TOKEN +"="+token
                ,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try{
                    if (jsonObject.getInt(Const.CODE) == Const.CODE_OK) {
                        JSONArray listConversation = jsonObject.getJSONArray(Const.DATA);
                        int leght = listConversation.length();
                        for(int i=0;i<leght;i++){
                            ArrayList<Friend> friends = new ArrayList<>();
                            JSONObject object = listConversation.getJSONObject(i);
                            ItemListviewConversation item = new ItemListviewConversation();
                            item.setId(object.getInt(Const.ID));
                            JSONArray listUser = object.getJSONArray(Const.LIST_USER);
                            for (int j =0;j<listUser.length();j++){
                                JSONObject obj = listUser.getJSONObject(j);
                                Friend friend = new Friend();
                                if (!bundle.getString(Const.USERNAME).equals(obj.getString(Const.USERNAME))){
                                    friend.setUsername(obj.getString(Const.USERNAME));
                                    friend.setDisplayName(obj.getString(Const.DISPLAY_NAME));
                                    friend.setGender(obj.getInt(Const.GENDER));
                                    friends.add(friend);
                                }
                            }
                                    item.setNameConservation(object.getString(Const.NAME_CONVERSATION));
//                            else
//                                item.setNameConservation(object.getString(obj.getString(Const.USERNAME));
                            item.setListFriends(friends);
                            data.add(item);
                        }
                        Log.d(Const.TAG,"leghtData = "+String.valueOf(data.size()));
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

        requestQueue.add(objectRequest);
        progressDialog.dismiss();
    }

    public AdapterView.OnItemClickListener itemLvConversationCkick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            intent = new Intent(getActivity(), ChatActivity.class);
            ItemListviewConversation item = data.get(position);
            bundle.putInt(Const.CONVERSATION_ID, item.getId());
            if (item.getListFriends().size() > 1){
                bundle.putString(Const.NAME_CONVERSATION,item.getNameConservation());
            }else
                bundle.putString(Const.NAME_CONVERSATION,item.getListFriends().get(0).getDisplayName());
            intent.putExtra(Const.PACKAGE,bundle);
            startActivity(intent);
        }
    };


}
