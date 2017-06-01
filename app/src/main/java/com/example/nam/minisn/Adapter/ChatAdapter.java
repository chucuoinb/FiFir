package com.example.nam.minisn.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nam.minisn.Activity.PersonalActivity;
import com.example.nam.minisn.ItemListview.Chat;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nam on 2/27/2017.
 */

public class ChatAdapter extends ArrayAdapter<Chat> {
    private Context context;
    private int layout;
    private ArrayList<Chat> data = new ArrayList<>();

    public ChatAdapter(Context context, int layout, ArrayList<Chat> data) {
        super(context, layout, data);
        this.context = context;
        this.layout = layout;
        this.data = data;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getTypeMessage();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        int type = getItemViewType(position);
        final Chat item = data.get(position);
        Holder holder = null;
        if (row ==null){
            holder =new Holder();
            if (type == Const.MESSAGE_SEND){
                row=LayoutInflater.from(getContext()).inflate(R.layout.item_lvchat_children_mychat, null);
                holder.textChat = (TextView)row.findViewById(R.id.lvChat_my_tvChat);
                holder.avatar = (CircleImageView)row.findViewById(R.id.lvChat_my_Ava);
            }else{
                row = LayoutInflater.from(getContext()).inflate(R.layout.item_lvchat_children_friendchat, null);
                holder.textChat = (TextView)row.findViewById(R.id.lvChat_fr_tvChat);
                holder.avatar = (CircleImageView)row.findViewById(R.id.lvChat_fr_Ava);
            }

        }
        else
            holder = (Holder)row.getTag();
        int a = (item.getGender() == Const.GENDER_UNKNOW)?R.drawable.people_24:
                        ((item.getGender() == Const.GENDER_MAN)?R.drawable.woman:R.drawable.man);
        holder.textChat.setText(item.getMessage());
        holder.avatar.setImageResource(a);
//        Log.d(Const.TAG,String.valueOf(position));
        if(position==0 || getItemViewType(position) != getItemViewType(position-1))
            holder.avatar.setVisibility(View.VISIBLE);
        else
            holder.avatar.setVisibility(View.INVISIBLE);
        row.setTag(holder);
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PersonalActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(Const.ID,item.getId());
                intent.putExtra(Const.PACKAGE,bundle);
                context.startActivity(intent);
            }
        });
        return row;
    }

    public static class Holder{
        TextView textChat;
        CircleImageView avatar;
    }
}
