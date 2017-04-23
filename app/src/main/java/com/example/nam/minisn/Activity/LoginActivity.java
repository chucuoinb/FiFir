package com.example.nam.minisn.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nam.minisn.R;
import com.example.nam.minisn.UseVoley.CustomRequest;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SharedPrefManager;
import com.example.nam.minisn.Util.SQLiteDataController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText edUser;
    private EditText edPass;
    private CheckBox cbSave;
    private Button btLogin;
    private TextView tvRegister;
    private String username;
    private String password;
    private TextInputLayout usernameWrapper;
    private TextInputLayout passwordWrapper;
    private Intent intentLogin, intentReg;
    private ProgressDialog progressDialog;
    private int use_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        init();
        listener();
    }

    public void init() {
        edUser = (EditText) findViewById(R.id.login_ed_user);
        edPass = (EditText) findViewById(R.id.login_ed_password);
        cbSave = (CheckBox) findViewById(R.id.login_cb_save);
        btLogin = (Button) findViewById(R.id.login_btn_login);
        tvRegister = (TextView) findViewById(R.id.login_tv_register);
        usernameWrapper = (TextInputLayout) findViewById(R.id.login_usernameWrapper);
        passwordWrapper = (TextInputLayout) findViewById(R.id.login_passwordWrapper);

        intentLogin = new Intent(LoginActivity.this, Main.class);
    }


    public void listener() {
        btLogin.setOnClickListener(btLoginClick);
    }

    public View.OnClickListener btLoginClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!CheckLogin()) {
                OnLoginFailed();
                return;
            }
            setEnableEdit(false);
            username = edUser.getText().toString();
            password = edPass.getText().toString();
            HashMap<String, String> params = new HashMap<>();
            params.put(Const.USERNAME, username);
            params.put(Const.PASSWORD, password);
            params.put(Const.FCM_TOKEN, SharedPrefManager.getInstance(getApplicationContext()).getString(Const.FCM_TOKEN));
            progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getResources().getString(R.string.login_login));
            progressDialog.show();
//            checkLogged(username);
            DoLogin(params);

        }
    };

    public void DoLogin(HashMap<String, String> params) {

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Const.URL_LOGIN, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt(Const.CODE) != Const.CODE_OK) {
                                CheckCode(response.getInt(Const.CODE));
                            } else {
                                JSONObject newObject = response.getJSONObject(Const.DATA);
                                String token = newObject.getString(Const.TOKEN);
                                use_id = newObject.getInt(Const.ID);
                                String username = newObject.getString(Const.USERNAME);
                                Bundle bundle = new Bundle();
                                bundle.putString(Const.USERNAME, newObject.getString(Const.USERNAME));
                                bundle.putString(Const.TOKEN, token);
                                SharedPrefManager.getInstance(getApplicationContext()).savePreferences(Const.TOKEN, token);
                                SharedPrefManager.getInstance(getApplicationContext()).savePreferences(Const.USERNAME, newObject.getString(Const.USERNAME));
                                SharedPrefManager.getInstance(getApplicationContext()).savePreferences(Const.ID, use_id);
                                intentLogin.putExtra(Const.PACKAGE, bundle);
                                setEnableEdit(true);
                                SharedPrefManager.getInstance(getApplicationContext()).savePreferences(Const.IS_LOGIN, Const.LOGIN);
                                if (!checkLogged(use_id)) {
                                    saveAccount(use_id,username);
                                    saveListFriend(token);
                                    saveListConversation(token);
                                }
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                                startActivity(intentLogin);
                            }

                        } catch (JSONException e) {
                            Log.d(Const.TAG, e.getMessage());
                            setEnableEdit(true);
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setEnableEdit(true);
                        Log.d(Const.TAG, "dang nhap that bai");
                        progressDialog.dismiss();
                    }
                });


        jsObjRequest.setShouldCache(false);
        requestQueue.add(jsObjRequest);
        //Log.d(Const.TAG,"done login");
    }

    public boolean CheckLogin() {
        boolean check = true;
        username = edUser.getText().toString();
        password = edPass.getText().toString();
        if (username.isEmpty()) {
            usernameWrapper.setError(getResources().getString(R.string.login_error_user));
            check = false;
        }
        if (password.isEmpty()) {
            passwordWrapper.setError(getResources().getString(R.string.login_error_password));
            check = false;
        }
        return check;
    }

    private void OnLoginFailed() {
        btLogin.setEnabled(true);
    }

    private void setEnableEdit(boolean bol) {
        btLogin.setEnabled(bol);
        edPass.setEnabled(bol);
        edUser.setEnabled(bol);
    }

    public void OnLoginSuccess() {
        btLogin.setEnabled(true);
    }

    public View.OnClickListener tvRegClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            intentReg = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivityForResult(intentReg, Const.REQUEST_CODE_REGISTER);
        }
    };

    private void CheckCode(int code) {

        switch (code) {
            case Const.CODE_FAIL:
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
                setEnableEdit(true);
                break;
            case Const.CODE_USER_NOT_EXIST:

                AlertDialog.Builder b = new AlertDialog.Builder(LoginActivity.this);
                b.setTitle(getResources().getString(R.string.login_user_not_exist));
                b.setMessage(getResources().getString(R.string.login_question_register));
                b.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        setEnableEdit(true);
                    }
                });
                b.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                b.create().show();
                break;
            default:
                break;
        }
    }


    private boolean checkLogged(int id) {
        SQLiteDataController sql = new SQLiteDataController(getBaseContext());
        try {
            sql.isCreatedDatabase();
            sql.openDataBase();
            Cursor cursor = sql.getDatabase().rawQuery(Const.SELECT + "*" + Const.FROM + Const.DB_USERS_SAVE + Const.WHERE +
                    Const.SAVE_COL1 + " = '" + id + "'", null);
            Log.d(Const.TAG,String.valueOf(cursor.getCount()>0));
            return cursor.getCount() > 0;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(Const.TAG, e.getMessage());
            return false;
        }
    }

    public void saveListConversation(String token) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, Const.URL_GET_LIST_CONVERSATION + "/?" +
                Const.TOKEN + "=" + token
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.getInt(Const.CODE) == Const.CODE_OK) {
                        JSONArray listConversation = jsonObject.getJSONArray(Const.DATA);
                        int leght = listConversation.length();
                        for (int i = 0; i < leght; i++) {
                            JSONObject object = listConversation.getJSONObject(i);
                            int idConversation = object.getInt(Const.ID);
                            String nameConversation = object.getString(Const.NAME_CONVERSATION);
                            insertConversation(idConversation, nameConversation);
                        }
                    } else
                        Toast.makeText(getApplicationContext(), "Co loi xay ra", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(Const.TAG, "JSON error: " + e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(Const.TAG, "Request Error");
            }
        });

        requestQueue.add(objectRequest);
        progressDialog.dismiss();
    }

    private void insertConversation(int id, String name) {
        SQLiteDataController db = new SQLiteDataController(getBaseContext());
        try {
            db.isCreatedDatabase();
            db.openDataBase();
            String sql = Const.INSERT +
                    Const.DB_CONVERSATION +
                    " (" +
                    Const.CONVERSATION_COL1 +
                    "," +
                    Const.CONVERSATION_COL2 +
                    "," +
                    Const.CONVERSATION_COL5 +
                    ")" +
                    Const.VALUES +
                    "('" +
                    name +
                    "','" +
                    id +
                    "','" +
                    use_id +
                    "')";
            db.getDatabase().execSQL(sql);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(Const.TAG, e.getMessage());
        }
    }


    public void saveListFriend(String token) {
//        final int use_id = id;
        RequestQueue request = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, Const.URL_GET_LIST_FRIEND + "/?" +
                Const.TOKEN + "=" + token
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (Const.CODE_OK == jsonObject.getInt(Const.CODE)) {
                        JSONArray listFriend = jsonObject.getJSONArray(Const.DATA);
                        for (int i = 0; i < listFriend.length(); i++) {
                            JSONObject obj = listFriend.getJSONObject(i);
                            String username = obj.getString(Const.USERNAME);
//                            String displayName = obj.getString(Const.DISPLAY_NAME);
                            int gender = obj.getInt(Const.GENDER);
                            int fri_id = obj.getInt(Const.ID);
//                            Log.d(Const.TAG,username+":"+displayName+":"+gender+":"+fri_id+":"+use_id);
                            insertListFriend(fri_id, username, use_id, gender);
                        }
                    } else
                        Toast.makeText(getApplicationContext(), "Co loi xay ra", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(Const.TAG, "JSON error: " + e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(Const.TAG, "Request Error");
            }
        });

        request.add(objectRequest);
        progressDialog.dismiss();
    }

    public void insertListFriend(int fri_id, String fri_username, int id, int gender) {
//        Log.d(Const.TAG, fri_username + ":" + gender + ":" + fri_id + ":" + use_id);
        SQLiteDataController db = new SQLiteDataController(LoginActivity.this);
        try {
            db.isCreatedDatabase();
            db.openDataBase();
            String sql = Const.INSERT +
                    Const.DB_FRIEND +
                    " (" +
                    Const.FRIENDS_COL1 +
                    "," +
                    Const.FRIENDS_COL2 +
                    "," +
                    Const.FRIENDS_COL4 +
                    "," +
                    Const.FRIENDS_COL5 +
                    ")" +
                    Const.VALUES +
                    "('" +
                    fri_id +
                    "','" +
                    fri_username +
                    "','" +
                    id +
                    "','" +
                    gender +
                    "')";
            db.getDatabase().execSQL(sql);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(Const.TAG, e.getMessage());
        }
    }

    public void saveAccount(int id,String username){
        SQLiteDataController db = new SQLiteDataController(getBaseContext());
        try {
            db.isCreatedDatabase();
            db.openDataBase();
            String sql = Const.INSERT +
                    Const.DB_USERS_SAVE +
                    " (" +
                    Const.SAVE_COL1 +
                    "," +
                    Const.SAVE_COL2 +
                    ")" +
                    Const.VALUES +
                    "('" +
                    id +
                    "','" +
                    username +
                    "')";
            db.getDatabase().execSQL(sql);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(Const.TAG, e.getMessage());
        }
    }
}
