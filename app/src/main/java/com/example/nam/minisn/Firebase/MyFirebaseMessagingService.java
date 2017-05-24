package com.example.nam.minisn.Firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nam.minisn.Activity.ChatActivity;
import com.example.nam.minisn.Activity.RequestFriendActivity;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SQLiteDataController;
import com.example.nam.minisn.Util.SharedPrefManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private RemoteViews mContentView;
    private int idSend, useId, idConversation;
    private SQLiteDataController database;

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                resolveMessageFcm(json);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(Const.TAG, "k co json");
            }
        }
    }

    //solve message FCM response
    public void resolveMessageFcm(JSONObject json) {
        try {
            JSONObject jsonObject = json.getJSONObject(Const.KEY_JSON_MESSAGE);
            Log.d(Const.TAG, "new request: " + jsonObject.getInt(Const.CODE));
            switch (jsonObject.getInt(Const.CODE)) {
                case Const.TYPE_MESSAGE:
                    receiveMessage(jsonObject);
                    break;
                case Const.TYPE_REQUEST_FRIEND:
                    solveRequestFriend(jsonObject);
                    break;
                case Const.TYPE_RESPONSE_FRIEND:
                    solveResponse(jsonObject);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage(JSONObject json) {
        try {
            JSONObject jsonObject = json.getJSONObject(Const.DATA);
            idConversation = jsonObject.getInt(Const.CONVERSATION_ID);
            String usernameSend = jsonObject.getString(Const.USERNAME_SEND);
            String message = json.getString(Const.MESSAGE);
            String nameConversation = jsonObject.getString(Const.NAME_CONVERSATION);
            useId = SharedPrefManager.getInstance(getApplicationContext()).getInt(Const.ID);
            idSend = jsonObject.getInt(Const.ID_USERNAME);
            database = new SQLiteDataController(getApplicationContext());
            database.openDataBase();
            int saveIdConversation=SharedPrefManager.getInstance(getApplicationContext()).getInt(Const.CONVERSATION_ID);
            if (!database.isExistConversation(idConversation)) {
                int type = (idConversation==saveIdConversation)?Const.TYPE_DONT_NEW_MESSAGE:Const.TYPE_NEW_MESSAGE;
                database.addConversation(idConversation, nameConversation, message, useId, type);
//                while (!database.isExistConversation(idConversation)) ;
            }
            getSizeConversation();
            database.saveMessage(message, idConversation, idSend, useId);
//            database.close();
            if (idConversation == saveIdConversation) {
                displayMessageOnScreen(getApplicationContext(), message,idSend);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(Const.MESSAGE, message);
                bundle.putString(Const.NAME_CONVERSATION, nameConversation);
                bundle.putString(Const.USERNAME_SEND, usernameSend);
                bundle.putInt(Const.CONVERSATION_ID, idConversation);
                bundle.putInt(Const.ID,idSend);
                pushNotifyMessage(bundle);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //send broad cast receive
    public static void displayMessageOnScreen(Context context, String message,int idSend) {

        Intent intent = new Intent(Const.DISPLAY_MESSAGE_ACTION);
        Bundle bundle = new Bundle();
        bundle.putInt(Const.ID,idSend);
        bundle.putString(Const.MESSAGE,message);
        intent.putExtra(Const.PACKAGE, bundle);


        context.sendBroadcast(intent);

    }

    public void pushNotifyMessage(Bundle bundle) {
        int icon = R.drawable.logo;
        String title = getResources().getString(R.string.notify_title_message);
        String content = getResources().getString(R.string.notify_content_message);
        Intent intentNotify = new Intent(getApplicationContext(), ChatActivity.class);
        intentNotify.putExtra(Const.PACKAGE, bundle);
        intentNotify.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pending = PendingIntent.getActivity(getApplicationContext(), 0, intentNotify, 0);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext()).
                setContentTitle(title).
                setContentText(content).
                setSmallIcon(icon).
                setContentIntent(pending).
                setAutoCancel(true);
        manager.notify(Const.ID_NOTIFICATION_MESSAGE, notification.build());

    }

    public void solveRequestFriend(JSONObject json) {
        try {
            useId = SharedPrefManager.getInstance(getApplicationContext()).getInt(Const.ID);
            JSONObject jsonObject = json.getJSONObject(Const.DATA);
            int idRequest = jsonObject.getInt(Const.ID_REQUEST);
            String usernameRequest = jsonObject.getString(Const.USERNAME_REQUEST);
            database = new SQLiteDataController(getApplicationContext());
            database.openDataBase();
            database.saveRequestFriend(useId, usernameRequest, idRequest);
            int icon = R.drawable.logo;
            String title = getResources().getString(R.string.notify_request_friend);
            String content = getResources().getString(R.string.notify_request_content);
            Intent intentNotify = new Intent(getApplicationContext(), RequestFriendActivity.class);
            intentNotify.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent pending = PendingIntent.getActivity(getApplicationContext(), 0, intentNotify, 0);
            NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext()).
                    setContentTitle(title).
                    setContentText(content).
                    setSmallIcon(icon).
                    setContentIntent(pending).
                    setAutoCancel(true);
            manager.notify(Const.ID_NOTIFICATION_REQUEST, notification.build());
        } catch (JSONException e) {
            Log.d(Const.TAG, "json err: " + e.getMessage());
        }
    }

    public void solveResponse(JSONObject json) {
        try {
            database = new SQLiteDataController(getApplicationContext());
            database.openDataBase();
            String content = "";
            String notify = getResources().getString(R.string.responce_notify);
            useId = SharedPrefManager.getInstance(getApplicationContext()).getInt(Const.ID);
            JSONObject jsonObject = json.getJSONObject(Const.DATA);
            int code = jsonObject.getInt(Const.CODE);
            int idFriend = jsonObject.getInt(Const.ID);
            database.deleteWaitResponse(useId, idFriend);
            String username = jsonObject.getString(Const.USERNAME);
            if (code == Const.CODE_ACCEPT) {
            int id = jsonObject.getInt("id_friend");
            int gender = jsonObject.getInt(Const.GENDER);
            String displayName = jsonObject.getString(Const.DISPLAY_NAME);
                database.addFriend(useId, gender, username, idFriend, id,displayName);
                content = username + " " + getResources().getString(R.string.responce_accept) + " " + notify;
            } else {
                content = username + " " + getResources().getString(R.string.responce_reject) + " " + notify;
            }
            int icon = R.drawable.logo;
            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext()).
                    setContentText(content).
                    setSmallIcon(icon).
                    setAutoCancel(true);
            manager.notify(Const.ID_NOTIFICATION_RESPONSE, notification.build());
        } catch (JSONException e) {
            Log.d(Const.TAG, "json err: " + e.getMessage());
        }
    }

    public void getSizeConversation() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, Const.URL_GET_SIZE_CONVERSATION + "?" +
                Const.CONVERSATION_ID + "=" + idConversation
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.getInt(Const.CODE) == Const.CODE_OK) {
                        int size = jsonObject.getInt(Const.DATA);
                        database = new SQLiteDataController(getApplicationContext());
                        database.openDataBase();
                        database.updateSizeConversation(idConversation, useId, size);
                        if (size == 2) {
                            if (database.isExistFriend(useId, idSend))
                                database.addIdConversationIntoFriend(useId, idConversation, idSend);
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
    }
}
