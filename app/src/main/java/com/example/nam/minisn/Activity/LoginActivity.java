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
import com.example.nam.minisn.ItemListview.Friend;
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
    private SQLiteDataController database;
    private boolean isLoadedConversation = false;
    private boolean isLoadedFriend = false;
    private boolean isLoadedRequest = false;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        init();
        listener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        database.openDataBase();
    }

    @Override
    protected void onPause() {
        super.onPause();
        database.close();
    }

    public void init() {
        database =  new SQLiteDataController(getApplicationContext());
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
                                token = newObject.getString(Const.TOKEN);
                                use_id = newObject.getInt(Const.ID);
                                String username = newObject.getString(Const.USERNAME);
                                Bundle bundle = new Bundle();
                                bundle.putString(Const.USERNAME, newObject.getString(Const.USERNAME));
                                bundle.putString(Const.TOKEN, token);
                                SharedPrefManager.getInstance(getApplicationContext()).savePreferences(Const.TOKEN, token);
                                SharedPrefManager.getInstance(getApplicationContext()).savePreferences(Const.USERNAME, newObject.getString(Const.USERNAME));
                                SharedPrefManager.getInstance(getApplicationContext()).savePreferences(Const.ID, use_id);
                                SharedPrefManager.getInstance(getApplicationContext()).savePreferences(Const.IS_LOGIN, Const.LOGIN);
                                intentLogin.putExtra(Const.PACKAGE, bundle);
                                setEnableEdit(true);

                                if (!database.checkLogged(use_id)) {
                                    database.saveAccount(use_id,username);
                                    saveListFriend();


                                }

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
                        Log.d(Const.TAG,"login vl er");
                        setEnableEdit(true);
                        Log.d(Const.TAG, "dang nhap that bai:"+error.getMessage());
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


//

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
//                        JSONArray
                        int leght = listConversation.length();
                        for (int i = 0; i < leght; i++) {
                            JSONObject object = listConversation.getJSONObject(i);
                            int idConversation = object.getInt(Const.ID);
                            String nameConversation = object.getString(Const.NAME_CONVERSATION);
                            JSONArray listUser = object.getJSONArray(Const.LIST_USER);
                            database.insertConversation(idConversation, nameConversation,use_id,listUser.length());
                            if (listUser.length() ==2){
                                int fri_id = listUser.getJSONObject(0).getInt(Const.ID);
                                if ( fri_id == use_id)
                                    fri_id = listUser.getJSONObject(1).getInt(Const.ID);
                                database.addIdConversationIntoFriend(use_id,idConversation,fri_id);
                            }
                        }
                        getListRequestFriend();
                    } else
                        Toast.makeText(getApplicationContext(), "Co loi xay ra", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(Const.TAG, "JSON error: " + e.getMessage());
                }
                isLoadedConversation = true;

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(Const.TAG, "Request Error sv list cv");
            }
        });

        requestQueue.add(objectRequest);
    }

//


    public void saveListFriend() {
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
                            int id = obj.getInt(Const.ID_FRIEND);
                            database.insertListFriend(fri_id, username, use_id, gender,id);
                        }
                        saveListConversation(token);

                    } else
                        Toast.makeText(getApplicationContext(), "Co loi xay ra", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(Const.TAG, "JSON error: " + e.getMessage());
                }
                isLoadedFriend = true;

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(Const.TAG, "Request Error sv list fr");
            }
        });

        request.add(objectRequest);
        progressDialog.dismiss();
    }

    public void getListRequestFriend(){
    RequestQueue request = Volley.newRequestQueue(getApplicationContext());
    JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, Const.URL_GET_REQUEST_FRIEND +
            Const.TOKEN +"="+token
            ,new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            try{
                if (Const.CODE_OK == jsonObject.getInt(Const.CODE)){
                    JSONArray data = jsonObject.getJSONArray(Const.DATA);
                    for (int i = 0;i<data.length();i++){
                        JSONObject request = data.getJSONObject(i);
                        String username = request.getString(Const.USERNAME);
                        int id = request.getInt(Const.ID);
                        database.saveRequestFriend(use_id,username,id);

                    }
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                    startActivity(intentLogin);
                }else
                    Toast.makeText(getApplicationContext(),"Co loi xay ra",Toast.LENGTH_SHORT).show();
            }catch (JSONException e){
                e.printStackTrace();
                Log.d(Const.TAG,"JSON error: "+ e.getMessage());
            }
            isLoadedRequest = true;

        }
    },new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Log.d(Const.TAG,"Request Error");
        }
    });

        request.add(objectRequest);
}
}
