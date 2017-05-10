package com.example.nam.minisn.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.nam.minisn.Activity.SearchFriendActivity;
import com.example.nam.minisn.ItemListview.Friend;
import com.example.nam.minisn.ItemListview.SearchFriendItem;
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
 * Created by Nam on 5/9/2017.
 */

public class SearchFriendAdapter extends ArrayAdapter<SearchFriendItem> {
    private Activity context;
    private int layout;
    private ArrayList<SearchFriendItem> data = new ArrayList<>();
    private String token;
    private SQLiteDataController database;
    private int useId;
    public SearchFriendAdapter(Activity context, int layout, ArrayList<SearchFriendItem> data) {
        super(context, layout, data);
        this.context = context;
        this.layout = layout;
        this.data = data;
        useId = SharedPrefManager.getInstance(context).getInt(Const.ID);
        token = SharedPrefManager.getInstance(context).getString(Const.TOKEN);
        database = new SQLiteDataController(context);
        database.openDataBase();
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder = null;
        if (row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layout,parent,false);
            holder = new Holder();
            holder.nameFriend = (TextView)row.findViewById(R.id.search_name_friend);
            holder.status = (TextView)row.findViewById(R.id.search_bt_request);
            row.setTag(holder);
        }else
            holder = (Holder) row.getTag();

        final SearchFriendItem item = data.get(position);
//        holder.nameFriend.setText();
        String name = item.getFriend().getUsername();
        int start = name.indexOf(SearchFriendActivity.getTextSearch());
        int end = start + SearchFriendActivity.getTextSearch().length();
        Spannable spannable = new SpannableString(name);

        spannable.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.nameFriend.setText(spannable, TextView.BufferType.SPANNABLE);
        if (item.isRequest()){
            holder.status.setBackgroundResource(R.drawable.background_requested);
            holder.status.setText(context.getResources().getString(R.string.friend_requested));
            holder.status.setEnabled(false);
        }
        else {
            holder.status.setBackgroundResource(R.drawable.background_request);
            holder.status.setText(context.getResources().getString(R.string.friend_request));
            holder.status.setEnabled(true);
        }
        holder.status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> params = new HashMap<String, String>();
                params.put(Const.TOKEN,token);
                params.put(Const.ID_RECEIVE,String.valueOf(item.getFriend().getId()));
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Const.URL_SEND_REQUEST_FRIENS, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getInt(Const.CODE) != Const.CODE_OK) {
                                        Toast.makeText(context,"Có lỗi xảy ra, vui lòng thử lại",Toast.LENGTH_SHORT).show();
                                    } else {
                                        database.addWaitResponse(useId,data.get(position).getFriend().getId(),data.get(position).getFriend().getUsername());
                                        SearchFriendActivity.getData().get(position).setRequest(true);
                                        SearchFriendActivity.getAdapter().notifyDataSetChanged();
                                    }

                                } catch (JSONException e) {
                                    Log.d(Const.TAG,"json err");
                                    Toast.makeText(context,"Có lỗi xảy ra, vui lòng thử lại",Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(Const.TAG,"volley err");
                                Toast.makeText(context,"Có lỗi xảy ra, vui lòng thử lại",Toast.LENGTH_SHORT).show();
                            }
                        });


                jsObjRequest.setShouldCache(false);
                requestQueue.add(jsObjRequest);
            }
        });
        return row;
    }

    public static class Holder{
        TextView nameFriend;
        TextView status;
    }
}
