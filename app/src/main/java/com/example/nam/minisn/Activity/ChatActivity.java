package com.example.nam.minisn.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.nam.minisn.Adapter.ListviewChatAdapter;
import com.example.nam.minisn.ItemListview.Chat;
import com.example.nam.minisn.R;
import com.example.nam.minisn.UseVoley.CustomRequest;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SQLiteDataController;
import com.example.nam.minisn.Util.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    private ListView lvChat;
    private ListviewChatAdapter adapter;
    private ArrayList<Chat> data = new ArrayList<>();
    private Intent intent;
    private Bundle bundle;
    private Button btSend;
    private String token;
    private String nameConversation;
    private TextView tvNameConversation;
    private LinearLayout btnBack;
    private EditText edInputMessage;
    private String message;
    private int idConversation;
    private int useId;
    private BroadcastReceiver receiverMessage;
    private SQLiteDataController database;
    private boolean isLoad = true;
    private LinearLayout headerLvChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chat);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        SharedPrefManager.getInstance(getApplicationContext()).savePreferences(Const.CONVERSATION_ID, idConversation);
    }

    @Override
    protected void onStop() {
        super.onStop();
        database.close();
        SharedPrefManager.getInstance(getApplicationContext()).savePreferences(Const.CONVERSATION_ID, 0);
    }


    public void init() {
        headerLvChat = (LinearLayout) findViewById(R.id.header_lv_chat);
        tvNameConversation = (TextView) findViewById(R.id.chat_tv_nameFriend);
        edInputMessage = (EditText) findViewById(R.id.chat_ed_inputMessage);
        btSend = (Button) findViewById(R.id.chat_bt_Send);
        lvChat = (ListView) findViewById(R.id.chat_lv_Chat);
        btnBack = (LinearLayout) findViewById(R.id.chat_btn_back);

        adapter = new ListviewChatAdapter(ChatActivity.this, R.id.layoutChat, data);
        startDatabase();
        setInfoConversation();
        setListener();
        getListData();
    }

    public void startDatabase() {
        database = new SQLiteDataController(getApplicationContext());
        database.openDataBase();
    }

    public void getListData() {
        data.clear();
        data.addAll(database.getDataConversation(idConversation, useId, 0));
    }

    public void setListener() {
        lvChat.setAdapter(adapter);
        btSend.setOnClickListener(btSendClick);
        edInputMessage.addTextChangedListener(changeMessage);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        receiverMessage = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String newMessage = intent.getExtras().getString(Const.MESSAGE);

                data.add(new Chat(Const.MESSAGE_RECEIVE, newMessage, Const.GENDER_WOMAN));
                adapter.notifyDataSetChanged();
            }
        };
        registerReceiver(receiverMessage, new IntentFilter(Const.DISPLAY_MESSAGE_ACTION));
        lvChat.setOnScrollListener(lvScroll);


    }

    public AbsListView.OnScrollListener lvScroll = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (isLoad) {
                if (lvChat.getFirstVisiblePosition() == 0) {
                    isLoad = false;
                    headerLvChat.setVisibility(View.VISIBLE);
                    ArrayList<Chat> newData = new ArrayList<>();
                    int size = data.size();
                    newData = database.getDataConversation(idConversation, useId, size);
                    data.addAll(0, newData);
                    if (newData.size() != 0) {
                        adapter.notifyDataSetChanged();
                        lvChat.setSelection(newData.size());

                    }
                    headerLvChat.setVisibility(View.INVISIBLE);
                    isLoad = true;
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    };

    public void setInfoConversation() {
        useId = SharedPrefManager.getInstance(getApplicationContext()).getInt(Const.ID);
        intent = getIntent();
        bundle = intent.getBundleExtra(Const.PACKAGE);
        String newMessage = bundle.getString(Const.MESSAGE, "");
        if (!"".equals(newMessage))
            data.add(new Chat(Const.MESSAGE_RECEIVE, newMessage, Const.GENDER_WOMAN));
        token = SharedPrefManager.getInstance(getApplicationContext()).getString(Const.FCM_TOKEN);
        nameConversation = bundle.getString(Const.NAME_CONVERSATION);
        idConversation = bundle.getInt(Const.CONVERSATION_ID);


        tvNameConversation.setText(nameConversation);
    }

    public TextWatcher changeMessage = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            message = edInputMessage.getText().toString();
            int lengthMessage = message.length();
            if (lengthMessage > 0) {
                btSend.setBackgroundResource(R.drawable.button_send_message_2);
                btSend.setEnabled(true);
            } else {
                btSend.setBackgroundResource(R.drawable.button_send_message_1);
                btSend.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            message = edInputMessage.getText().toString();
        }
    };

    public View.OnClickListener btSendClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            HashMap<String, String> params = new HashMap<>();
            params.put(Const.MESSAGE, message);
            params.put(Const.CONVERSATION_ID, String.valueOf(idConversation));
            params.put(Const.TOKEN, bundle.getString(Const.TOKEN));
            btSend.setEnabled(false);
            btSend.setBackgroundResource(R.drawable.button_send_message_2);
            data.add(new Chat(Const.MESSAGE_SEND, message, Const.GENDER_MAN));
            adapter.notifyDataSetChanged();
            edInputMessage.setText("");
            sendMessage(params);
        }
    };


    public void sendMessage(final HashMap<String, String> params) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Const.URL_SEND_MESSAGE, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt(Const.CODE) != Const.CODE_OK) {
                                Toast.makeText(getApplicationContext(), "Gui loi", Toast.LENGTH_SHORT).show();
                                data.remove(data.size() - 1);
                                adapter.notifyDataSetChanged();
                            } else {
                                database.saveMessage(params.get(Const.MESSAGE),idConversation,useId,useId);
                            }
                        } catch (JSONException e) {
                            Log.d(Const.TAG,"json err");
                        }
                        Log.d(Const.TAG,"response");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(Const.TAG,"volley er");
                    }
                });


        jsObjRequest.setShouldCache(false);
        requestQueue.add(jsObjRequest);
    }


}
