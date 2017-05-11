package com.example.nam.minisn.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.nam.minisn.Activity.RequestFriendActivity;
import com.example.nam.minisn.ItemListview.Friend;
import com.example.nam.minisn.R;
import com.example.nam.minisn.UseVoley.CustomRequest;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SQLiteDataController;
import com.example.nam.minisn.Util.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nam on 2/21/2017.
 */

public class RequestFriendAdapter extends ArrayAdapter<Friend> {
    private Activity context;
    private int layout;
    private ArrayList<Friend> data = null;
    private String token;
    private int useId;
    private int positionItem;
    private SQLiteDataController database;

    public RequestFriendAdapter(Activity context, int layout, ArrayList<Friend> data) {
        super(context, layout, data);
        this.context = context;
        this.data = data;
        this.layout = layout;
        token = SharedPrefManager.getInstance(context).getString(Const.TOKEN);
        useId = SharedPrefManager.getInstance(context).getInt(Const.ID);
        database = new SQLiteDataController(context);
        database.openDataBase();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        positionItem = position;
        View row = convertView;
        Holder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layout, parent, false);
            holder = new Holder();
            holder.ava = (ImageView) row.findViewById(R.id.request_friend_avatar);
            holder.namFriend = (TextView) row.findViewById(R.id.request_friend_name);
            holder.btAccept = (LinearLayout) row.findViewById(R.id.request_friend_bt_accept);
            holder.btCancel = (LinearLayout) row.findViewById(R.id.request_friend_bt_cancel);
            row.setTag(holder);
        } else
            holder = (Holder) row.getTag();

        Friend temp = data.get(position);
        int gender = temp.getGender();
        holder.namFriend.setText(temp.getUsername());
        holder.btAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<>();
                params.put(Const.TOKEN, token);
                params.put(Const.ID_RECEIVE, String.valueOf(data.get(position).getId()));
                final int code = Const.CODE_ACCEPT;
                params.put(Const.CODE_RESPONSE, String.valueOf(code));
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Const.URL_SEND_RESPONSE_FRIEND, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getInt(Const.CODE) != Const.CODE_OK) {
                                        Toast.makeText(context, "Có lỗi xảy ra. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                                        Log.d(Const.TAG, "request friend: " + response.getString(Const.MESSAGE));
                                    } else {
                                        database.deleteRequestFriend(useId, data.get(position).getId());
                                        RequestFriendActivity.getFriends().remove(position);
                                        RequestFriendActivity.getAdapter().notifyDataSetChanged();
//                                    data.remove(positionItem);
                                        if (code == Const.CODE_ACCEPT) {
                                            JSONObject userFriend = response.getJSONObject(Const.DATA);
                                            int gender = userFriend.getInt(Const.GENDER);
                                            String username = userFriend.getString(Const.USERNAME);
                                            int fri_id = userFriend.getInt(Const.ID);
                                            int id = userFriend.getInt("id_friend");
                                            String displayName = userFriend.getString(Const.DISPLAY_NAME);
                                            if (!database.isExistFriend(useId, id))
                                                database.addFriend(useId, gender, username, fri_id, id,displayName);
                                        }
                                    }
                                } catch (JSONException e) {
                                    Log.d(Const.TAG, "json err:"+e.getMessage());
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(Const.TAG, "volley err");
                            }
                        });


                jsObjRequest.setShouldCache(false);
                requestQueue.add(jsObjRequest);
            }
        });
        holder.btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params = new HashMap<>();
                params.put(Const.TOKEN, token);
                params.put(Const.ID_RECEIVE, String.valueOf(data.get(position).getId()));
                final int code = Const.CODE_CANCEL;
                params.put(Const.CODE_RESPONSE, String.valueOf(code));
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Const.URL_SEND_RESPONSE_FRIEND, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getInt(Const.CODE) != Const.CODE_OK) {
                                        Toast.makeText(context, "Có lỗi xảy ra. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                                        Log.d(Const.TAG, "request friend: " + response.getString(Const.MESSAGE));
                                    } else {
                                        database.deleteRequestFriend(useId, data.get(position).getId());
                                        RequestFriendActivity.getFriends().remove(position);
                                        RequestFriendActivity.getAdapter().notifyDataSetChanged();
//                                    data.remove(positionItem);
                                    }
                                } catch (JSONException e) {
                                    Log.d(Const.TAG, "json err");
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(Const.TAG, "volley err");
                            }
                        });


                jsObjRequest.setShouldCache(false);
                requestQueue.add(jsObjRequest);
            }
    });
        return row;
}


public static class Holder {
    ImageView ava;
    TextView namFriend;
    LinearLayout btCancel;
    LinearLayout btAccept;
}


}
