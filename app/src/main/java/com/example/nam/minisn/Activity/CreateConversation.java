package com.example.nam.minisn.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.nam.minisn.Adapter.CreateConversationAdapter;
import com.example.nam.minisn.ItemListview.Friend;
import com.example.nam.minisn.ItemListview.ItemDeleteFriend;
import com.example.nam.minisn.R;
import com.example.nam.minisn.UseVoley.CustomRequest;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SQLiteDataController;
import com.example.nam.minisn.Util.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class CreateConversation extends AppCompatActivity implements View.OnClickListener {
    private EditText inputName;
    private static EditText listFriend;
    private ListView listView;
    private FloatingActionButton btnAdd;
    private static ArrayList<ItemDeleteFriend> data;
    private static String textSearch;
    private CreateConversationAdapter adapter;
    private SQLiteDataController dataControllerl;
    private LinearLayout btnBack;
    private TextView toolbarText;
    private int useId;
    private Bundle bundle;
    private String token;
    private Intent intent;
    private ArrayList<Friend> friendAdd;
    private String nameConversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_create_conversation);
        init();
    }

    public void init() {
        toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText("Tạo nhóm");
        btnBack = (LinearLayout) findViewById(R.id.toolbar_btnback);
        btnAdd = (FloatingActionButton) findViewById(R.id.add_conversation_btn);
        inputName = (EditText) findViewById(R.id.add_conversation_input);
        listView = (ListView) findViewById(R.id.add_conversation_lv);
        listFriend = (EditText) findViewById(R.id.add_conversation_list_friend);

        token = SharedPrefManager.getInstance(getApplicationContext()).getString(Const.TOKEN);
        nameConversation = new String();
        friendAdd = new ArrayList<>();
        textSearch = new String();
        bundle = new Bundle();
        intent = new Intent(this, ChatActivity.class);
        useId = SharedPrefManager.getInstance(getApplicationContext()).getInt(Const.ID);
        dataControllerl = new SQLiteDataController(getApplicationContext());
        dataControllerl.openDataBase();
        data = dataControllerl.getListFriend(useId, false);
        adapter = new CreateConversationAdapter(this, R.layout.item_lvfriend, data);
        for (int i = 0; i < data.size(); i++)
            Log.d(Const.TAG, "create: " + data.get(i).getFriend().getUsername());
        listener();
    }

    public void listener() {
        listView.setAdapter(adapter);
        btnAdd.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        inputName.addTextChangedListener(inputText);
    }

    public static EditText getListFriend() {
        return listFriend;
    }

    public static ArrayList<ItemDeleteFriend> getData() {
        return data;
    }

    public static String getTextSearch() {
        if (textSearch.length() > 0)
            return textSearch;
        else
            return "";
    }

    TextWatcher inputText = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            textSearch = inputName.getText().toString();
            data.clear();
            data.addAll(dataControllerl.searchFriends(textSearch, useId));
            adapter.notifyDataSetChanged();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_btnback:
                finish();
                break;
            case R.id.add_conversation_btn:
                addConversation();
                break;
        }
    }

    public void addConversation() {
        friendAdd = dataControllerl.getListFriendAdd(useId);
        final HashMap<String, String> params = new HashMap<>();
        if (friendAdd.size() == 0) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_choose), Toast.LENGTH_SHORT).show();
        }
        if (friendAdd.size() == 1) {
            int idConversation = dataControllerl.getConversationFriend(friendAdd.get(0).getId(), useId);
            if (idConversation > 0) {
                String name = dataControllerl.getNameConversation(idConversation, useId);
                bundle.putInt(Const.CONVERSATION_ID, idConversation);
                bundle.putString(Const.NAME_CONVERSATION, name);
                intent.putExtra(Const.PACKAGE, bundle);
                startActivity(intent);
            } else {
                params.put(Const.TOKEN, token);

                params.put(Const.NAME_CONVERSATION, friendAdd.get(0).getUsername());
                params.put(Const.ID_USER_FRIEND + "0", String.valueOf(friendAdd.get(0).getId()));
                addNewConversation(params, 2);
            }
        }
        if (friendAdd.size() > 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Nhập tên nhóm");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setPadding(50, 50, 50, 50);
            builder.setView(input);
            builder.setPositiveButton("OK", null);
            builder.setNegativeButton("Cancel", null);

//            builder.show();
            final AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positive.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            nameConversation = input.getText().toString();
                            if (nameConversation.length() == 0)
                                Toast.makeText(CreateConversation.this, "Chưa nhập tên nhóm", Toast.LENGTH_SHORT).show();
                            else {
                                if (nameConversation.length() >= 20)
                                    Toast.makeText(CreateConversation.this, "Nhập tối đa 20 kí tự", Toast.LENGTH_SHORT).show();
                                else {
                                    params.clear();
                                    params.put(Const.TOKEN, token);
                                    params.put(Const.NAME_CONVERSATION, nameConversation);
                                    for (int i = 0; i < friendAdd.size(); i++) {
                                        Log.d(Const.TAG, Const.ID_USER_FRIEND + i);
                                        params.put(Const.ID_USER_FRIEND + i, String.valueOf(friendAdd.get(i).getId()));
                                    }
                                    alertDialog.dismiss();
                                    addNewConversation(params, friendAdd.size() + 1);
                                }
                            }
                        }
                    });

                    Button negative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    negative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                }
            });
            alertDialog.show();
        }
    }

    public void addNewConversation(final HashMap<String, String> params, final int size) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Const.URL_ADD_NEW_CONVERSATION, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt(Const.CODE) == Const.CODE_OK) {
                                int idConversation = response.getInt(Const.DATA);
                                dataControllerl.addConversation(idConversation, params.get(Const.NAME_CONVERSATION), "", useId, Const.TYPE_DONT_NEW_MESSAGE);
                                if (size == 1)
                                    dataControllerl.addIdConversationIntoFriend(useId, idConversation, Integer.parseInt(params.get(Const.ID_USER_FRIEND + "0")));
                                dataControllerl.updateSizeConversation(idConversation, useId, size);
                                bundle.putInt(Const.CONVERSATION_ID, idConversation);
                                bundle.putString(Const.NAME_CONVERSATION, params.get(Const.NAME_CONVERSATION));
                                intent.putExtra(Const.PACKAGE, bundle);
                                startActivity(intent);
                            } else {
                                Toasty.error(getApplicationContext(), "Có lỗi xảy ra. Vui lòng thử lại", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            Log.d(Const.TAG, "json er");
                            Toasty.error(getApplicationContext(), "Có lỗi xảy ra. Vui lòng thử lại", Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(Const.TAG, "volley er");
                        Toasty.error(getApplicationContext(), "Có lỗi xảy ra. Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                    }
                });


        jsObjRequest.setShouldCache(false);
        requestQueue.add(jsObjRequest);
    }


    @Override
    protected void onStop() {
        super.onStop();
        dataControllerl.setAllChooseFriend(Const.TYPE_NO_CHOOSE);
        dataControllerl.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataControllerl.openDataBase();
    }
}
