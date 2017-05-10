package com.example.nam.minisn.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nam.minisn.Fragmen.FragmentStatus;
import com.example.nam.minisn.ItemListview.Status;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SQLiteDataController;
import com.example.nam.minisn.Util.SharedPrefManager;

import java.util.ArrayList;

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
        holder.nameFriend.setText(temp.getFriend().getDisplayName());
        holder.timeStatus.setText(Const.getStringTime(context,temp.getTime()));
        holder.status.setText(temp.getStatus());
        holder.countComment.setText(String.valueOf(temp.getCountComment()));
        holder.countLike.setText(String.valueOf(temp.getCountLike()));
        if (temp.isLike()){
            holder.imgLike.setImageResource(R.drawable.like);
        }
        else
            holder.imgLike.setImageResource(R.drawable.unlike);
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean like = FragmentStatus.getData().get(position).isLike();
                FragmentStatus.getData().get(position).setLike(!like);
                FragmentStatus.getAdapter().notifyDataSetChanged();
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
