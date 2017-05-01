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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private RemoteViews mContentView;
    private int idSend, use_id;
    private SQLiteDataController database ;

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        database = new SQLiteDataController(getApplicationContext());
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
            switch (jsonObject.getInt(Const.CODE)) {
                case Const.TYPE_MESSAGE:
                    receiveMessage(jsonObject);
                    break;
                case Const.TYPE_REQUEST_FRIEND:
                    solveRequestFriend();
                    break;
                case Const.TYPE_RESPONSE_FRIEND:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage(JSONObject json) {
        try {
            JSONObject jsonObject = json.getJSONObject(Const.DATA);
            int idConversation = jsonObject.getInt(Const.CONVERSATION_ID);
            String usernameSend = jsonObject.getString(Const.USERNAME_SEND);
            String message = json.getString(Const.MESSAGE);
            String nameConversation = jsonObject.getString(Const.NAME_CONVERSATION);
            use_id = SharedPrefManager.getInstance(getApplicationContext()).getInt(Const.ID);
            idSend = jsonObject.getInt(Const.ID_USERNAME);

            database.openDataBase();
            if (!database.isExistConversation(idConversation)) {
                database.addConversation(idConversation, nameConversation, message, use_id, Const.TYPE_NEW_MESSAGE);
                getSizeConversation(idConversation);
            }
            database.saveMessage(message, idConversation, idSend);
            database.close();
            if (idConversation == SharedPrefManager.getInstance(getApplicationContext()).getInt(Const.CONVERSATION_ID)) {
                displayMessageOnScreen(getApplicationContext(), message);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(Const.MESSAGE, message);
                bundle.putString(Const.NAME_CONVERSATION, nameConversation);
                bundle.putString(Const.USERNAME_SEND, usernameSend);
                bundle.putInt(Const.CONVERSATION_ID, idConversation);
                pushNotifyMessage(bundle);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //send broad cast receive
    public static void displayMessageOnScreen(Context context, String message) {

        Intent intent = new Intent(Const.DISPLAY_MESSAGE_ACTION);
        intent.putExtra(Const.MESSAGE, message);


        context.sendBroadcast(intent);

    }

    public void pushNotifyMessage(Bundle bundle) {
        int icon = R.drawable.icon_notify;
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
        manager.notify(Const.ID_NOTIFICATION, notification.build());

    }

    public void solveRequestFriend() {
        int icon = R.drawable.icon_notify;
        String title = getResources().getString(R.string.notify_title_message);
        String content = getResources().getString(R.string.notify_content_message);
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
        manager.notify(Const.ID_NOTIFICATION, notification.build());
    }

    public void getSizeConversation(final int idConversation) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, Const.URL_GET_SIZE_CONVERSATION + "/?" +
                Const.CONVERSATION_ID + "=" + idConversation
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (jsonObject.getInt(Const.CODE) == Const.CODE_OK) {
                        int size = jsonObject.getInt(Const.DATA);
                        database.updateSizeConversation(idConversation, use_id, size);
                        if (size == 2) {
                            database.addIdConversationIntoFriend(use_id, idConversation, idSend);
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
