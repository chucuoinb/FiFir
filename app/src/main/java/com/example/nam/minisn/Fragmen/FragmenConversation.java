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
import java.util.ArrayList;

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
        Log.d(Const.TAG,"size:"+String.valueOf(data.size()));
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
        try {
            db.isCreatedDatabase();
            db.openDataBase();
            String sql = Const.SELECT +
                    Const.CONVERSATION_COL1 +
                    "," +
                    Const.CONVERSATION_COL2 +
                    "," +
                    Const.CONVERSATION_COL6 +
                    Const.FROM +
                    Const.DB_CONVERSATION +
                    Const.WHERE +
                    Const.CONVERSATION_COL5 +
                    "= '" +
                    use_id +
                    "'";
            Cursor cursor = db.getDatabase().rawQuery(sql, null);
            Log.d(Const.TAG, "conversatinon:"+String.valueOf(cursor.getCount()));
            while (cursor.moveToNext()) {
                int idConversation = cursor.getInt(cursor.getColumnIndex(Const.CONVERSATION_COL2));
                String nameConversation = cursor.getString(cursor.getColumnIndex(Const.CONVERSATION_COL1));
                boolean isNew = cursor.getInt(cursor.getColumnIndex(Const.CONVERSATION_COL6)) == 1;
                Conversation conversation = new Conversation(nameConversation, idConversation);
                conversation.setNew(isNew);
                data.add(conversation);
            }
            db.getDatabase().close();
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(Const.TAG, e.getMessage());
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
        }
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


}
