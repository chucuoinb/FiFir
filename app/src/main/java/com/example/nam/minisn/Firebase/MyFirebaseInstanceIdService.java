package com.example.nam.minisn.Firebase;
import android.util.Log;

import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SharedPrefManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Nam on 2/19/2017.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {

        super.onTokenRefresh();
        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat

        //calling the method store token and passing token
        storeToken(refreshedToken);
    }

    private void storeToken(String token) {
        //we will save the token in sharedpreferences later
        Log.d(Const.TAG,"token:"+token);
        SharedPrefManager.getInstance(getApplicationContext()).savePreferences(Const.FCM_TOKEN,token);
    }
}
