package com.example.nam.minisn.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nam.minisn.Adapter.CommentAdapter;
import com.example.nam.minisn.ItemListview.Comment;
import com.example.nam.minisn.R;
import com.example.nam.minisn.UseVoley.CustomRequest;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {
    private CircleImageView avatar;
    private TextView tvFullnameStatus;
    private TextView tvTimeStatus;
    private TextView tvStatus;
    private TextView countLikeStatus;
    private ImageView btLikeStatus;
    private ImageView btGoChat;
    private ImageView btSend;
    private EditText edInput;
    private ListView lvComment;
    private String token;
    private String status;
    private String fullnameStatus;
    private String comment;
    private int useId;
    private String fullname;
    private int idStatus;
    private int idUsername;
    private int numberLike;
    private int typeLike;
    private long timeStatus;
    private Intent intent;
    private Bundle bundle;
    private ArrayList<Comment> data;
    private CommentAdapter adapter;
    private LinearLayout loadError, loadSuccess, btBack;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_comment);
        init();
        listener();
    }

    public void init() {
        btBack = (LinearLayout) findViewById(R.id.toolbar_btnback);
        countLikeStatus = (TextView) findViewById(R.id.count_like_status);
        loadError = (LinearLayout) findViewById(R.id.comment_load_err);
        loadSuccess = (LinearLayout) findViewById(R.id.comment_load_success);
        avatar = (CircleImageView) findViewById(R.id.comment_ava_status);
        tvFullnameStatus = (TextView) findViewById(R.id.comment_name);
        tvTimeStatus = (TextView) findViewById(R.id.comment_status_time);
        tvStatus = (TextView) findViewById(R.id.comment_status);
        btLikeStatus = (ImageView) findViewById(R.id.comment_bt_like);
        btGoChat = (ImageView) findViewById(R.id.btn_go_chat);
        btSend = (ImageView) findViewById(R.id.comment_bt_send);
        edInput = (EditText) findViewById(R.id.comment_input);
        lvComment = (ListView) findViewById(R.id.comment_lv);

        token = SharedPrefManager.getInstance(getApplicationContext()).getString(Const.TOKEN);
        useId = SharedPrefManager.getInstance(getApplicationContext()).getInt(Const.ID);
        fullname = SharedPrefManager.getInstance(getApplicationContext()).getString(Const.DISPLAY_NAME);
        intent = getIntent();
        bundle = intent.getBundleExtra(Const.PACKAGE);
        idStatus = bundle.getInt(Const.ID);
        comment = new String();

        dialog = new ProgressDialog(this, R.style.AppTheme_Dialog);
        dialog.setCancelable(false);
        showDialog("Đang tải ...");
        data = new ArrayList<>();
        adapter = new CommentAdapter(this, R.layout.item_lv_comment, data);
        lvComment.setAdapter(adapter);


        loadInfoStatus();
    }

    public void listener() {
        btBack.setOnClickListener(this);
        btLikeStatus.setOnClickListener(this);
        loadError.setOnClickListener(this);
        btGoChat.setOnClickListener(this);
        btSend.setOnClickListener(this);
        edInput.addTextChangedListener(changeComment);
    }

    public void loadInfoStatus() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Const.URL_GET_INFO_STATUS + Const.TOKEN + "=" + token + "&id=" + idStatus,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dimissDialog();
                            if (response.getInt(Const.CODE) != Const.CODE_OK) {
                                setLoad(false);
                                Toasty.error(getApplicationContext(), getResources().getString(R.string.notifi_error), Toast.LENGTH_SHORT).show();
                            } else {
                                setLoad(true);
                                JSONObject jsStatus = response.getJSONObject(Const.DATA);
                                idUsername = jsStatus.getInt(Const.ID_USERNAME);
                                status = jsStatus.getString(Const.STATUS);
                                timeStatus = jsStatus.getLong(Const.TIME_POST);
                                numberLike = jsStatus.getInt(Const.NUMBER_LIKE);
                                typeLike = jsStatus.getInt(Const.TYPE_LIKE);
                                fullnameStatus = jsStatus.getString(Const.DISPLAY_NAME);
                                tvStatus.setText(status);
                                tvTimeStatus.setText(Const.getStringTime(getApplicationContext(), timeStatus));
                                tvFullnameStatus.setText(fullnameStatus);
                                countLikeStatus.setText(String.valueOf(numberLike));
                                if (typeLike == Const.STA_LIKE)
                                    btLikeStatus.setImageResource(R.drawable.like);
                                else
                                    btLikeStatus.setImageResource(R.drawable.unlike);
                            }
                        } catch (JSONException e) {
                            dimissDialog();
                            e.printStackTrace();
                            setLoad(false);
                            Toasty.error(getApplicationContext(), getResources().getString(R.string.notifi_error), Toast.LENGTH_SHORT).show();
                            Log.d(Const.TAG, "json err");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(Const.TAG, "vl err");
                        setLoad(false);
                        Toasty.error(getApplicationContext(), getResources().getString(R.string.notifi_error), Toast.LENGTH_SHORT).show();
                        dimissDialog();
                    }
                }
        );
        requestQueue.add(request);
    }

    public void setLoad(boolean type) {
        if (type) {
            loadSuccess.setVisibility(View.VISIBLE);
            loadError.setVisibility(View.INVISIBLE);
            loadError.setEnabled(false);
        } else {
            loadSuccess.setVisibility(View.INVISIBLE);
            loadError.setVisibility(View.VISIBLE);
            loadError.setEnabled(true);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_btnback:
                finish();
                break;
            case R.id.comment_bt_like:
                break;
            case R.id.comment_bt_send:
                postComment();
                break;
            case R.id.btn_go_chat:
                break;
            case R.id.comment_load_err:
                showDialog("Đang tải ...");
                loadInfoStatus();
                break;
        }
    }

    public TextWatcher changeComment = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            comment = edInput.getText().toString();
            if (comment.length() > 0) {
                btSend.setImageResource(R.drawable.button_send_message_2);
                btSend.setEnabled(true);
            } else {
                btSend.setImageResource(R.drawable.button_send_message_1);
                btSend.setEnabled(false);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void postComment() {
        showDialog("Đang đăng...");
        HashMap<String, String> params = new HashMap<>();
        params.put(Const.TOKEN, token);
        params.put(Const.ID, String.valueOf(idStatus));
        params.put(Const.COMMENT, comment);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        CustomRequest request = new CustomRequest(Request.Method.POST, Const.URL_ADD_COMMENT, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dimissDialog();
                        try {
                            if (response.getInt(Const.CODE) != Const.CODE_OK){
                                Toasty.error(getApplicationContext(), getResources().getString(R.string.notifi_error), Toast.LENGTH_SHORT).show();
                            }
                            else {
                                edInput.setText("");
                                int idComment = response.getInt(Const.DATA);
                                Comment myComment = new Comment(idComment,useId, fullname,comment,System.currentTimeMillis()/1000);
                                data.add(0,myComment);
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        request.setShouldCache(false);
        requestQueue.add(request);
    }

    public void showDialog(String message){
        dialog.setMessage(message);
        dialog.show();
    }
    public void dimissDialog(){
        if (dialog.isShowing())
            dialog.dismiss();
    }

}
