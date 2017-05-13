package com.example.nam.minisn.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private TextInputLayout usernameWrapper, passwordWrapper, rePasswordWrapper;
    private TextInputLayout emailWrapper, fullnameWrapper, birthWrapper;
    private EditText inputUsername, inputPassword, inputRepassword;
    private EditText inputFullname, inputEmail;
    private TextView inputBirth;
    private LinearLayout lnBirth;
    private RadioGroup groupGender;
    private RadioButton btnMale, btnFemale;
    private Button btnRegister;
    private LinearLayout btnBack;
    private TextView toolbarText;
    private Calendar calendar;
    private Intent intent;
    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        init();
    }

    public void init() {
        lnBirth = (LinearLayout) findViewById(R.id.res_ln_birth);
        toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText("Đăng kí");
        btnBack = (LinearLayout) findViewById(R.id.toolbar_btnback);
        usernameWrapper = (TextInputLayout) findViewById(R.id.register_usernameWrapper);
        passwordWrapper = (TextInputLayout) findViewById(R.id.register_passWrapper);
        emailWrapper = (TextInputLayout) findViewById(R.id.res_email_wrapper);
        fullnameWrapper = (TextInputLayout) findViewById(R.id.res_fullname_wrapper);
        rePasswordWrapper = (TextInputLayout) findViewById(R.id.res_retype_wrapper);
        birthWrapper = (TextInputLayout) findViewById(R.id.res_birth_wrapper);

        inputUsername = (EditText) findViewById(R.id.res_input_username);
        inputPassword = (EditText) findViewById(R.id.res_input_pass);
        inputRepassword = (EditText) findViewById(R.id.res_input_retype);
        inputEmail = (EditText) findViewById(R.id.res_input_email);
        inputFullname = (EditText) findViewById(R.id.res_input_fullname);
        inputBirth = (TextView) findViewById(R.id.res_input_birth);
        groupGender = (RadioGroup) findViewById(R.id.res_gender);
        btnMale = (RadioButton) findViewById(R.id.res_gender_male);
        btnFemale = (RadioButton) findViewById(R.id.res_gender_female);
        btnRegister = (Button) findViewById(R.id.res_btn_res);

        intent = getIntent();
        bundle = intent.getBundleExtra(Const.PACKAGE);
        String username = bundle.getString(Const.USERNAME,"");
        inputUsername.setText(username);
        listener();
    }

    public void listener() {
        btnBack.setOnClickListener(this);
        lnBirth.setOnClickListener(this);
        inputBirth.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    TextWatcher checkUsername = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String username = inputUsername.getText().toString();
        }
    };

    public void checkExist(HashMap<String, String> params) {

    }

    public void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear,
                                  int dayOfMonth) {
                inputBirth.setText(
                        (dayOfMonth) + "/" + (monthOfYear + 1) + "/" + year);
            }
        };
        String birth = inputBirth.getText().toString();
        Calendar now = Calendar.getInstance();
        int day, month, year;
        if ("".equals(birth)) {
            day = now.get(Calendar.DAY_OF_MONTH);
            month = now.get(Calendar.MONTH);
            year = now.get(Calendar.YEAR);
        } else {

            String strArrtmp[] = birth.split("/");
            day = Integer.parseInt(strArrtmp[0]);
            month = Integer.parseInt(strArrtmp[1]) - 1;
            year = Integer.parseInt(strArrtmp[2]);
        }
        DatePickerDialog pic = new DatePickerDialog(
                RegisterActivity.this,
                callback, year, month, day);
        pic.setTitle("Chọn ngày sinh");
        pic.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_btnback:
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle("Chưa đăng kí!");
                b.setMessage("Thoát?");
                b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                b.create().show();
                break;
            case R.id.res_ln_birth:
                showDatePickerDialog();
                break;
            case R.id.res_input_birth:
                showDatePickerDialog();
                break;
            case R.id.res_btn_res:
                if (validateValues())
                    register();
                break;
        }
    }

    public boolean validateWordSpecial(String value) {
        String filter = "^([0-9a-zA-Z | _ |. |-])+$";
        Pattern pattern = Pattern.compile(filter);
        return pattern.matcher(value).matches();
    }

    public boolean validateEmail(String email) {
        String filter = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(filter);
        return pattern.matcher(email).matches();
    }

    public boolean validateLength(String value) {
        return (value.length() <= 20 && value.length() >= 8);
    }

    public boolean validateValues() {
        boolean result = true;
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();
        String rePass = inputRepassword.getText().toString();
        String email = inputEmail.getText().toString();
        String fullName = inputFullname.getText().toString();
        String fullNameConvert = covertStringUnicode(fullName);
        String birthday = inputBirth.getText().toString();
        if (validateLength(username)) {
            if (!validateWordSpecial(username)) {
                result = false;
                usernameWrapper.setError("Tên đăng nhập không được có kí tự đặc biệt");
            } else
                usernameWrapper.setErrorEnabled(false);
        } else {
            result = false;
            usernameWrapper.setError("Tên đăng nhập có 8 đến 20 kí tự");
        }
        if (validateLength(password)) {
            if (validateWordSpecial(password)) {
                if (!password.equals(rePass)) {
                    result = false;
                    rePasswordWrapper.setError("Nhập lại mật khẩu sai");
                } else
                    rePasswordWrapper.setError("");
                passwordWrapper.setErrorEnabled(false);
            } else {
                result = false;
                passwordWrapper.setError("Tên đăng nhập không được có kí tự đặc biệt");
            }
        } else {
            result = false;
            passwordWrapper.setError("Mật khẩu có 8 đến 20 kí tự");
        }
        if (!email.isEmpty()) {
            if (!validateEmail(email)) {
                emailWrapper.setError("Email sai định dạng");
                result = false;
            } else
                emailWrapper.setErrorEnabled(false);
        } else {
            result = false;
            emailWrapper.setError("Bạn chưa nhập email");
        }

        if (fullNameConvert.length() < 30 && fullNameConvert.length() >= 8) {
            if (!validateWordSpecial(fullNameConvert)) {
                fullnameWrapper.setError("Nhập sai họ tên");
                result = false;
            } else fullnameWrapper.setErrorEnabled(false);
        } else {
            fullnameWrapper.setError("Nhập tên từ 8 đến 30 kí tự");
            result = false;
        }

        if (birthday.isEmpty()) {
            birthWrapper.setError("Chưa chọn ngày sinh");
            result = false;
        } else
            birthWrapper.setErrorEnabled(false);
        return result;
    }

    public String covertStringUnicode(String str) {
        try {
            String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").toLowerCase().replaceAll(" ", "").replaceAll("đ", "d");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void register() {
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Dialog);
        progressDialog.setMessage("Đăng kí ...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        final String username = inputUsername.getText().toString();
        final String password = inputPassword.getText().toString();
        String rePass = inputRepassword.getText().toString();
        String email = inputEmail.getText().toString();
        String fullName = inputFullname.getText().toString();
        String birthday = inputBirth.getText().toString();
        int gender = btnMale.isChecked() ? Const.GENDER_MAN : Const.GENDER_WOMAN;
        HashMap<String, String> params = new HashMap<>();
        params.put(Const.USERNAME, username);
        params.put(Const.PASSWORD, password);
        params.put(Const.EMAIL, email);
        params.put(Const.DISPLAY_NAME, fullName);
        params.put(Const.BIRTH_DAY, birthday);
        params.put(Const.GENDER, String.valueOf(gender));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        CustomRequest jsonRequest = new CustomRequest(Request.Method.POST, Const.URL_REGISTER, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt(Const.CODE) != Const.CODE_OK) {
                                Toasty.error(getApplicationContext(), response.getString(Const.MESSAGE), Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = getIntent();
                                Bundle bundle = new Bundle();
                                bundle.putString(Const.USERNAME, username);
                                bundle.putString(Const.PASSWORD, password);
                                intent.putExtra(Const.PACKAGE, bundle);
                                Toasty.success(getApplicationContext(),"Đăng kí thành công",Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(Const.TAG, "res json err");
                            Toasty.error(getApplicationContext(), getResources().getString(R.string.notifi_error), Toast.LENGTH_SHORT).show();

                        }
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toasty.error(getApplicationContext(), getResources().getString(R.string.notifi_error), Toast.LENGTH_SHORT).show();
                        Log.d(Const.TAG, "res vl err");

                    }
                });
        jsonRequest.setShouldCache(false);
        requestQueue.add(jsonRequest);
    }
}
