package com.example.nam.minisn.Util;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.nam.minisn.Activity.LoginActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public void saveMessage(String message, int idConversation, int idSend) {
//        String time = Const.getTimeNow();
        long time = System.currentTimeMillis()/1000;
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
                "')";
        Log.d(Const.TAG,sql);
        database.execSQL(sql);
        updateConversation(message,time,idConversation);
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
        long time = System.currentTimeMillis()/1000;
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

    public void insertListFriend(int fri_id, String fri_username, int id, int gender) {
//        Log.d(Const.TAG, fri_username + ":" + gender + ":" + fri_id + ":" + use_id);
        String sql = Const.INSERT +
                Const.DB_FRIEND +
                " (" +
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
                fri_id +
                "','" +
                fri_username +
                "','" +
                id +
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

    public void updateConversation(String lastMessage,long time,int idConversation){
        int use_id = SharedPrefManager.getInstance(mContext).getInt(Const.ID);
        String sql = Const.UPDATE +
                    Const.DB_CONVERSATION+
                Const.SET+
                Const.CONVERSATION_COL3+
                "='"+
                lastMessage+
                "',"+
                Const.CONVERSATION_COL4+
                "='"+
                time+
                "',"+
                Const.CONVERSATION_COL6+
                "='"+
                Const.TYPE_NEW_MESSAGE+
                "'"+
                Const.WHERE+
                Const.CONVERSATION_COL2+
                "='"+
                idConversation+
                "'"+
                Const.AND+
                Const.CONVERSATION_COL5+
                "='"+
                use_id+
                "'";
        Log.d(Const.TAG,sql);
        database.execSQL(sql);

    }
    public void setNewMessageConversation(int idConversation, int use_id){
        String sql = Const.UPDATE+
                Const.DB_CONVERSATION+
                Const.SET+
                Const.CONVERSATION_COL6+
                "='"+
                Const.TYPE_DONT_NEW_MESSAGE+
                "'"+
                Const.WHERE+
                Const.CONVERSATION_COL2 +
                "='"+
                idConversation+
                "'"+
                Const.AND+
                Const.CONVERSATION_COL5 +
                "='"+
                use_id+
                "'";
        database.execSQL(sql);
    }
    public void addIdConversationIntoFriend(int use_id,int id_conversation, int fri_id){
        String sql = Const.UPDATE+
                Const.DB_FRIEND+
                Const.SET+
                Const.FRIENDS_COL6+
                "='"+
                id_conversation+
                "'"+
                Const.WHERE+
                Const.FRIENDS_COL4+
                "='"+
                use_id+
                "'"+
                Const.AND+
                Const.FRIENDS_COL3+
                "='"+
                fri_id+
                "'";
        database.execSQL(sql);
    }

    public void updateSizeConversation(int idConversation,int use_id,int size){
        String sql = Const.UPDATE+
                Const.DB_CONVERSATION+
                Const.SET+
                Const.CONVERSATION_COL7+
                "='"+
                size+
                "'"+
                Const.WHERE+
                Const.CONVERSATION_COL5+
                "='"+
                use_id+
                "'"+
                Const.AND+
                Const.CONVERSATION_COL2+
                "='"+
                idConversation+
                "'";
        database.execSQL(sql);
    }
}
