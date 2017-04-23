package com.example.nam.minisn.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nam.minisn.ItemListview.Friend;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;

import java.util.ArrayList;

/**
 * Created by Nam on 2/21/2017.
 */

public class ListviewRequestFriendAdapter extends ArrayAdapter<Friend> {
    private Activity context;
    private int layout;
    private ArrayList<Friend> data =null;

    public ListviewRequestFriendAdapter(Activity context, int layout, ArrayList<Friend> data) {
        super(context, layout, data);
        this.context = context;
        this.data = data;
        this.layout = layout;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder = null;
        if (row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layout,parent,false);
            holder = new Holder();
            holder.ava = (ImageView)row.findViewById(R.id.request_friend_avatar);
            holder.namFriend =(TextView)row.findViewById(R.id.request_friend_name);
            row.setTag(holder);
        }else
            holder = (Holder)row.getTag();

        Friend temp = data.get(position);
        int gender = temp.getGender();
        int ava = (gender == Const.GENDER_UNKNOW) ? R.drawable.people_24 :
                ((gender == Const.GENDER_MAN)? R.drawable.man_24:R.drawable.woman_24 );
        holder.ava.setImageResource(ava);
        holder.namFriend.setText(temp.getUsername());
        return row;
    }

    public static  class Holder{
        ImageView ava;
        TextView namFriend;
    }
}
