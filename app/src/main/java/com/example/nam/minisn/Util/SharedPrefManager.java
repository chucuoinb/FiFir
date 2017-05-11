package com.example.nam.minisn.Util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Nam on 2/19/2017.
 */

public class SharedPrefManager
{
    private static SharedPrefManager mInstance;
    private static Context mCtx;
    private SharedPrefManager(Context context) {
        mCtx = context;
    }
    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //this method will save the device token to shared preferences
    public boolean savePreferences(String tag, String data){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Const.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(tag, data);
        editor.apply();
        return true;
    }

    public boolean savePreferences(String tag, int data){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Const.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(tag, data);
        editor.apply();
        return true;
    }

    public boolean savePreferences(String tag, long data){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Const.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(tag, data);
        editor.apply();
        return true;
    }

    //this method will fetch the device token from shared preferences
    public String getString(String tag){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Const.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(tag, null);
    }

    public int getInt(String tag){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Const.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getInt(tag, 0);
    }

}
