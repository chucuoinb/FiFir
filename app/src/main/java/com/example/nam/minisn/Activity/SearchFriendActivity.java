package com.example.nam.minisn.Activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import com.example.nam.minisn.Adapter.SearchFriendAdapter;
import com.example.nam.minisn.ItemListview.Friend;
import com.example.nam.minisn.ItemListview.SearchFriendItem;
import com.example.nam.minisn.R;
import com.example.nam.minisn.UseVoley.CustomRequest;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SQLiteDataController;
import com.example.nam.minisn.Util.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class SearchFriendActivity extends AppCompatActivity implements View.OnClickListener {

    private static ArrayList<SearchFriendItem> data;
    private static SearchFriendAdapter adapter;
    private EditText edInput;
    private ImageView btSearch;
    private ListView lvData;
    private LinearLayout btnBack;
    private ProgressDialog dialog;
    private TextView toolbarText;
    private String token;
    private int useId;
    private static String textSearch;
    private SQLiteDataController database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_search_friend);
        init();
    }

    public void init() {
        toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText("Thêm bạn");
        btnBack = (LinearLayout) findViewById(R.id.toolbar_btnback);
        lvData = (ListView) findViewById(R.id.search_lv);
        edInput = (EditText) findViewById(R.id.search_input);
        btSearch = (ImageView) findViewById(R.id.search_bt_search);
        textSearch = new String();
        token = SharedPrefManager.getInstance(getApplicationContext()).getString(Const.TOKEN);
        useId = SharedPrefManager.getInstance(getApplicationContext()).getInt(Const.ID);
        database = new SQLiteDataController(getApplicationContext());
        database.openDataBase();
        data = new ArrayList<>();
        adapter = new SearchFriendAdapter(this, R.layout.item_lv_search_friend, data);
        dialog = new ProgressDialog(this, R.style.AppTheme_Dialog);
        dialog.setMessage("Đang tìm kiếm ...");
        listener();
    }

    public void listener() {
        lvData.setAdapter(adapter);
        edInput.addTextChangedListener(changeInput);
        btSearch.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        getListWaitResponse();
    }

    public void getListWaitResponse(){
        data.clear();
        data.addAll(database.getListWaitResponse(useId));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        database.openDataBase();
    }

    @Override
    protected void onStop() {
        super.onStop();
        database.close();
    }

    public TextWatcher changeInput = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            textSearch = edInput.getText().toString();
            if (textSearch.length() > 0)
                btSearch.setVisibility(View.VISIBLE);
            else
                btSearch.setVisibility(View.INVISIBLE);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public void searchClick() {
        textSearch = edInput.getText().toString();
        HashMap<String, String> params = new HashMap<>();
        params.put(Const.TOKEN, token);
        params.put(Const.KEY_SEARCH, textSearch);
        data.clear();
        dialog.show();
        getListFriend(params);
    }

    public void getListFriend(HashMap<String, String> params) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Const.URL_SEARCH_FRIEND, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt(Const.CODE) != Const.CODE_OK) {
                                Toasty.error(getApplicationContext(), "Có lỗi xảy ra. Vui lòng thử lại", Toast.LENGTH_SHORT).show();

                            } else {
                                JSONArray newObject = response.getJSONArray(Const.DATA);
//                                Log.d(Const.TAG,"new obj: "+params.get(Const.KEY_SEARCH));
                                for (int i = 0; i < newObject.length(); i++) {
                                    JSONObject json = newObject.getJSONObject(i);
                                    int id = json.getInt(Const.ID);
                                    if (database.isExistFriend(useId,id) || database.isExistRequest(useId,id) || id==useId) continue;
                                    String username = json.getString(Const.USERNAME);
                                    Friend friend = new Friend(id, username);
                                    data.add(new SearchFriendItem(friend, database.isExistWaitResponse(useId, id)));
                                }
                                dialog.dismiss();
                                adapter.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            Log.d(Const.TAG,"json err");
                            Toasty.error(getApplicationContext(), "Có lỗi xảy ra. Vui lòng thử lại", Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(Const.TAG,"volley err");
                        Toasty.error(getApplicationContext(), "Có lỗi xảy ra. Vui lòng thử lại", Toast.LENGTH_SHORT).show();

                    }
                });


        jsObjRequest.setShouldCache(false);
        requestQueue.add(jsObjRequest);
    }

    public static String getTextSearch() {
        return textSearch;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_btnback:
                finish();
                break;
            case R.id.search_bt_search:
                searchClick();
                break;
        }
    }
    public static ArrayList<SearchFriendItem> getData() {
        return data;
    }
    public static SearchFriendAdapter getAdapter() {
        return adapter;
    }
}
