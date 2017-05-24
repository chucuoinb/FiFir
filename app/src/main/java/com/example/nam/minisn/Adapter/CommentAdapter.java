package com.example.nam.minisn.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.nam.minisn.ItemListview.Comment;
import com.example.nam.minisn.ItemListview.Status;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SQLiteDataController;

import java.util.ArrayList;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nam on 5/24/2017.
 */

public class CommentAdapter extends ArrayAdapter<Comment> {
    private Activity context;
    private ArrayList<Comment> data;
    private int layout;
    private int useId;
    private String token;

    public CommentAdapter(Activity context,int layout,ArrayList<Comment> data){
        super(context,layout,data);
        this.context = context;
        this.layout = layout;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        Holder holder = null;
        if (row == null){
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(layout,parent,false);
            holder = new Holder();
            holder.avatar = (CircleImageView)row.findViewById(R.id.item_cm_ava);
            holder.comment = (TextView)row.findViewById(R.id.item_cm_comment);
            holder.time = (TextView)row.findViewById(R.id.item_cm_time);
            holder.username = (TextView)row.findViewById(R.id.item_cm_username);
            row.setTag(holder);
        }
        else
            holder = (Holder)row.getTag();
        Comment comment = data.get(position);
        holder.comment.setText(comment.getComment());
        holder.time.setText(Const.getStringTime(context,comment.getTime()));
        holder.username.setText(comment.getUsername());
        return row;
    }

    private static class Holder{
        CircleImageView avatar;
        TextView comment;
        TextView time;
        TextView username;
    }
}
