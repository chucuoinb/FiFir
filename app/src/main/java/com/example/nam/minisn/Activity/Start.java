package com.example.nam.minisn.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SharedPrefManager;

public class Start extends AppCompatActivity {
    private Intent intent;
    private ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_start);

        init();
    }
    public void init(){
        logo = (ImageView)findViewById(R.id.start_logo);
        logo.setImageResource(R.drawable.logo);
//        int index =10000;
//        while ((--index) >0){
//
//        };
        if (SharedPrefManager.getInstance(getApplicationContext()).getInt(Const.IS_LOGIN) != Const.LOGIN){
            intent = new Intent(Start.this,LoginActivity.class);
        }
        else{
            intent = new Intent(Start.this,Main.class);
            Bundle bundle= new Bundle();
            bundle.putString(Const.TOKEN,SharedPrefManager.getInstance(getApplicationContext()).getString(Const.TOKEN));
            bundle.putString(Const.USERNAME,SharedPrefManager.getInstance(getApplicationContext()).getString(Const.USERNAME));
            intent.putExtra(Const.PACKAGE,bundle);
        }
        startActivity(intent);

    }
}
