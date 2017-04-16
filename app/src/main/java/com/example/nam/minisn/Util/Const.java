package com.example.nam.minisn.Util;

import android.util.Log;

/**
 * Created by Nam on 2/19/2017.
 */

public class Const {
    //Const string

    public static final String DISPLAY_MESSAGE_ACTION= "display_action";

    public static final String FAIL = "fail";
    public static final String GENDER = "gender";
    public static final String KEY_JSON_MESSAGE = "package";
    public static final String ID = "id";


    public static final String STATUS = "status";
    public static final String SUCCESS = "success";
    public static final String TAG = "tag_nam";

    public static final String USERNAME_SEND = "username_send";

    //Key upload
    public static final String AVATAR = "avatar";
    public static final String PACKAGE = "package";
    public static final String PASSWORD = "password";
    public static final String CODE = "code";
    public static final String CONVERSATION_ID = "conversation_id";
    public static final String DATA = "data";
    public static final String LIST_USER = "list_user";
    public static final String MESSAGE = "message";
    public static final String NAME_CONVERSATION = "name_conversation";
    public static final String DISPLAY_NAME = "displayname";
    public static final String TOKEN = "token";
    public static final String USERNAME = "username";
    //URL
    public static final String URL = "http://www.namlv.hol.es/ver1/";
    public static final String URL_LOGIN = URL + "user/login.php";
    public static final String URL_GET_LIST_CONVERSATION = URL + "conversation/get_list_conversation.php";
    public static final String URL_GET_LIST_FRIEND = URL + "friend/get_list_friend.php";
    public static final String URL_UPLOAD_AVATAR = URL + "user/upload_avatar.php";
    public static final String URL_DOWNLOAD_AVATAR = URL+ "user/download_avatar.php";
    public static final String URL_SEND_MESSAGE = URL +"conversation/send_message.php";

    //Share preferen
    public static final String FCM_TOKEN = "fcm_token";
    public static final String SHARED_PREF_NAME = "Preferences";

    //Code responce
    public static final int REQUEST_CODE_REGISTER =100;
    public static final int CODE_OK =  101;
    public static final int CODE_FAIL =  102;
    public static final int CODE_EMAIL_EXIST =  103;
    public static final int CODE_USER_EXIST =  104;
    public static final int CODE_INVALID =  105;
    public static final int CODE_USER_NOT_EXIST =  106;

    //Gender
    public static final int GENDER_UNKNOW = 300;
    public static final int GENDER_MAN = 301;
    public static final int GENDER_WOMAN =302;

    //Message
    public  static final int MESSAGE_SEND =1;
    public static final int MESSAGE_RECEIVE = 0;

    //Type Json receive FCM
    public static final int TYPE_MESSAGE = 200;
    public static final int TYPE_REQUEST_FRIEND = 201;
    public static final int TYPE_RESPONSE_FRIEND = 202;


    public static final int ID_NOTIFICATION = 1111;
    //method
    public static final void log(String message){
        Log.d(Const.TAG,message);
    }

}
