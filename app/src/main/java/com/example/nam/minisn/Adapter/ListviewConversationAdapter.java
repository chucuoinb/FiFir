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
import com.example.nam.minisn.ItemListview.Conversation;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nam on 2/21/2017.
 */

public class ListviewConversationAdapter extends ArrayAdapter<Conversation> {
    private Context context;
    private int layout;
    private ArrayList<Conversation> data = null;

    public ListviewConversationAdapter(Context context, int layout, ArrayList<Conversation> data) {
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
            holder.nameConversation = (TextView) row.findViewById(R.id.lv_conversation_nameConversation);
            holder.lastMessage = (TextView)row.findViewById(R.id.lv_conversation_lastmessage) ;
            holder.iconNew = (ImageView)row.findViewById(R.id.lv_conversation_new);
            holder.time = (TextView)row.findViewById(R.id.lv_conversation_time);
            holder.avatar =(CircleImageView)row.findViewById(R.id.lv_conversation_avata);
            row.setTag(holder);
        } else
            holder = (Holder) row.getTag();
        Conversation temp = data.get(position);
        holder.nameConversation.setText(temp.getNameConservation());
        if (temp.isNew())
            holder.iconNew.setVisibility(View.INVISIBLE);
        else
            holder.iconNew.setVisibility(View.INVISIBLE);
        return row;
    }

    public static class Holder {
        CircleImageView avatar;
        ImageView iconNew;
        TextView nameConversation;
        TextView lastMessage;
        TextView time;

    }
}
