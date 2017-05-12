package com.example.nam.minisn.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;

import java.util.Calendar;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private TextInputLayout usernameWrapper, passwordWrapper,rePasswordWrapper;
    private TextInputLayout emailWrapper, fullnameWrapper;
    private EditText inputUsername,inputPassword,inputRepassword;
    private EditText inputFullname,inputEmail;
    private TextView inputBirth;
    private LinearLayout lnBirth;
    private RadioGroup groupGender;
    private RadioButton btnMale, btnFemale;
    private Button btnRegister;
    private LinearLayout btnBack;
    private TextView toolbarText;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        init();
    }
    public void init(){
        lnBirth = (LinearLayout)findViewById(R.id.res_ln_birth);
        toolbarText = (TextView)findViewById(R.id.toolbar_text);
        toolbarText.setText("Đăng kí");
        btnBack = (LinearLayout) findViewById(R.id.toolbar_btnback);
        usernameWrapper = (TextInputLayout)findViewById(R.id.register_usernameWrapper);
        passwordWrapper = (TextInputLayout)findViewById(R.id.register_passWrapper);
        emailWrapper = (TextInputLayout)findViewById(R.id.res_email_wrapper);
        fullnameWrapper = (TextInputLayout)findViewById(R.id.res_fullname_wrapper);
        rePasswordWrapper = (TextInputLayout)findViewById(R.id.res_retype_wrapper);

        inputUsername = (EditText)findViewById(R.id.res_input_username);
        inputPassword = (EditText)findViewById(R.id.res_input_pass);
        inputRepassword = (EditText)findViewById(R.id.res_input_retype);
        inputEmail = (EditText)findViewById(R.id.res_input_email);
        inputFullname = (EditText)findViewById(R.id.res_input_fullname);
        inputBirth = (TextView)findViewById(R.id.res_input_birth);
        groupGender = (RadioGroup)findViewById(R.id.res_gender);
        btnMale = (RadioButton)findViewById(R.id.res_gender_male);
        btnFemale = (RadioButton)findViewById(R.id.res_gender_female);
        btnRegister = (Button)findViewById(R.id.res_btn_res);

        listener();
    }

    public void listener(){
        usernameWrapper.setError("Có lỗi xảy ra");
        btnBack.setOnClickListener(this);
        lnBirth.setOnClickListener(this);
        inputBirth.setOnClickListener(this);
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

    public void checkExist(HashMap<String,String> params){

    }

    public void showDatePickerDialog()
    {
        DatePickerDialog.OnDateSetListener callback=new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear,
                                  int dayOfMonth) {
                inputBirth.setText(
                        (dayOfMonth) +"/"+(monthOfYear+1)+"/"+year);
            }
        };
        String birth=inputBirth.getText().toString();
        Calendar now = Calendar.getInstance();
        int day,month,year;
        if ("".equals(birth)){
            day = now.get(Calendar.DAY_OF_MONTH);
            month = now.get(Calendar.MONTH);
            year = now.get(Calendar.YEAR);
        }
        else {

        String strArrtmp[]=birth.split("/");
         day=Integer.parseInt(strArrtmp[0]);
         month=Integer.parseInt(strArrtmp[1])-1;
         year=Integer.parseInt(strArrtmp[2]);
        }
        DatePickerDialog pic=new DatePickerDialog(
                RegisterActivity.this,
                callback, year, month, day);
        pic.setTitle("Chọn ngày sinh");
        pic.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_btnback:
                AlertDialog.Builder b = new AlertDialog.Builder(getApplicationContext());
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
        }
    }
}
