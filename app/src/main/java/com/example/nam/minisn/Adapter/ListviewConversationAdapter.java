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

import com.example.nam.minisn.Fragmen.FragmenConversation;
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

public class ListviewConversationAdapter extends ArrayAdapter<Conversation> {
    private Context context;
    private int layout;
    private ArrayList<Conversation> data = null;
    private SQLiteDataController database;
    private int useId;

    public ListviewConversationAdapter(Context context, int layout, ArrayList<Conversation> data) {
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
        if (!FragmenConversation.isSearch || FragmenConversation.search.length() == 0)
            holder.nameConversation.setText(temp.getNameConservation());
        else {
            String name = temp.getNameConservation();
            int start = name.indexOf(FragmenConversation.search);
            int end = start + FragmenConversation.search.length();
            Spannable spannable = new SpannableString(name);

            spannable.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.nameConversation.setText(spannable, TextView.BufferType.SPANNABLE);
        }
        holder.lastMessage.setText(temp.getLastMessage());
        if (temp.getTime() > 0)
            holder.time.setText(getStringTime(temp.getTime()));
        else
            holder.time.setText("");
        if (temp.isNew())
            holder.iconNew.setVisibility(View.VISIBLE);
        else
            holder.iconNew.setVisibility(View.INVISIBLE);
        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int choose = (FragmenConversation.data.get(position).getTypeChoose()==Const.TYPE_CHOOSE)?
                            Const.TYPE_NO_CHOOSE :Const.TYPE_CHOOSE;
                FragmenConversation.data.get(position).setTypeChoose(choose);
                database.updateChooseConversation(useId,FragmenConversation.data.get(position).getId(),choose);
                FragmenConversation.tvCount.setText(String.valueOf(database.getCountChooseConversation()));
                int countChoose = 0;
                for(int i = 0;i<FragmenConversation.data.size();i++){
                    if (FragmenConversation.data.get(i).getTypeChoose() == Const.TYPE_CHOOSE)
                        countChoose++;
                }
                if (countChoose == FragmenConversation.data.size())
                    FragmenConversation.getCheckAll().setChecked(true);
                else
                    FragmenConversation.getCheckAll().setChecked(false);
            }
        });
        return row;
    }

    public String getStringTime(long time) {
//        String result = new String();
        long nowTime = System.currentTimeMillis() / 1000;
        long temp = nowTime - time;
        if (temp < 60)
            return context.getResources().getString(R.string.now_time);
        else if (temp >= 60 && temp < 60 * 60)
            return String.valueOf(temp / 60) + " " + context.getResources().getString(R.string.time2);
        else if (temp >= 60 * 60 && temp < 60 * 60 * 24)
            return String.valueOf(temp / (60 * 60)) + " " + context.getResources().getString(R.string.time3);
        else if (temp >= 60 * 60 * 24 && temp < 60 * 60 * 24 * 7)
            return String.valueOf(temp / (60 * 60 * 24)) + " " + context.getResources().getString(R.string.time4);
        else {
            String pattern = "dd/MM/yyyy";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.format(new Date(time * 1000));
        }

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
