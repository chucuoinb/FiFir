package com.example.nam.minisn.Fragmen;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nam.minisn.Adapter.StatusAdapter;
import com.example.nam.minisn.ItemListview.Chat;
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

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentStatus extends Fragment implements View.OnClickListener {
    private View rootView;
    private Bundle bundle;
    private static StatusAdapter adapter;
    private static ArrayList<Status> data;
    private ListView lvStatus;
    private EditText inputStatus;
    private TextView btnPost;
    private ProgressDialog dialog;
    private int useId;
    private String token;
    private LinearLayout loadErr, loadSucess;
    private SQLiteDataController dataController;
    private int page;
    private boolean isLoad = false;
    private long lastLoad;
    private Animation progressAnim;
    private LinearLayout progress, statutPost;
    private boolean isShowProgress = false;
    private int lastPositionFirst = 0;
    private int lastPositionEnd = 0;
    private String statusMessage;
    private boolean isEmptyStatus = false;

    public FragmentStatus() {
    }


    public static FragmentStatus newInstance(Bundle bundle) {
        FragmentStatus fragmen = new FragmentStatus();
        fragmen.setArguments(bundle);
        return fragmen;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_tab_status, container, false);
        bundle = getArguments();

        init();
        return rootView;
    }

    public void init() {
        statutPost = (LinearLayout) rootView.findViewById(R.id.status_post_status);
        progress = (LinearLayout) rootView.findViewById(R.id.status_progress);
        loadErr = (LinearLayout) rootView.findViewById(R.id.status_load_err);
        loadSucess = (LinearLayout) rootView.findViewById(R.id.status_loaded);
        lvStatus = (ListView) rootView.findViewById(R.id.status_lv);
        btnPost = (TextView) rootView.findViewById(R.id.status_btnPost);
        inputStatus = (EditText) rootView.findViewById(R.id.status_input);

        page = 0;


        dataController = new SQLiteDataController(getActivity());
        dataController.openDataBase();
        data = new ArrayList<>();
        adapter = new StatusAdapter(getActivity(), R.layout.item_tab_status, data);
        lvStatus.setAdapter(adapter);

        dialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dialog);
        dialog.setMessage("Đang tải");
        dialog.setCancelable(false);
        token = SharedPrefManager.getInstance(getActivity()).getString(Const.TOKEN);
        useId = SharedPrefManager.getInstance(getActivity()).getInt(Const.ID);


        if (!isEmptyStatus)
            getListStatus();
        else
            Toast.makeText(getActivity(),"Không còn bảng tin cũ hơn",Toast.LENGTH_SHORT).show();
        listener();
    }

    public void listener() {
        lvStatus.setOnScrollListener(lvScroll);
        loadErr.setOnClickListener(this);
        btnPost.setOnClickListener(this);

    }

    public static StatusAdapter getAdapter() {
        return adapter;
    }

    public static ArrayList<Status> getData() {
        return data;
    }

    public void getListStatus() {
        HashMap<String, String> params = new HashMap<>();
        params.put(Const.TOKEN, token);
        params.put(Const.PAGE, String.valueOf(page));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        final CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Const.URL_GET_STATUS, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt(Const.CODE) != Const.CODE_OK) {
                                Log.d(Const.TAG, "status code err: " + response.getString(Const.MESSAGE));
                                setLoad(false);
                            } else {
                                setLoad(true);
                                lastLoad = (long) (System.currentTimeMillis() / 1000);
                                JSONArray listStatus = response.getJSONArray(Const.DATA);
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
                                        Status item = new Status(idStatus, friend, timePost, status, typeLike,numberComment, numberLike);
                                        data.add(item);
                                    }
                                    adapter.notifyDataSetChanged();
                                    if (dialog.isShowing())
                                        dialog.dismiss();
                                    page++;
                                } else {
                                    isEmptyStatus = true;
                                    Toast.makeText(getActivity(),"Không còn bảng tin cũ hơn",Toast.LENGTH_SHORT).show();
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

    public void getNewStatus() {
        HashMap<String, String> params = new HashMap<>();
        params.put(Const.TOKEN, token);
        params.put(Const.KEY_NEW_STATUS, String.valueOf(lastLoad));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        final CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Const.URL_GET_NEW_STATUS, params,
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
                                    Status item = new Status(idStatus, friend, timePost, status, typeLike,numberComment, numberLike);
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

    public void setLoad(boolean check) {
        if (check) {
            loadSucess.setVisibility(View.VISIBLE);
            loadErr.setVisibility(View.INVISIBLE);
            loadErr.setEnabled(false);
        } else {
            loadSucess.setVisibility(View.INVISIBLE);
            loadErr.setVisibility(View.VISIBLE);
            loadErr.setEnabled(true);
        }
    }


    public AbsListView.OnScrollListener lvScroll = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == 0) {
                if (lastPositionFirst == 0 && lvStatus.getFirstVisiblePosition() == 0) {
                    if (!isLoad) {
                        isLoad = !isLoad;
                        showProgress();
                        getNewStatus();
                    }
                }
//                Log.d(Const.TAG,"hihi+ "+lvStatus.getLastVisiblePosition() +":"+data.size());
                if (lvStatus.getLastVisiblePosition() == data.size()-1) {
                    if (lvStatus.getLastVisiblePosition() == lastPositionEnd) {
//                        dialog.setMessage("Đang tải thêm..");
//                        dialog.show();
                        if (!isEmptyStatus)
                            getListStatus();
                        else
                            Toast.makeText(getActivity(),"Không còn bảng tin cũ hơn",Toast.LENGTH_SHORT).show();
                    }
                }
                lastPositionEnd = lvStatus.getLastVisiblePosition();
                lastPositionFirst = lvStatus.getFirstVisiblePosition();
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
//            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) progress.getLayoutParams();
//            layoutParams.topMargin = (int) (statutPost.getHeight() + getResources().getDimension(R.dimen.main_margin));
//            TranslateAnimation translateAnimation = new TranslateAnimation(progress.getTranslationX(), progress.getTranslationX(),
//                    -statutPost.getHeight() + getResources().getDimension(R.dimen.main_margin), 0);
//            translateAnimation.setDuration(1000);
//            progress.startAnimation(translateAnimation);
//            progress.setLayoutParams(layoutParams);
        }
    }


    public void hideProgress() {
        if (isShowProgress) {
            isShowProgress = false;
//            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) progress.getLayoutParams();
//            layoutParams.topMargin = 0;
//            TranslateAnimation translateAnimation = new TranslateAnimation(progress.getTranslationX(), progress.getTranslationX(),
//                    statutPost.getHeight() + getResources().getDimension(R.dimen.main_margin), 0);
//            translateAnimation.setDuration(1000);
//            progress.startAnimation(translateAnimation);
//            progress.setLayoutParams(layoutParams);
            progress.setVisibility(View.INVISIBLE);

        }
    }

    public TextWatcher changePost = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            statusMessage = inputStatus.getText().toString();
            if (statusMessage.length() > 0) {
                btnPost.setEnabled(true);
            } else
                btnPost.setEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.status_btnPost:
                dialog.setCancelable(true);
                dialog.setMessage("Đang đăng");
                dialog.show();
                postStatus();
                break;
            case R.id.status_load_err:
                dialog.setCancelable(true);
                dialog.setMessage("Đang tải");
                dialog.show();
                page = 0;
                isEmptyStatus = false;
                getListStatus();
                break;
        }
    }

    public void postStatus() {
        statusMessage = inputStatus.getText().toString();
        if (statusMessage.length() > 0) {
            HashMap<String, String> params = new HashMap<>();
            params.put(Const.TOKEN, token);
            params.put(Const.STATUS, statusMessage);
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Const.URL_ADD_STATUS, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getInt(Const.CODE) != Const.CODE_OK) {
                                } else {
                                    JSONObject newObject = response.getJSONObject(Const.DATA);
//                                    Friend friend = new

                                }
                                if (dialog.isShowing())
                                    dialog.dismiss();

                            } catch (JSONException e) {
                                if (dialog.isShowing())
                                    dialog.dismiss();
                                Log.d(Const.TAG, e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (dialog.isShowing())
                                dialog.dismiss();
                            Log.d(Const.TAG, "post vl er");
                        }
                    });


            jsObjRequest.setShouldCache(false);
            requestQueue.add(jsObjRequest);
        }
    }
}
