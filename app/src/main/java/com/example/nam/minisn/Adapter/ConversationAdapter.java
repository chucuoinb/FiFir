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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nam.minisn.Fragmen.FragmentConversation;
import com.example.nam.minisn.ItemListview.Conversation;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SQLiteDataController;
import com.example.nam.minisn.Util.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nam on 2/21/2017.
 */

public class ConversationAdapter extends ArrayAdapter<Conversation> {
    private Context context;
    private int layout;
    private ArrayList<Conversation> data = null;
    private SQLiteDataController database;
    private int useId;

    public ConversationAdapter(Context context, int layout, ArrayList<Conversation> data) {
        super(context, layout, data);
        this.context = context;
        this.layout = layout;
        this.data = data;
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
            holder.nameConversation = (TextView) row.findViewById(R.id.lv_conversation_nameConversation);
            holder.lastMessage = (TextView) row.findViewById(R.id.lv_conversation_lastmessage);
            holder.iconNew = (ImageView) row.findViewById(R.id.lv_conversation_new);
            holder.time = (TextView) row.findViewById(R.id.lv_conversation_time);
            holder.avatar = (CircleImageView) row.findViewById(R.id.lv_conversation_avata);
            holder.check = (CheckBox) row.findViewById(R.id.lv_conversation_check);
            row.setTag(holder);
        } else
            holder = (Holder) row.getTag();
        Conversation temp = data.get(position);
        if (temp.isShowCheckBox()) {
            holder.check.setVisibility(View.VISIBLE);
            if (temp.getTypeChoose() == Const.TYPE_CHOOSE)
                holder.check.setChecked(true);
            else
                holder.check.setChecked(false);
        } else {
            holder.check.setVisibility(View.INVISIBLE);
        }
        if (!FragmentConversation.isSearch || FragmentConversation.search.length() == 0)
            holder.nameConversation.setText(temp.getNameConservation());
        else {
            String name = temp.getNameConservation();
            int start = name.indexOf(FragmentConversation.search);
            int end = start + FragmentConversation.search.length();
            Spannable spannable = new SpannableString(name);

            spannable.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.nameConversation.setText(spannable, TextView.BufferType.SPANNABLE);
        }
        if (temp.getLastMessage().length() <= 15)
            holder.lastMessage.setText(temp.getLastMessage());
        else {
            String subLastMessage = temp.getLastMessage().substring(0,15) + " ...";
            holder.lastMessage.setText(subLastMessage);
        }
        if (temp.getTime() > 0)
            holder.time.setText(Const.getStringTime(context, temp.getTime()));
        else
            holder.time.setText("");
        if (temp.isNew())
            holder.iconNew.setVisibility(View.VISIBLE);
        else
            holder.iconNew.setVisibility(View.INVISIBLE);
        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int choose = (FragmentConversation.data.get(position).getTypeChoose() == Const.TYPE_CHOOSE) ?
                        Const.TYPE_NO_CHOOSE : Const.TYPE_CHOOSE;
                FragmentConversation.data.get(position).setTypeChoose(choose);
                database.updateChooseConversation(useId, FragmentConversation.data.get(position).getId(), choose);
                FragmentConversation.tvCount.setText(String.valueOf(database.getCountChooseConversation()));
                int countChoose = 0;
                for (int i = 0; i < FragmentConversation.data.size(); i++) {
                    if (FragmentConversation.data.get(i).getTypeChoose() == Const.TYPE_CHOOSE)
                        countChoose++;
                }
                if (countChoose == FragmentConversation.data.size())
                    FragmentConversation.getCheckAll().setChecked(true);
                else
                    FragmentConversation.getCheckAll().setChecked(false);
            }
        });
        return row;
    }


    public static class Holder {
        CircleImageView avatar;
        ImageView iconNew;
        TextView nameConversation;
        TextView lastMessage;
        TextView time;
        CheckBox check;
    }

}
