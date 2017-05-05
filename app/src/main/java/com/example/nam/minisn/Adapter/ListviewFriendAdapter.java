package com.example.nam.minisn.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.nam.minisn.Fragmen.FragmenConversation;
import com.example.nam.minisn.Fragmen.FragmenFriend;
import com.example.nam.minisn.ItemListview.ItemDeleteFriend;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SQLiteDataController;
import com.example.nam.minisn.Util.SharedPrefManager;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nam on 2/21/2017.
 */

public class ListviewFriendAdapter extends ArrayAdapter<ItemDeleteFriend> {
    private Context context;
    private int layout;
    private ArrayList<ItemDeleteFriend> data;
    private SQLiteDataController database;
    private int useId;
    public ListviewFriendAdapter(Context context, int layout, ArrayList<ItemDeleteFriend> data) {
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
        if (row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layout,parent,false);
            holder = new Holder();
            holder.ava = (CircleImageView)row.findViewById(R.id.lv_friend_avata);
            holder.namFriend =(TextView)row.findViewById(R.id.lv_friend_nameFriend);
            holder.checkBox = (CheckBox)row.findViewById(R.id.friend_cb_delete);
            row.setTag(holder);
        }else
            holder = (Holder)row.getTag();

        ItemDeleteFriend temp = data.get(position);
        holder.namFriend.setText(temp.getFriend().getUsername());
        if (temp.isShowCheckBox()){
            holder.checkBox.setVisibility(View.VISIBLE);
            if(temp.getTpeChoose() == Const.TYPE_CHOOSE){
                holder.checkBox.setChecked(true);
            }
            else holder.checkBox.setChecked(false);
        }
        else
            holder.checkBox.setVisibility(View.INVISIBLE);
        if (!FragmenFriend.isSearch() || FragmenFriend.getTextSearch().length() == 0)
            holder.namFriend.setText(temp.getFriend().getUsername());
        else {
            String name = temp.getFriend().getUsername();
            int start = name.indexOf(FragmenFriend.getTextSearch());
            int end = start + FragmenFriend.getTextSearch().length();
            Spannable spannable = new SpannableString(name);

            spannable.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.namFriend.setText(spannable, TextView.BufferType.SPANNABLE);
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int choose = (FragmenFriend.getFriends().get(position).getTpeChoose()==Const.TYPE_CHOOSE)?
                        Const.TYPE_NO_CHOOSE :Const.TYPE_CHOOSE;
                FragmenFriend.getFriends().get(position).setTypeChoose(choose);
                database.updateChooseFriend(useId,FragmenFriend.getFriends().get(position).getFriend().getId(),choose);
                int count = 0;
                FragmenFriend.getTvCount().setText(String.valueOf(database.getCountChooseFriend()));
                for(int i = 0;i<FragmenFriend.getFriends().size();i++){
                    if (FragmenFriend.getFriends().get(i).getTpeChoose() == Const.TYPE_CHOOSE)
                        count++;
                }
                if (count == FragmenFriend.getFriends().size())
                    FragmenFriend.getCbAll().setChecked(true);
                else
                    FragmenFriend.getCbAll().setChecked(false);
            }
        });
        return row;
    }

    public static  class Holder{
        CircleImageView ava;
        TextView namFriend;
        CheckBox checkBox;
    }
}
