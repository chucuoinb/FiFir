package com.example.nam.minisn.Activity;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SQLiteDataController;

import java.io.IOException;

public class testDB extends AppCompatActivity {
    private TextView tv;
    private Button bt;
//    private
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_lvconversation);
//        tv =(TextView)findViewById(R.id.textView2);
//        bt =(Button)findViewById(R.id.button2);
//        createDB();
    }

    private void createDB() {
// khởi tạo database
            Log.d(Const.TAG,"success");
        SQLiteDataController sql = new SQLiteDataController(getBaseContext());
        try {
            sql.isCreatedDatabase();
            sql.openDataBase();
            Cursor cursor = sql.getDatabase().rawQuery("select * from conversation",null);
            tv.setText(String.valueOf(cursor.getColumnName(0)));
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(Const.TAG,e.getMessage());
        }
    }
}
