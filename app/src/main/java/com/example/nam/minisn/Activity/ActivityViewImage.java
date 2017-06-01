package com.example.nam.minisn.Activity;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;

public class ActivityViewImage extends AppCompatActivity {
    private ImageView imgImage;
    private LinearLayout btnBack;
    private TextView toolbarText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_avatar);
        init();
    }

    public void init() {
        toolbarText = (TextView) findViewById(R.id.toolbar_text);
        btnBack = (LinearLayout) findViewById(R.id.toolbar_btnback);
        imgImage = (ImageView) findViewById(R.id.view_img_avatar);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = Const.URL + "uploads/149546930630l.jpg";
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imgImage.setImageBitmap(response);
                    }
                },0,0,null,
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(Const.TAG,"err");
                    }
                });
        requestQueue.add(request);
    }
}
