package com.example.nam.minisn.Firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.nam.minisn.Activity.ChatActivity;
import com.example.nam.minisn.Activity.RequestFriendActivity;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SharedPrefManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private RemoteViews mContentView;
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
            switch (jsonObject.getInt(Const.CODE)) {
                case Const.TYPE_MESSAGE:
                    receiveMessage(jsonObject);
                    break;
                case Const.TYPE_REQUEST_FRIEND:
                    Log.d(Const.TAG,"request friend");
                    solveRequestFriend();
                    break;
                case Const.TYPE_RESPONSE_FRIEND:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(Const.TAG, "k co json2");
        }
    }

    public void receiveMessage(JSONObject json) {
        try {
            JSONObject jsonObject = json.getJSONObject(Const.DATA);
            int idConversation = jsonObject.getInt(Const.CONVERSATION_ID);
            String usernameSend = jsonObject.getString(Const.USERNAME_SEND);
            String message = json.getString(Const.MESSAGE);
            String nameConversation = jsonObject.getString(Const.NAME_CONVERSATION);
            if (idConversation == SharedPrefManager.getInstance(getApplicationContext()).getInt(Const.CONVERSATION_ID)) {
                Log.d(Const.TAG, "message: " + message);
                displayMessageOnScreen(getApplicationContext(), message);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(Const.MESSAGE, message);
                bundle.putString(Const.NAME_CONVERSATION,nameConversation);
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
        intentNotify.putExtra(Const.PACKAGE,bundle);
        intentNotify.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        NotificationManager manager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pending = PendingIntent.getActivity(getApplicationContext(),0,intentNotify,0);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext()).
                setContentTitle(title).
                setContentText(content).
                setSmallIcon(icon).
                setContentIntent(pending).
                setAutoCancel(true);
        manager.notify(Const.ID_NOTIFICATION, notification.build());

    }

    public void solveRequestFriend(){
        int icon = R.drawable.icon_notify;
        String title = getResources().getString(R.string.notify_title_message);
        String content = getResources().getString(R.string.notify_content_message);
        Intent intentNotify = new Intent(getApplicationContext(), RequestFriendActivity.class);
        intentNotify.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        NotificationManager manager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pending = PendingIntent.getActivity(getApplicationContext(),0,intentNotify,0);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext()).
                setContentTitle(title).
                setContentText(content).
                setSmallIcon(icon).
                setContentIntent(pending).
                setAutoCancel(true);
        manager.notify(Const.ID_NOTIFICATION, notification.build());
    }
}
