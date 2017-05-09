package com.example.nam.minisn.Util;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.nam.minisn.Fragmen.FragmenFriend;
import com.example.nam.minisn.ItemListview.Chat;
import com.example.nam.minisn.ItemListview.Conversation;
import com.example.nam.minisn.ItemListview.Friend;
import com.example.nam.minisn.ItemListview.ItemDeleteFriend;
import com.example.nam.minisn.ItemListview.SearchFriendItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Nam on 4/20/2017.
 */

public class SQLiteDataController extends SQLiteOpenHelper {
    public String DB_PATH = "//data//data//%s//databases//";
    private static String DB_NAME = "minisn.sqlite";

    private SQLiteDatabase database;
    private final Context mContext;

    public SQLiteDataController(Context con) {
        super(con, DB_NAME, null, 1);
        DB_PATH = String.format(DB_PATH, con.getPackageName());
        this.mContext = con;
        try {
            isCreatedDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * copy database from assets to the device if not existed
     *
     * @return true if not exist and create database success
     * @throws IOException
     */
    public boolean isCreatedDatabase() throws IOException {
        // Default là đã có DB
        boolean result = true;
        // Nếu chưa tồn tại DB thì copy từ Asses vào Data
        if (!checkExistDataBase()) {
            this.getReadableDatabase();
            try {
                copyDataBase();
                result = false;
            } catch (Exception e) {
                throw new Error("Error copying database");
            }
        }

        return result;
    }

    /**
     * check whether database exist on the device?
     *
     * @return true if existed
     */
    private boolean checkExistDataBase() {

        try {
            String myPath = DB_PATH + DB_NAME;
            File fileDB = new File(myPath);

            if (fileDB.exists()) {
                return true;
            } else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * copy database from assets folder to the device
     *
     * @throws IOException
     */
    private void copyDataBase() throws IOException {
        InputStream myInput = mContext.getAssets().open(DB_NAME);
        OutputStream myOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    /**
     * delete database file
     *
     * @return
     */
    public boolean deleteDatabase() {
        File file = new File(DB_PATH + DB_NAME);
        if (file != null && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * open database
     *
     * @throws SQLException
     */
    public void openDataBase() throws SQLException {
        database = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null,
                SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if (database != null)
            database.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // do nothing
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do nothing
    }

    public int deleteData_From_Table(String tbName) {

        int result = 0;
        try {
            openDataBase();
            database.beginTransaction();
            result = database.delete(tbName, null, null);
            if (result >= 0) {
                database.setTransactionSuccessful();
            }
        } catch (Exception e) {
            database.endTransaction();
            close();
        } finally {
            database.endTransaction();
            close();
        }

        return result;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public void setDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    public void saveMessage(String message, int idConversation, int idSend, int useID) {
//        String time = Const.getTimeNow();
        long time = System.currentTimeMillis() / 1000;
        String sql = Const.INSERT +

                Const.DB_DATA_CONVERSATION +
                " (" +
                Const.DATA_CONVERSATION_COL1 +
                "," +
                Const.DATA_CONVERSATION_COL2 +
                "," +
                Const.DATA_CONVERSATION_COL3 +
                "," +
                Const.DATA_CONVERSATION_COL4 +
                "," +
                Const.DATA_CONVERSATION_COL5 +
                ")" +
                Const.VALUES +
                "('" +
                idConversation +
                "','" +
                idSend +
                "','" +
                message +
                "','" +
                time +
                "','" +
                useID +
                "')";
        database.execSQL(sql);
        int type = idSend == useID ? Const.TYPE_DONT_NEW_MESSAGE : Const.TYPE_NEW_MESSAGE;
        updateConversation(message, time, idConversation, type);
    }

    public boolean isExistConversation(int idConversation) {
        String sql = Const.SELECT +
                " * " +
                Const.FROM +
                Const.DB_CONVERSATION +
                Const.WHERE +
                Const.CONVERSATION_COL2 +
                "='" +
                idConversation +
                "'";
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }

    public void addConversation(int idConversation, String nameConversation, String lastMessage, int use_id, int is_new_message) {
//        String time = Const.getTimeNow();
        long time = System.currentTimeMillis() / 1000;
        String sql = Const.INSERT +
                Const.DB_CONVERSATION +
                " (" +
                Const.CONVERSATION_COL1 +
                "," +
                Const.CONVERSATION_COL2 +
                "," +
                Const.CONVERSATION_COL3 +
                "," +
                Const.CONVERSATION_COL4 +
                "," +
                Const.CONVERSATION_COL5 +
                "," +
                Const.CONVERSATION_COL6 +
                ")" +
                Const.VALUES +
                "('" +
                nameConversation +
                "','" +
                idConversation +
                "','" +
                lastMessage +
                "','" +
                time +
                "','" +
                use_id +
                "','" +
                is_new_message +
                "')";
        database.execSQL(sql);
    }

    public boolean checkLogged(int id) {
        Cursor cursor = database.rawQuery(Const.SELECT + "*" + Const.FROM + Const.DB_USERS_SAVE + Const.WHERE +
                Const.SAVE_COL1 + " = '" + id + "'", null);
        return cursor.getCount() > 0;
    }

    public void saveAccount(int id, String username) {
        String sql = Const.INSERT +
                Const.DB_USERS_SAVE +
                " (" +
                Const.SAVE_COL1 +
                "," +
                Const.SAVE_COL2 +
                ")" +
                Const.VALUES +
                "('" +
                id +
                "','" +
                username +
                "')";
        database.execSQL(sql);
    }

    public void insertListFriend(int fri_id, String fri_username, int useId, int gender, int id) {
        String sql = Const.INSERT +
                Const.DB_FRIEND +
                " (" +
                Const.FRIENDS_COL0 +
                "," +
                Const.FRIENDS_COL1 +
                "," +
                Const.FRIENDS_COL2 +
                "," +
                Const.FRIENDS_COL4 +
                "," +
                Const.FRIENDS_COL5 +
                ")" +
                Const.VALUES +
                "('" +
                id +
                "','" +
                fri_id +
                "','" +
                fri_username +
                "','" +
                useId +
                "','" +
                gender +
                "')";
        database.execSQL(sql);
    }

    public void insertConversation(int id, String name, int use_id, int size) {
        String sql = Const.INSERT +
                Const.DB_CONVERSATION +
                " (" +
                Const.CONVERSATION_COL1 +
                "," +
                Const.CONVERSATION_COL2 +
                "," +
                Const.CONVERSATION_COL5 +
                "," +
                Const.CONVERSATION_COL7 +
                ")" +
                Const.VALUES +
                "('" +
                name +
                "','" +
                id +
                "','" +
                use_id +
                "','" +
                size +
                "')";
        database.execSQL(sql);
    }

    public void updateConversation(String lastMessage, long time, int idConversation, int type) {
        int use_id = SharedPrefManager.getInstance(mContext).getInt(Const.ID);
        String sql = Const.UPDATE +
                Const.DB_CONVERSATION +
                Const.SET +
                Const.CONVERSATION_COL3 +
                "='" +
                lastMessage +
                "'," +
                Const.CONVERSATION_COL4 +
                "='" +
                time +
                "'," +
                Const.CONVERSATION_COL6 +
                "='" +
                type +
                "'" +
                Const.WHERE +
                Const.CONVERSATION_COL2 +
                "='" +
                idConversation +
                "'" +
                Const.AND +
                Const.CONVERSATION_COL5 +
                "='" +
                use_id +
                "'";
        database.execSQL(sql);

    }

    public void setNewMessageConversation(int idConversation, int use_id) {
        String sql = Const.UPDATE +
                Const.DB_CONVERSATION +
                Const.SET +
                Const.CONVERSATION_COL6 +
                "='" +
                Const.TYPE_DONT_NEW_MESSAGE +
                "'" +
                Const.WHERE +
                Const.CONVERSATION_COL2 +
                "='" +
                idConversation +
                "'" +
                Const.AND +
                Const.CONVERSATION_COL5 +
                "='" +
                use_id +
                "'";
        database.execSQL(sql);
    }

    public void addIdConversationIntoFriend(int use_id, int id_conversation, int fri_id) {
        String sql = Const.UPDATE +
                Const.DB_FRIEND +
                Const.SET +
                Const.FRIENDS_COL6 +
                "='" +
                id_conversation +
                "'" +
                Const.WHERE +
                Const.FRIENDS_COL4 +
                "='" +
                use_id +
                "'" +
                Const.AND +
                Const.FRIENDS_COL1 +
                "='" +
                fri_id +
                "'";

        database.execSQL(sql);
    }

    public boolean isExistFriend(int useId, int idFriend) {
        String sql = Const.SELECT +
                "*" +
                Const.FROM +
                Const.DB_FRIEND +
                Const.WHERE +
                Const.FRIENDS_COL1 +
                "='" +
                idFriend +
                "'" +
                Const.AND +
                Const.FRIENDS_COL4 +
                "='" +
                useId +
                "'";
        Cursor cursor = database.rawQuery(sql, null);
        return cursor.getCount() > 0;

    }

    public void updateSizeConversation(int idConversation, int use_id, int size) {
        String sql = Const.UPDATE +
                Const.DB_CONVERSATION +
                Const.SET +
                Const.CONVERSATION_COL7 +
                "='" +
                size +
                "'" +
                Const.WHERE +
                Const.CONVERSATION_COL5 +
                "='" +
                use_id +
                "'" +
                Const.AND +
                Const.CONVERSATION_COL2 +
                "='" +
                idConversation +
                "'";
        if (isExistConversation(idConversation))
            database.execSQL(sql);
    }

    public ArrayList<Conversation> searchConversation(String name, int useId) {
        ArrayList<Conversation> data = new ArrayList<Conversation>();
        String sql = Const.SELECT + " * " +
                Const.FROM +
                Const.DB_CONVERSATION +
                Const.WHERE +
                Const.CONVERSATION_COL1 +
                Const.LIKE +
                " '%" +
                name +
                "%'" +
                Const.AND +
                Const.CONVERSATION_COL5 +
                "='" +
                useId +
                "'" +
                Const.ORDER_BY +
                Const.CONVERSATION_COL1 +
                Const.ASC;
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int idConversation = cursor.getInt(2);
            String nameConversation = cursor.getString(1);
            String lastMessage = cursor.getString(3);
            long time = cursor.getLong(4);
            boolean isNew = cursor.getInt(6) == 1;
            Conversation conversation = new Conversation(idConversation, nameConversation, lastMessage, time, isNew, Const.TYPE_NO_CHOOSE);
            data.add(conversation);
        }

        return data;
    }

    public void saveRequestFriend(int useId, String username, int id) {

        String sql = Const.INSERT +
                Const.DB_REQUEST_FRIEND +
                " (" +
                Const.REQUEST_FRIEND_COL1 +
                "," +
                Const.REQUEST_FRIEND_COL2 +
                "," +
                Const.REQUEST_FRIEND_COL3 +
                ")" +
                Const.VALUES +
                "('" +
                id +
                "','" +
                username +
                "','" +
                useId +
                "')";
        database.execSQL(sql);
    }

    public ArrayList<Friend> getListRequestFriend(int useId) {
        ArrayList<Friend> data = new ArrayList<>();
        String sql = Const.SELECT +
                " * " +
                Const.FROM +
                Const.DB_REQUEST_FRIEND +
                Const.WHERE +
                Const.REQUEST_FRIEND_COL3 +
                "='" +
                useId +
                "'";
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(1);
            String username = cursor.getString(2);
            data.add(new Friend(id, username));
        }
        return data;
    }

    public int getCountRequestFriend(int useId) {
        String sql = Const.SELECT +
                " * " +
                Const.FROM +
                Const.DB_REQUEST_FRIEND +
                Const.WHERE +
                Const.REQUEST_FRIEND_COL3 +
                "='" +
                useId +
                "'";
        Cursor cursor = database.rawQuery(sql, null);
        return cursor.getCount();
    }

    public int getSizeConversation(int conversationId) {
        int size = 0;
        String sql = Const.SELECT +
                Const.CONVERSATION_COL7 +
                Const.FROM +
                Const.DB_CONVERSATION +
                Const.WHERE +
                Const.CONVERSATION_COL2 +
                "='" +
                conversationId +
                "'";
        Cursor cursor = database.rawQuery(sql, null);
//        size = cursor.getInt(0);
        cursor.moveToFirst();
        return size;
    }

    public void updateChooseConversation(int useId, int idConversation, int choose) {
        String sql = Const.UPDATE +
                Const.DB_CONVERSATION +
                Const.SET +
                Const.CONVERSATION_COL8 +
                "='" +
                choose +
                "'" +
                Const.WHERE +
                Const.CONVERSATION_COL2 +
                "='" +
                idConversation +
                "'" +
                Const.AND +
                Const.CONVERSATION_COL5 +
                "='" +
                useId +
                "'";
        database.execSQL(sql);
    }

    public void updateChooseFriend(int useId, int idFriend, int choose) {
        String sql = Const.UPDATE +
                Const.DB_FRIEND +
                Const.SET +
                Const.FRIENDS_COL7 +
                "='" +
                choose +
                "'" +
                Const.WHERE +
                Const.FRIENDS_COL1 +
                "='" +
                idFriend +
                "'" +
                Const.AND +
                Const.FRIENDS_COL4 +
                "='" +
                useId +
                "'";
        database.execSQL(sql);
    }

    public void setAllChooseConversation(int choose) {
        String sql = Const.UPDATE +
                Const.DB_CONVERSATION +
                Const.SET +
                Const.CONVERSATION_COL8 +
                "='" +
                choose +
                "'";
        database.execSQL(sql);
    }

    public void setAllChooseFriend(int choose) {
        String sql = Const.UPDATE +
                Const.DB_FRIEND +
                Const.SET +
                Const.FRIENDS_COL7 +
                "='" +
                choose +
                "'";
        database.execSQL(sql);
    }

    public int getCountChooseConversation() {
        String sql = Const.SELECT +
                Const.CONVERSATION_COL8 +
                Const.FROM +
                Const.DB_CONVERSATION +
                Const.WHERE +
                Const.CONVERSATION_COL8 +
                "='" +
                Const.TYPE_CHOOSE +
                "'" +
                Const.AND +
                Const.CONVERSATION_COL9 +
                "='" +
                Const.TYPE_ACTIVE +
                "'";
        Cursor cursor = database.rawQuery(sql, null);
        return cursor.getCount();
    }

    public int getCountChooseFriend() {
        String sql = Const.SELECT +
                Const.FRIENDS_COL7 +
                Const.FROM +
                Const.DB_FRIEND +
                Const.WHERE +
                Const.FRIENDS_COL7 +
                "='" +
                Const.TYPE_CHOOSE +
                "'";
        Cursor cursor = database.rawQuery(sql, null);
        return cursor.getCount();
    }

    public void setActive(int active, int idConversation, int useId) {
        String sql = Const.UPDATE +
                Const.DB_CONVERSATION +
                Const.SET +
                Const.CONVERSATION_COL8 +
                "='" +
                active +
                "'" +
                Const.WHERE +
                Const.CONVERSATION_COL2 +
                "='" +
                idConversation +
                "'" +
                Const.AND +
                Const.CONVERSATION_COL5 +
                "='" +
                useId +
                "'";
        database.execSQL(sql);
    }

    public void deleteMessageInConversation(int idConversation, int useId) {
        String sql = Const.DELETE +
                Const.FROM +
                Const.DB_DATA_CONVERSATION +
                Const.WHERE +
                Const.DATA_CONVERSATION_COL1 +
                "='" +
                idConversation +
                "'" +
                Const.AND +
                Const.DATA_CONVERSATION_COL5 +
                "='" +
                useId +
                "'";
        database.execSQL(sql);
        updateConversation("", 0, idConversation, Const.TYPE_DONT_NEW_MESSAGE);
        updateActiveConversation(idConversation, useId, Const.TYPE_DONT_ACTIVE);
        setNewMessageConversation(idConversation, useId);
    }

    public void updateActiveConversation(int idConversation, int useId, int active) {
        String sql = Const.UPDATE +
                Const.DB_CONVERSATION +
                Const.SET +
                Const.CONVERSATION_COL9 +
                "='" +
                active +
                "'" +
                Const.WHERE +
                Const.CONVERSATION_COL2 +
                "='" +
                idConversation +
                "'" +
                Const.AND +
                Const.CONVERSATION_COL5 +
                "='" +
                useId +
                "'";
        database.execSQL(sql);
    }

    public ArrayList<Integer> getListIdDelete() {
        ArrayList<Integer> data = new ArrayList<>();
        String sql = Const.SELECT +
                Const.CONVERSATION_COL2 +
                Const.FROM +
                Const.DB_CONVERSATION +
                Const.WHERE +
                Const.CONVERSATION_COL8 +
                "='" +
                Const.TYPE_CHOOSE +
                "'";
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            data.add(cursor.getInt(cursor.getColumnIndex(Const.CONVERSATION_COL2)));
        }
        return data;
    }

    public ArrayList<ItemDeleteFriend> searchFriends(String name, int useId) {
        ArrayList<ItemDeleteFriend> data = new ArrayList<>();
        String sql = Const.SELECT + " * " +
                Const.FROM +
                Const.DB_FRIEND +
                Const.WHERE +
                Const.FRIENDS_COL2 +
                Const.LIKE +
                " '%" +
                name +
                "%'" +
                Const.AND +
                Const.FRIENDS_COL4 +
                "='" +
                useId +
                "'" +
                Const.ORDER_BY +
                Const.FRIENDS_COL2 +
                Const.ASC;
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int idFriend = cursor.getInt(1);
            String nameFriend = cursor.getString(2);
            int gender = cursor.getInt(5);
            String displayName = cursor.getString(3);
            int choose = cursor.getInt(7);
            Friend friend = new Friend(idFriend, nameFriend, displayName);
            data.add(new ItemDeleteFriend(friend, FragmenFriend.isDelete(), choose));
        }

        return data;
    }

    public ArrayList<Chat> getDataConversation(int idConversation, int useId, int start) {
        ArrayList<Chat> data = new ArrayList<>();
        int end = start + Const.NUMBER_PAGE_CONVERSATION;
//        int size = getSizeDataConversation(idConversation, useId);
//        Log.d(Const.TAG,"size: "+size);
//        if (end > size)
//            end = size;
        String sql = Const.SELECT +
                " * " +
                Const.FROM +
                Const.DB_DATA_CONVERSATION +
                Const.WHERE +
                Const.DATA_CONVERSATION_COL1 +
                "='" +
                idConversation +
                "'" +
                Const.AND +
                Const.DATA_CONVERSATION_COL5 +
                "='" +
                useId +
                "'" +
                Const.ORDER_BY +
                Const.DATA_CONVERSATION_COL4 +
                Const.DESC +
                Const.LIMIT +
                start +
                "," +
                end;
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor.getCount() > 0) {

            cursor.moveToLast();
            do {
                String message = cursor.getString(3);
                int idSend = cursor.getInt(2);
                int gender = 0;
                int typeMessage;
                if (idSend != useId) {
                    gender = getGenderFriend(idSend);
                    typeMessage = Const.MESSAGE_RECEIVE;
                } else {

                    gender = 0;
                    typeMessage = Const.MESSAGE_SEND;
                }
                Chat chat = new Chat(typeMessage, message, gender);
                data.add(chat);
            } while (cursor.moveToPrevious());
        }
        return data;
    }

    public int getGenderFriend(int friId) {
        String sql = Const.SELECT +
                Const.FRIENDS_COL5 +
                Const.FROM +
                Const.DB_FRIEND +
                Const.WHERE +
                Const.FRIENDS_COL1 +
                "='" +
                friId +
                "'";
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex(Const.FRIENDS_COL5));
    }

    public int getSizeDataConversation(int idConversation, int useId) {
        String sql = Const.SELECT +
                "*" +
                Const.FROM +
                Const.DB_DATA_CONVERSATION +
                Const.WHERE +
                Const.DATA_CONVERSATION_COL2 +
                "='" +
                idConversation +
                "'" +
                Const.AND +
                Const.DATA_CONVERSATION_COL5 +
                "='" +
                useId +
                "'";
        return database.rawQuery(sql, null).getCount();
    }

    public int getConversationFriend(int idFriend, int useId) {
        String sql = Const.SELECT +
                Const.FRIENDS_COL6 +
                Const.FROM +
                Const.DB_FRIEND +
                Const.WHERE +
                Const.FRIENDS_COL1 +
                "='" +
                idFriend +
                "'" +
                Const.AND +
                Const.FRIENDS_COL4 +
                "='" +
                useId +
                "'";
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();

        return cursor.getInt(0);
    }

    public String getNameConversation(int idConversation, int useId) {
        String sql = Const.SELECT +
                Const.CONVERSATION_COL1 +
                Const.FROM +
                Const.DB_CONVERSATION +
                Const.WHERE +
                Const.CONVERSATION_COL2 +
                "='" +
                idConversation +
                "'" +
                Const.AND +
                Const.CONVERSATION_COL5 +
                "='" +
                useId +
                "'";
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        String name = cursor.getString(0);
        return name;
    }

    //    public void deleteRequest(){
//        String sql = "delete from "+
//                Const.DB_REQUEST_FRIEND;
//        database.execSQL(sql);
//    }
    public void addFriend(int useId, int friGender, String friUsername, int friId, int id) {
        String sql = Const.INSERT +
                Const.DB_FRIEND +
                " (" +
                Const.FRIENDS_COL1 +
                "," +
                Const.FRIENDS_COL1 +
                "," +
                Const.FRIENDS_COL2 +
                "," +
                Const.FRIENDS_COL4 +
                "," +
                Const.FRIENDS_COL5 +
                ")" +
                Const.VALUES +
                "('" +
                id +
                "','" +
                friId +
                "','" +
                friUsername +
                "','" +
                useId +
                "','" +
                friGender +
                "')";
        database.execSQL(sql);
    }

    public void deleteRequestFriend(int useId, int idRequest) {
        String sql = Const.DELETE +
                Const.FROM +
                Const.DB_REQUEST_FRIEND +
                Const.WHERE +
                Const.REQUEST_FRIEND_COL1 +
                "='" +
                idRequest +
                "'" +
                Const.AND +
                Const.REQUEST_FRIEND_COL3 +
                "='" +
                useId +
                "'";
        database.execSQL(sql);
    }

    public ArrayList<Integer> getListFriendChoose() {
        ArrayList<Integer> listId = new ArrayList<>();
        String sql = Const.SELECT +
                Const.FRIENDS_COL0 +
                Const.FROM +
                Const.DB_FRIEND +
                Const.WHERE +
                Const.FRIENDS_COL7 +
                "='" +
                Const.TYPE_CHOOSE +
                "'";
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(Const.FRIENDS_COL0));
            listId.add(id);
        }
//        Log.d(Const.TAG,"count: "+cursor.getCount());
        return listId;
    }

    public void deleteFriend(int id, int useId) {
        String sql = Const.DELETE +
                Const.FROM +
                Const.DB_FRIEND +
                Const.WHERE +
                Const.FRIENDS_COL0 +
                "='" +
                id +
                "'" +
                Const.AND +
                Const.FRIENDS_COL4 +
                "='" +
                useId +
                "'";
        database.execSQL(sql);
    }

    public boolean isExistWaitResponse(int useId, int friId) {
        String sql = Const.SELECT +
                "*" +
                Const.FROM +
                Const.DB_WAIT_RESPONSE +
                Const.WHERE +
                Const.RESPONSE_COL1 +
                "='" +
                useId +
                "'" +
                Const.AND +
                Const.RESPONSE_COL2 +
                "='" +
                friId +
                "'";
        Cursor cursor = database.rawQuery(sql, null);
        return cursor.getCount() > 0;
    }

    public void addWaitResponse(int useId, int friId,String username) {
        String sql = Const.INSERT +
                Const.DB_WAIT_RESPONSE +
                " (" +
                Const.RESPONSE_COL1 +
                "," +
                Const.RESPONSE_COL2 +
                "," +
                Const.RESPONSE_COL3 +
                ")" +
                Const.VALUES +
                "('" +
                useId +
                "','" +
                friId +
                "','" +
                username +
                "')";
        if (!isExistWaitResponse(useId, friId))
            database.execSQL(sql);
    }
    public boolean isExistRequest(int useId, int friId){
        String sql = Const.SELECT +
                "*" +
                Const.FROM +
                Const.DB_REQUEST_FRIEND +
                Const.WHERE +
                Const.REQUEST_FRIEND_COL1 +
                "='" +
                friId +
                "'" +
                Const.AND +
                Const.REQUEST_FRIEND_COL3 +
                "='" +
                useId +
                "'";
        Cursor cursor = database.rawQuery(sql, null);
        return cursor.getCount() > 0;
    }

    public ArrayList<SearchFriendItem> getListWaitResponse(int useId){
        ArrayList<SearchFriendItem> data = new ArrayList<>();
        String sql = Const.SELECT+
                "*"+
                Const.FROM+
                Const.DB_WAIT_RESPONSE+
                Const.WHERE+
                Const.RESPONSE_COL1+
                "='"+
                useId+
                "'";
        Cursor cursor = database.rawQuery(sql,null);
        while (cursor.moveToNext()){
            int id = cursor.getInt(2);
            String username = cursor.getString(3);
            SearchFriendItem item = new SearchFriendItem(new Friend(id,username), true);
            data.add(item);
        }
        return data;
    }

    public void deleteWaitResponse (int useId,int friId){
        String sql = Const.DELETE+
                Const.FROM+
                Const.DB_WAIT_RESPONSE+
                Const.WHERE+
                Const.RESPONSE_COL1 +
                "='" +
                useId +
                "'" +
                Const.AND +
                Const.RESPONSE_COL2 +
                "='" +
                friId +
                "'";
        if (isExistWaitResponse(useId, friId))
            database.execSQL(sql);

    }
}
