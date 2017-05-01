package com.example.nam.minisn.Fragmen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nam.minisn.Activity.ChatActivity;
import com.example.nam.minisn.Activity.Main;
import com.example.nam.minisn.Adapter.ListviewConversationAdapter;
import com.example.nam.minisn.ItemListview.Conversation;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SQLiteDataController;
import com.example.nam.minisn.Util.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Nam on 2/21/2017.
 */

public class FragmenConversation extends Fragment {
    private View rootView;
    private ListView lvConversation;
    private ArrayList<Conversation> data = new ArrayList<>();
    private ArrayList<Conversation> dataSingle = new ArrayList<>();
    private ArrayList<Conversation> dataGroup = new ArrayList<>();
    private Bundle bundle;
    private ListviewConversationAdapter adapter;
    private ProgressDialog progressDialog;
    private Intent intent;
    private String token;
    private int use_id;
    private SQLiteDataController database;
    private TextView single, group;
    private ImageView singleNew, groupNew;

    public FragmenConversation() {

    }

    public static FragmenConversation newInstance(Bundle bundle) {
        FragmenConversation fragmen = new FragmenConversation();

        fragmen.setArguments(bundle);
        return fragmen;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dialog);
//        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        rootView = inflater.inflate(R.layout.layout_tab_conversation, container, false);
        bundle = getArguments();
//        data = (ArrayList<Conversation>) bundle.getSerializable(Const.DB_CONVERSATION);
        use_id = SharedPrefManager.getInstance(getActivity()).getInt(Const.ID);
        token = SharedPrefManager.getInstance(getActivity()).getString(Const.TOKEN);
        init();
        return rootView;
    }

    public void init() {
        singleNew = (ImageView) rootView.findViewById(R.id.single_new);
        groupNew = (ImageView) rootView.findViewById(R.id.group_new);
        single = (TextView) rootView.findViewById(R.id.lv_conversation_single);
        group = (TextView) rootView.findViewById(R.id.lv_conversation_group);
        single.setOnClickListener(singleClick);
        group.setOnClickListener(groupClick);
        database = new SQLiteDataController(getActivity());
        lvConversation = (ListView) rootView.findViewById(R.id.tab_Conversation_lv);

        adapter = new ListviewConversationAdapter(getActivity(), R.layout.item_lvconversation, data);
        lvConversation.setAdapter(adapter);
        lvConversation.setOnItemClickListener(itemLvConversationCkick);

    }

    @Override
    public void onResume() {
        super.onResume();
        setConversationSelect(Const.CONVERSATION_TYPE_SINGLE);
        data.clear();
        dataSingle.clear();
        dataGroup.clear();
        getListConversation();
        adapter.notifyDataSetChanged();
    }

    public void getListConversation() {
//        SQLiteDataController db = new SQLiteDataController(getActivity());
//            database.isCreatedDatabase();
        database.openDataBase();
        String sql = Const.SELECT +
                " * " +
                Const.FROM +
                Const.DB_CONVERSATION +
                Const.WHERE +
                Const.CONVERSATION_COL5 +
                "= '" +
                use_id +
                "'" +
                Const.ORDER_BY +
                Const.CONVERSATION_COL4 +
                Const.DESC;
        Cursor cursor = database.getDatabase().rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int idConversation = cursor.getInt(cursor.getColumnIndex(Const.CONVERSATION_COL2));
            String nameConversation = cursor.getString(cursor.getColumnIndex(Const.CONVERSATION_COL1));
            String lastMessage = cursor.getString(3);
            String time = new String();
            if (cursor.getLong(4) > 0)
                time = getStringTime(cursor.getLong(4));
            boolean isNew = cursor.getInt(cursor.getColumnIndex(Const.CONVERSATION_COL6)) == 1;
            Conversation conversation = new Conversation(idConversation, nameConversation, lastMessage, time, isNew);
            int size = cursor.getInt(7);
            if (size > 2) {
                if (conversation.isNew())
                    groupNew.setVisibility(View.VISIBLE);
                else
                    groupNew.setVisibility(View.INVISIBLE);
                dataGroup.add(conversation);
            } else {
                if (conversation.isNew())
                    single.setVisibility(View.VISIBLE);
                else
                    singleNew.setVisibility(View.INVISIBLE);
                dataSingle.add(conversation);
            }
        }
        database.close();
//        data = new ArrayList<>(dataSingle);
        data.addAll(dataSingle);
        adapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }

    public AdapterView.OnItemClickListener itemLvConversationCkick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            intent = new Intent(getActivity(), ChatActivity.class);
            Conversation item = data.get(position);
            if (item.isNew()) {
                database.openDataBase();
                database.setNewMessageConversation(item.getId(), use_id);
                database.close();
                item.setNew(false);
//                adapter.notifyDataSetChanged();
            }
            bundle.putInt(Const.CONVERSATION_ID, item.getId());
            bundle.putString(Const.NAME_CONVERSATION, item.getNameConservation());
            intent.putExtra(Const.PACKAGE, bundle);
            startActivity(intent);
        }
    };

    public String getStringTime(long time) {
//        String result = new String();
        long nowTime = System.currentTimeMillis() / 1000;
        long temp = nowTime - time;
        if (temp < 60)
            return getResources().getString(R.string.now_time);
        else if (temp >= 60 && temp < 60 * 60)
            return String.valueOf(temp / 60) + " " + getResources().getString(R.string.time2);
        else if (temp >= 60 * 60 && temp < 60 * 60 * 24)
            return String.valueOf(temp / (60 * 60)) + " " + getResources().getString(R.string.time3);
        else if (temp >= 60 * 60 * 24 && temp < 60 * 60 * 24 * 7)
            return String.valueOf(temp / (60 * 60 * 24)) + " " + getResources().getString(R.string.time4);
        else {
            String pattern = "dd/MM/yyyy";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.format(new Date(time * 1000));
        }

    }

    public View.OnClickListener singleClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setConversationSelect(Const.CONVERSATION_TYPE_SINGLE);
            data.clear();
            data.addAll(dataSingle);
            adapter.notifyDataSetChanged();
        }
    };
    public View.OnClickListener groupClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setConversationSelect(Const.CONVERSATION_TYPE_GROUP);
            data.clear();
            data.addAll(dataGroup);
            adapter.notifyDataSetChanged();
        }
    };

    public void setConversationSelect(int choose) {
        if (choose == Const.CONVERSATION_TYPE_SINGLE) {
            single.setTextColor(getResources().getColor(R.color.colorTextChoose));
            group.setTextColor(getResources().getColor(R.color.colorText));
        } else {
            group.setTextColor(getResources().getColor(R.color.colorTextChoose));
            single.setTextColor(getResources().getColor(R.color.colorText));
        }
    }
}
