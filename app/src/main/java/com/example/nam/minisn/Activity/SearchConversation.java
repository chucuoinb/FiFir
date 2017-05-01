package com.example.nam.minisn.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.nam.minisn.Adapter.ListviewConversationAdapter;
import com.example.nam.minisn.ItemListview.Conversation;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SQLiteDataController;
import com.example.nam.minisn.Util.SharedPrefManager;

import java.util.ArrayList;

public class SearchConversation extends AppCompatActivity {
    private EditText inputSearch;
    private ImageView btnBack;
    private ListView lv;
    private ArrayList<Conversation> data = new ArrayList<>();
    private ListviewConversationAdapter adapter;
    private SQLiteDataController database;
    private int useId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_search_conversation);
        init();
    }

    public void init(){
        useId = SharedPrefManager.getInstance(getApplicationContext()).getInt(Const.ID);
        database = new SQLiteDataController(getApplicationContext());
        inputSearch = (EditText)findViewById(R.id.search_conversation_input);
        btnBack = (ImageView)findViewById(R.id.chat_btn_back);
        adapter = new ListviewConversationAdapter(SearchConversation.this,R.layout.item_lvconversation,data);

        lv = (ListView)findViewById(R.id.search_conversation_lv);
        lv.setAdapter(adapter);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        inputSearch.addTextChangedListener(changeInput);
        data.clear();
        database.openDataBase();
        data.addAll(database.searchConversation(String.valueOf(""),useId));
        database.close();
        lv.setOnItemClickListener(chat);
    }

    public TextWatcher changeInput = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            database.openDataBase();
            String name = inputSearch.getText().toString();
            data.clear();
            data.addAll(database.searchConversation(name,useId));
            adapter.notifyDataSetChanged();
            database.close();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public AdapterView.OnItemClickListener chat = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(SearchConversation.this, ChatActivity.class);
            Conversation item = data.get(position);
            if (item.isNew()) {
                database.openDataBase();
                database.setNewMessageConversation(item.getId(), useId);
                database.close();
                item.setNew(false);
//                adapter.notifyDataSetChanged();
            }
            Bundle bundle = new Bundle();
            bundle.putInt(Const.CONVERSATION_ID, item.getId());
            bundle.putString(Const.NAME_CONVERSATION, item.getNameConservation());
            intent.putExtra(Const.PACKAGE, bundle);
            startActivity(intent);
        }
    };
}
