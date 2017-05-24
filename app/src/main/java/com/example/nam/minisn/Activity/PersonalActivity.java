package com.example.nam.minisn.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.nam.minisn.Adapter.PersonalAdapter;
import com.example.nam.minisn.Adapter.StatusAdapter;
import com.example.nam.minisn.ItemListview.Friend;
import com.example.nam.minisn.ItemListview.Status;
import com.example.nam.minisn.R;
import com.example.nam.minisn.UseVoley.CustomRequest;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SQLiteDataController;
import com.example.nam.minisn.Util.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class PersonalActivity extends AppCompatActivity implements View.OnClickListener {
    private CircleImageView avatar;
    private LinearLayout banner;
    private LinearLayout btnInfo;
    private Intent intent;
    private Bundle bundle;
    private ListView listView;
    private static int friendId;
    private String token;
    private int useId;
    private static ArrayList<Status> data;
    private static PersonalAdapter adapter;
    private ProgressDialog dialog;
    private LinearLayout loadErr, loadSucess, progress;
    private SQLiteDataController dataController;
    private int page = 0;
    private boolean isLoad = false;
    private long lastLoad = 0;
    private boolean isShowProgress = false;
    private int lastPositionFirst = 0;
    private int lastPositionEnd = 0;
    private String statusMessage;
    private boolean isEmptyStatus = false;
    private LinearLayout btnBack, lnInfo;
    private TextView toolbarText;
    private TextView nameFriend;
    private int heightParams;
    private int marginParam;
    private int withBackground;
    private int heightBackground;
    private Bitmap bitmap;
    private Uri filePath;
    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_personal);
        init();
    }

    public void init() {
        lnInfo = (LinearLayout) findViewById(R.id.personal_info);
        toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText("Nhật kí");
        nameFriend = (TextView) findViewById(R.id.personal_info_name);
        btnBack = (LinearLayout) findViewById(R.id.toolbar_btnback);
        avatar = (CircleImageView) findViewById(R.id.personal_avatar);
        banner = (LinearLayout) findViewById(R.id.personal_img_banner);
        btnInfo = (LinearLayout) findViewById(R.id.btn_info);
        listView = (ListView) findViewById(R.id.personal_lv);
        progress = (LinearLayout) findViewById(R.id.status_progress);
        loadErr = (LinearLayout) findViewById(R.id.status_load_err);
        loadSucess = (LinearLayout) findViewById(R.id.status_loaded);

        token = SharedPrefManager.getInstance(getApplicationContext()).getString(Const.TOKEN);
        data = new ArrayList<>();
        adapter = new PersonalAdapter(this, R.layout.item_lv_personal, data);
        listView.setAdapter(adapter);
        intent = getIntent();
        bundle = intent.getBundleExtra(Const.PACKAGE);
        friendId = bundle.getInt(Const.ID);
        dataController = new SQLiteDataController(this);
        dataController.openDataBase();
        dialog = new ProgressDialog(this, R.style.AppTheme_Dialog);
        dialog.setMessage("Đang tải");
        dialog.setCancelable(false);
        dialog.show();
        if (!isEmptyStatus)
            getListStatus();
        else
            Toasty.info(this, "Không còn bảng tin cũ hơn", Toast.LENGTH_SHORT).show();
        listener();

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) lnInfo.getLayoutParams();
        marginParam = layoutParams.topMargin;
        heightParams = layoutParams.height;

        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) banner.getLayoutParams();
        banner.post(new Runnable() {

            @Override
            public void run() {
                heightBackground = banner.getHeight();
                withBackground = banner.getWidth();
                Log.i("TEST", "Layout width : " + banner.getWidth());

            }
        });
        Log.d(Const.TAG, "height: " + heightBackground);
        Log.d(Const.TAG, "width: " + withBackground);
    }

    public void listener() {
        btnBack.setOnClickListener(this);
        btnInfo.setOnClickListener(this);
        loadErr.setOnClickListener(this);
        banner.setOnClickListener(this);
        avatar.setOnClickListener(this);
        if (friendId == useId)
            registerForContextMenu(avatar);
    }

    public void getListStatus() {
        HashMap<String, String> params = new HashMap<>();
        params.put(Const.TOKEN, token);
        params.put(Const.PAGE, String.valueOf(page));
        params.put(Const.ID, String.valueOf(friendId));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Const.URL_GET_STATUS_FRIEND, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt(Const.CODE) != Const.CODE_OK) {
                                Log.d(Const.TAG, "status code err: " + response.getString(Const.MESSAGE));
                                setLoad(false);
                                if (dialog.isShowing())
                                    dialog.dismiss();
                            } else {
                                setLoad(true);
                                lastLoad = (long) (System.currentTimeMillis() / 1000);
                                JSONObject info = response.getJSONObject(Const.DATA);
                                JSONArray listStatus = info.getJSONArray(Const.DATA);
                                String displayname = info.getString(Const.DISPLAY_NAME);
                                nameFriend.setText(displayname);
                                if (listStatus.length() > 0) {
                                    for (int i = 0; i < listStatus.length(); i++) {
                                        JSONObject jsonStatus = listStatus.getJSONObject(i);
                                        int idStatus = jsonStatus.getInt(Const.ID);
                                        int idFriend = jsonStatus.getInt(Const.ID_USERNAME);
                                        String status = jsonStatus.getString(Const.STATUS);
                                        long timePost = jsonStatus.getLong(Const.TIME_POST);
                                        int typeLike = jsonStatus.getInt(Const.TYPE_LIKE);
                                        int numberLike = jsonStatus.getInt(Const.NUMBER_LIKE);
                                        int numberComment = jsonStatus.getInt(Const.NUMBER_COMMENT);
                                        Friend friend = dataController.getFriend(idFriend);
                                        Status item = new Status(idStatus, friend, timePost, status, typeLike, numberComment, numberLike);
                                        data.add(item);
                                    }
                                    adapter.notifyDataSetChanged();
                                    if (dialog.isShowing())
                                        dialog.dismiss();
                                    page++;
                                } else {
                                    isEmptyStatus = true;
                                    if (dialog.isShowing())
                                        dialog.dismiss();
                                    Toasty.info(getApplicationContext(), "Không còn bảng tin cũ hơn", Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (JSONException e) {
                            Log.d(Const.TAG, "status json err");
                            if (dialog.isShowing())
                                dialog.dismiss();
                            setLoad(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(Const.TAG, "status vl err");
                        if (dialog.isShowing())
                            dialog.dismiss();
                        setLoad(false);
                    }
                });


        jsObjRequest.setShouldCache(false);
        requestQueue.add(jsObjRequest);
    }

    public static int getFriendId() {
        return friendId;
    }

    public static ArrayList<Status> getData() {
        return data;
    }

    public static PersonalAdapter getAdapter() {
        return adapter;
    }

    public void setLoad(boolean check) {
        if (check) {
            listView.setVisibility(View.VISIBLE);
            loadErr.setVisibility(View.INVISIBLE);
            loadErr.setEnabled(false);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) lnInfo.getLayoutParams();
            layoutParams.topMargin = marginParam;
            layoutParams.height = heightParams;
            lnInfo.setLayoutParams(layoutParams);
        } else {
            listView.setVisibility(View.INVISIBLE);
            loadErr.setVisibility(View.VISIBLE);
            loadErr.setEnabled(true);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) lnInfo.getLayoutParams();
            layoutParams.topMargin = 0;
            layoutParams.height = 0;
            lnInfo.setLayoutParams(layoutParams);

        }
    }

    public AbsListView.OnScrollListener lvScroll = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == 0) {
                if (lastPositionFirst == 0 && listView.getFirstVisiblePosition() == 0) {
                    if (!isLoad) {
                        isLoad = !isLoad;
                        showProgress();
                        getNewStatus();
                    }
                }
                if (listView.getLastVisiblePosition() == data.size() - 1) {
                    if (listView.getLastVisiblePosition() == lastPositionEnd) {
                        if (!isEmptyStatus)
                            getListStatus();
                        else
                            Toast.makeText(getApplicationContext(), "Không còn bảng tin cũ hơn", Toast.LENGTH_SHORT).show();
                    }
                }
                lastPositionEnd = listView.getLastVisiblePosition();
                lastPositionFirst = listView.getFirstVisiblePosition();
            }

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }


    };

    public void showProgress() {
        if (!isShowProgress) {
            isShowProgress = true;
            progress.setVisibility(View.VISIBLE);
        }
    }


    public void hideProgress() {
        if (isShowProgress) {
            isShowProgress = false;
            progress.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.status_load_err:
                dialog.setCancelable(true);
                dialog.setMessage("Đang tải");
                dialog.show();
                page = 0;
                isEmptyStatus = false;
                getListStatus();
                break;
            case R.id.toolbar_btnback:
                finish();
                break;
            case R.id.personal_img_banner:
                showFileChooser("Chọn ảnh bìa");
                break;
        }
    }

    public void getNewStatus() {
        HashMap<String, String> params = new HashMap<>();
        params.put(Const.TOKEN, token);
        params.put(Const.KEY_NEW_STATUS, String.valueOf(lastLoad));
        params.put(Const.ID, String.valueOf(friendId));
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Const.URL_GET_NEW_STATUS_FRIEND, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt(Const.CODE) != Const.CODE_OK) {
                            } else {
                                lastLoad = (long) (System.currentTimeMillis() / 1000);
                                JSONArray listStatus = response.getJSONArray(Const.DATA);
                                for (int i = 0; i < listStatus.length(); i++) {
                                    JSONObject jsonStatus = listStatus.getJSONObject(i);
                                    int idStatus = jsonStatus.getInt(Const.ID);
                                    int idFriend = jsonStatus.getInt(Const.ID_USERNAME);
                                    String status = jsonStatus.getString(Const.STATUS);
                                    long timePost = jsonStatus.getLong(Const.TIME_POST);
                                    int typeLike = jsonStatus.getInt(Const.TYPE_LIKE);
                                    int numberLike = jsonStatus.getInt(Const.NUMBER_LIKE);
                                    int numberComment = jsonStatus.getInt(Const.NUMBER_COMMENT);

                                    Friend friend = dataController.getFriend(idFriend);
                                    Status item = new Status(idStatus, friend, timePost, status, typeLike, numberComment, numberLike);
                                    data.add(0, item);
                                }
                                adapter.notifyDataSetChanged();
                            }
                            hideProgress();
                            isLoad = !isLoad;

                        } catch (JSONException e) {
                            Log.d(Const.TAG, "status json err");
                            hideProgress();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(Const.TAG, "status vl err");
                        hideProgress();
                    }
                });


        jsObjRequest.setShouldCache(false);
        requestQueue.add(jsObjRequest);
    }

    private void showFileChooser(String message) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, message), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
//            File file = icon_new File(filePath);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Bitmap resize = Bitmap.createScaledBitmap(bitmap, withBackground, heightBackground, false);
                BitmapDrawable drawable = new BitmapDrawable(resize);
//                drawable.setGravity(Gravity.CENTER);
                banner.setBackgroundDrawable(drawable);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_ava,menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_ava_view:
                break;
            case R.id.menu_ava_change:
                break;
        }
        return super.onContextItemSelected(item);
    }
}
