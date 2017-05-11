package com.example.nam.minisn.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.nam.minisn.Activity.CreateConversation;
import com.example.nam.minisn.ItemListview.Friend;
import com.example.nam.minisn.ItemListview.ItemDeleteFriend;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SQLiteDataController;
import com.example.nam.minisn.Util.SharedPrefManager;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nam on 5/11/2017.
 */

public class CreateConversationAdapter extends ArrayAdapter<ItemDeleteFriend> {
    private Context context;
    private int layout;
    private ArrayList<ItemDeleteFriend> data;
    private SQLiteDataController database;
    private int useId;

    public CreateConversationAdapter(Context context, int layout, ArrayList<ItemDeleteFriend> data) {
        super(context, layout, data);
        this.context = context;
        this.data = data;
        this.layout = layout;
        database = new SQLiteDataController(context);
        database.openDataBase();
        useId = SharedPrefManager.getInstance(context).getInt(Const.ID);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layout, parent, false);
            holder = new Holder();
            holder.namFriend = (TextView) row.findViewById(R.id.lv_friend_nameFriend);
            holder.checkBox = (CheckBox) row.findViewById(R.id.friend_cb_delete);
            row.setTag(holder);
        } else
            holder = (Holder) row.getTag();

        ItemDeleteFriend temp = data.get(position);
        if (temp.getTpeChoose() == Const.TYPE_CHOOSE) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
        String name = temp.getFriend().getUsername();
        holder.namFriend.setText(name);
        int start = name.indexOf(CreateConversation.getTextSearch());
        int end = start + CreateConversation.getTextSearch().length();
        Spannable spannable = new SpannableString(name);
        spannable.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.namFriend.setText(spannable, TextView.BufferType.SPANNABLE);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int choose = (CreateConversation.getData().get(position).getTpeChoose() == Const.TYPE_CHOOSE) ?
                        Const.TYPE_NO_CHOOSE : Const.TYPE_CHOOSE;
                CreateConversation.getData().get(position).setTypeChoose(choose);
                database.updateChooseFriend(useId, CreateConversation.getData().get(position).getFriend().getId(), choose);
                Friend friend = database.getFriend(CreateConversation.getData().get(position).getFriend().getId());
                String listFriend = CreateConversation.getListFriend().getText().toString();
                ArrayList<Friend> friends = database.getListFriendAdd(useId);
                String text = new String();
                if (friends.size() > 0) {

                    for (int i = 0; i < friends.size(); i++) {
                        text += " " + friends.get(i).getUsername();
                    }
                }
                else text = context.getResources().getString(R.string.no_choose);
                    CreateConversation.getListFriend().setText(text);
            }
        });
        return row;
    }

    public static class Holder {
        TextView namFriend;
        CheckBox checkBox;
    }
}
