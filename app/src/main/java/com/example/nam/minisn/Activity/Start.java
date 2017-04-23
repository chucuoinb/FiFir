package com.example.nam.minisn.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SharedPrefManager;

public class Start extends AppCompatActivity {
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPrefManager.getInstance(getApplicationContext()).getInt(Const.IS_LOGIN) != Const.LOGIN){
            Log.d(Const.TAG,"true");
            intent = new Intent(Start.this,LoginActivity.class);
        }
        else{
            Log.d(Const.TAG,"false");
            intent = new Intent(Start.this,Main.class);
            Bundle bundle= new Bundle();
            bundle.putString(Const.TOKEN,SharedPrefManager.getInstance(getApplicationContext()).getString(Const.TOKEN));
            bundle.putString(Const.USERNAME,SharedPrefManager.getInstance(getApplicationContext()).getString(Const.USERNAME));
            intent.putExtra(Const.PACKAGE,bundle);
        }
        startActivity(intent);
    }
}
