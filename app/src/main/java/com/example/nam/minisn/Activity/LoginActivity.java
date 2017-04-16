package com.example.nam.minisn.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.android.volley.toolbox.Volley;
import com.example.nam.minisn.R;
import com.example.nam.minisn.UseVoley.CustomRequest;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

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

        intentLogin = new Intent(LoginActivity.this,Main.class);
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
            params.put(Const.FCM_TOKEN,SharedPrefManager.getInstance(getApplicationContext()).getString(Const.FCM_TOKEN));
            progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getResources().getString(R.string.login_login));
            progressDialog.show();

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
                            progressDialog.dismiss();

                            if (response.getInt(Const.CODE) != Const.CODE_OK) {
                                CheckCode(response.getInt(Const.CODE));
                            } else {
                                JSONObject newObject = response.getJSONObject(Const.DATA);
                                String token = newObject.getString(Const.TOKEN);
                                Bundle bundle = new Bundle();
                                bundle.putString(Const.USERNAME, newObject.getString(Const.USERNAME));
                                bundle.putString(Const.TOKEN, token);
                                SharedPrefManager.getInstance(getApplicationContext()).savePreferences(Const.TOKEN,token);
                                SharedPrefManager.getInstance(getApplicationContext()).savePreferences(Const.USERNAME,newObject.getString(Const.USERNAME));
                                intentLogin.putExtra(Const.PACKAGE, bundle);
                                setEnableEdit(true);
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                                startActivity(intentLogin);
                            }

                        } catch (JSONException e) {
                            Log.d(Const.TAG, e.getMessage());
                            setEnableEdit(true);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setEnableEdit(true);
                        Log.d(Const.TAG, "dang nhap that bai");
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
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });
                b.create().show();
                break;
            default:
                break;
        }
    }


    /**
     * saving token in preferences
     * @param token
     * @param username
     */
    public  void savingPreferences(String token,String username) {

        SharedPreferences pre=getSharedPreferences
                (Const.SHARED_PREF_NAME, MODE_PRIVATE);

        SharedPreferences.Editor editor=pre.edit();
        editor.clear();
        editor.putString(Const.TOKEN, token);
        editor.putString(Const.USERNAME,username);
        editor.commit();
    }
}
