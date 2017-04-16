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
import com.example.nam.minisn.ItemListview.ItemListviewConversation;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;

import java.util.ArrayList;

/**
 * Created by Nam on 2/21/2017.
 */

public class ListviewConversationAdapter extends ArrayAdapter<ItemListviewConversation> {
    private Context context;
    private int layout;
    private ArrayList<ItemListviewConversation> data = null;

    public ListviewConversationAdapter(Context context, int layout, ArrayList<ItemListviewConversation> data) {
        super(context, layout, data);
        this.context = context;
        this.layout = layout;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layout, parent, false);
            holder = new Holder();
            holder.avatar = (ImageView) row.findViewById(R.id.lv_conversation_avafriend);
            holder.nameConversation = (TextView) row.findViewById(R.id.lv_conversation_nameConversation);
            row.setTag(holder);
        } else
            holder = (Holder) row.getTag();
        ItemListviewConversation temp = data.get(position);
        int size;
        size = temp.getListFriends().size();
        holder.nameConversation.setText(temp.getNameConservation());
        if (size > 1) {
            holder.avatar.setImageResource(R.drawable.group_24);
        } else {
            Friend friend = temp.getListFriends().get(0);
            int gender = friend.getGender();
            int ava = (gender == Const.GENDER_UNKNOW) ? R.drawable.people_24 :
                    ((gender == Const.GENDER_MAN) ? R.drawable.man_24 : R.drawable.woman_24);
            holder.avatar.setImageResource(ava);
            if ("".equals(friend.getDisplayName()))
                friend.setDisplayName(friend.getUsername());
            holder.nameConversation.setText(friend.getDisplayName());
        }
        return row;
    }

    public static class Holder {
        ImageView avatar;
        TextView nameConversation;
    }
}
