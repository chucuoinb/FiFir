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
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nam.minisn.Activity.ChatActivity;
import com.example.nam.minisn.Adapter.ListviewConversationAdapter;
import com.example.nam.minisn.ItemListview.Friend;
import com.example.nam.minisn.ItemListview.Conversation;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SQLiteDataController;
import com.example.nam.minisn.Util.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    private Bundle bundle;
    private ListviewConversationAdapter adapter;
    private ProgressDialog progressDialog;
    private Intent intent;
    private String token;
    private int use_id;

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
        progressDialog.setIndeterminate(true);
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
        lvConversation = (ListView) rootView.findViewById(R.id.tab_Conversation_lv);

        adapter = new ListviewConversationAdapter(getActivity(), R.layout.item_lvconversation, data);
        lvConversation.setAdapter(adapter);
        getListConversation();
        lvConversation.setOnItemClickListener(itemLvConversationCkick);
    }

    public void getListConversation() {
        SQLiteDataController db = new SQLiteDataController(getActivity());
//            db.isCreatedDatabase();
            db.openDataBase();
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
                    Const.CONVERSATION_COL4+
                    Const.DESC;
            Cursor cursor = db.getDatabase().rawQuery(sql, null);
            while (cursor.moveToNext()) {
                int idConversation = cursor.getInt(cursor.getColumnIndex(Const.CONVERSATION_COL2));
                String nameConversation = cursor.getString(cursor.getColumnIndex(Const.CONVERSATION_COL1));
                String lastMessage = cursor.getString(3);
                String time = new String();
                if (cursor.getLong(4) > 0)
                    time = getStringTime(cursor.getLong(4));
                Log.d(Const.TAG, "time: " + time);
                boolean isNew = cursor.getInt(cursor.getColumnIndex(Const.CONVERSATION_COL6)) == 1;
                Conversation conversation = new Conversation(idConversation, nameConversation, lastMessage, time, isNew);
//                conversation.setNew(isNew);
                data.add(conversation);
        }
            db.getDatabase().close();
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
    }

    public AdapterView.OnItemClickListener itemLvConversationCkick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            intent = new Intent(getActivity(), ChatActivity.class);
            Conversation item = data.get(position);
            bundle.putInt(Const.CONVERSATION_ID, item.getId());
            bundle.putString(Const.NAME_CONVERSATION, item.getNameConservation());
            intent.putExtra(Const.PACKAGE, bundle);
            startActivity(intent);
        }
    };

    public String getStringTime(long time) {
//        String result = new String();
        long nowTime = System.currentTimeMillis()/1000;
        long temp = nowTime - time;
        if (temp < 60)
            return getResources().getString(R.string.now_time);
        else if (temp >= 60 && temp < 60 * 60)
            return String.valueOf(temp / 60) + " "+getResources().getString(R.string.time2);
        else if (temp >= 60 * 60 && temp < 60 * 60 * 24)
            return String.valueOf(temp / (60 * 60)) + " "+getResources().getString(R.string.time3);
        else if (temp >= 60 * 60 * 24 && temp < 60 * 60 * 24 * 7)
            return String.valueOf(temp / (60 * 60 * 24)) +" "+ getResources().getString(R.string.time4);
        else {
            String pattern = "dd/MM/yyyy";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.format(new Date(time*1000));
        }

    }

}
