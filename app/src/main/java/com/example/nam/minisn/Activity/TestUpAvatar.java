package com.example.nam.minisn.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nam.minisn.R;
import com.example.nam.minisn.UseVoley.CustomRequest;
import com.example.nam.minisn.Util.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class TestUpAvatar extends AppCompatActivity implements View.OnClickListener {
    private ImageView imgUp,imgDown;
    private Button btChoose;
    private Button btUp, btLoad;
    private Bitmap bitmap;
    private Uri filePath;
    private int PICK_IMAGE_REQUEST = 1;
    private String a;
    private LinearLayout ln;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test);
        imgUp = (ImageView) findViewById(R.id.imgUp);
        imgDown = (ImageView)findViewById(R.id.imgdown);
        btChoose = (Button) findViewById(R.id.btChoose);
        btUp = (Button) findViewById(R.id.btUp);
        ln = (LinearLayout)findViewById(R.id.ln_image);
        btLoad = (Button) findViewById(R.id.btLoad);
        btChoose.setOnClickListener(this);
        btUp.setOnClickListener(this);
        btLoad.setOnClickListener(this);
    }


    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
//            File file = icon_new File(filePath);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgUp.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String strImage = Base64.encodeToString(b, Base64.DEFAULT);
        return strImage;
    }

    public Bitmap convertBase64ToBitmap(String stringBitmap) {
        try {
            String pureBase64Encoded = stringBitmap.substring(stringBitmap.indexOf(",")  + 1);
            byte[] encodeByte = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
//            Log.d(Const.TAG,convertBitmapToBase64(bitmap));
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            Log.d(Const.TAG,e.getMessage());
            e.getMessage();
            return null;
        }
    }

    public void upLoadAvatar(Bitmap bmp){
        HashMap<String,String> params = new HashMap<>();
        params.put(Const.USERNAME,"admin");
        params.put(Const.AVATAR,convertBitmapToBase64(bmp));
//        Log.d(Const.TAG,convertBitmapToBase64(bmp));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//        final ProgressDialog dialog = icon_new ProgressDialog(getApplicationContext());
//        dialog.show();
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Const.URL + "test.php", params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(Const.TAG,"done");
//                        dialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        setEnableEdit(true);
                        Log.d(Const.TAG, "Request Error up");
//                        Log.d(Const.TAG,error.getLocalizedMessage());
//                        dialog.dismiss();
                    }
                });


        jsObjRequest.setShouldCache(false);
        requestQueue.add(jsObjRequest);
    }

    public void downLoadAvatar(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, Const.URL_DOWNLOAD_AVATAR + "?username=admin"
                ,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    Log.d(Const.TAG,"loaded");
                    String strBitmap = jsonObject.getString(Const.DATA);
                    Log.d(Const.TAG,strBitmap);
                    imgDown.setImageBitmap(convertBase64ToBitmap(strBitmap));
                } catch (JSONException e) {
                    Log.d(Const.TAG,e.getMessage() +":::json error");
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(Const.TAG,"Request Error");
//                Log.d(Const.TAG,volleyError.getLocalizedMessage());
            }
        });

        requestQueue.add(objectRequest);
    }

    private void uploadImage(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.URL_UPLOAD_AVATAR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        Log.d(Const.TAG,"uploaded");
                        //Showing toast message of the response
//                        Toast.makeText(TestUpAvatar.this, s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        Log.d(Const.TAG, "Request Error up");
                        //Showing toast
//                        Toast.makeText(TestUpAvatar.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = convertBitmapToBase64(bitmap);

                //Getting Image Name
                String name = "admin";

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(Const.AVATAR, image);
                params.put(Const.USERNAME, name);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btChoose:
                showFileChooser();
                break;
            case R.id.btUp:
                upLoadAvatar(bitmap);
                break;
            case R.id.btLoad:
                downLoadAvatar();
                break;
        }
    }
}
