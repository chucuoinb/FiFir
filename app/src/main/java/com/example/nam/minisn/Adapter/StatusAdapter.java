package com.example.nam.minisn.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.example.nam.minisn.Fragmen.FragmentStatus;
import com.example.nam.minisn.ItemListview.Status;
import com.example.nam.minisn.R;
import com.example.nam.minisn.UseVoley.CustomRequest;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SQLiteDataController;
import com.example.nam.minisn.Util.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

/**
 * Created by Nam on 5/10/2017.
 */

public class StatusAdapter extends ArrayAdapter<Status>{
    private Activity context;
    private ArrayList<Status> data;
    private int layout;
    private int useId;
    private String token;
    private SQLiteDataController database;
    public StatusAdapter(Activity context,int layout,ArrayList<Status> data){
        super(context,layout,data);
        this.context=context;
        this.data=data;
        this.layout=layout;
        database = new SQLiteDataController(context);
        database.openDataBase();
        token= SharedPrefManager.getInstance(context).getString(Const.TOKEN);
        useId=SharedPrefManager.getInstance(context).getInt(Const.ID);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        Holder holder = null;
        if (row == null){
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(layout,parent,false);
            holder = new Holder();
            holder.nameFriend = (TextView)row.findViewById(R.id.status_name_friend);
            holder.timeStatus =(TextView)row.findViewById(R.id.status_time);
            holder.status = (TextView)row.findViewById(R.id.status_status);
            holder.countComment = (TextView)row.findViewById(R.id.status_count_cmt);
            holder.countLike = (TextView)row.findViewById(R.id.status_count_like);
            holder.imgLike = (ImageView)row.findViewById(R.id.status_img_like);
            holder.like = (LinearLayout)row.findViewById(R.id.status_like);
            holder.comment = (LinearLayout)row.findViewById(R.id.status_comment);
            row.setTag(holder);
        }
        else
            holder = (Holder)row.getTag();

        Status temp = data.get(position);
//        Log.d(Const.TAG,temp.getFriend().getDisplayName());
        holder.nameFriend.setText(temp.getFriend().getDisplayName());
        holder.timeStatus.setText(Const.getStringTime(context,temp.getTime()));
        holder.status.setText(temp.getStatus());
        holder.countComment.setText(String.valueOf(temp.getCountComment()));
        holder.countLike.setText(String.valueOf(temp.getCountLike()));
        if (temp.getTypeLike() == Const.STA_LIKE){
            holder.imgLike.setImageResource(R.drawable.like);
        }
        else
            holder.imgLike.setImageResource(R.drawable.unlike);
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> params = new HashMap<String, String>();
                params.put(Const.TOKEN,token);
                params.put(Const.STATUS_ID,String.valueOf(FragmentStatus.getData().get(position).getId()));
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                CustomRequest jsonRequest = new CustomRequest(Request.Method.POST, Const.URL_LIKE_STATUS, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.d(Const.TAG,response.getString(Const.MESSAGE));
                                    if (response.getInt(Const.CODE) == Const.CODE_FAIL)
                                        Toasty.error(context,response.getString(Const.MESSAGE), Toast.LENGTH_SHORT).show();
                                    else{

                                        if (response.getInt(Const.CODE) != Const.CODE_OK){
                                            Toasty.error(context, context.getResources().getString(R.string.notifi_error), Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            int typeLike = FragmentStatus.getData().get(position).getTypeLike()==Const.STA_LIKE?
                                                            Const.STA_UNLIKE:Const.STA_LIKE;
                                            Log.d(Const.TAG,"typelike: "+typeLike);
                                            FragmentStatus.getData().get(position).setTypeLike(typeLike);
                                            FragmentStatus.getAdapter().notifyDataSetChanged();
                                        }
                                    }
                                } catch (JSONException e) {
                                    Toasty.error(context, context.getResources().getString(R.string.notifi_error), Toast.LENGTH_SHORT).show();
                                    Log.d(Const.TAG,"like json err");
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(Const.TAG,"like vl err");
                                Toasty.error(context, context.getResources().getString(R.string.notifi_error), Toast.LENGTH_SHORT).show();

                            }
                        });
                jsonRequest.setShouldCache(false);
                requestQueue.add(jsonRequest);

            }
        });

        return row;
    }

    private class Holder{
        TextView nameFriend;
        TextView timeStatus;
        TextView status;
        TextView countComment;
        TextView countLike;
        ImageView imgLike;
        LinearLayout like;
        LinearLayout comment;
    }
}
