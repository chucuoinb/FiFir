package com.example.nam.minisn.Fragmen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.example.nam.minisn.Activity.LoginActivity;
import com.example.nam.minisn.Activity.PersonalActivity;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class FragmentPersonal extends Fragment implements View.OnClickListener {
    private View rootView;
    private Bundle bundle;
    private Intent intent;
    private TextView tvName;
    private LinearLayout goPersonal, lnLogout;
    private CircleImageView avatar;
    private int useId;
    private String strAvatar;
    public FragmentPersonal() {
    }


    public static FragmentPersonal newInstance(Bundle bundle) {
        FragmentPersonal fragmen = new FragmentPersonal();
        fragmen.setArguments(bundle);
        return fragmen;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.tab_personal, container, false);
        bundle = getArguments();

        init();
        return rootView;
    }

    public void init() {
        avatar = (CircleImageView)rootView.findViewById(R.id.tab_personal_ava);
        intent = new Intent(getActivity(), PersonalActivity.class);
        intent.putExtra(Const.PACKAGE, bundle);

        tvName = (TextView) rootView.findViewById(R.id.personal_name);
        goPersonal = (LinearLayout) rootView.findViewById(R.id.personal_go);
        lnLogout = (LinearLayout) rootView.findViewById(R.id.tab_personal_logout);

        tvName.setText(SharedPrefManager.getInstance(getActivity()).getString(Const.DISPLAY_NAME));
        useId = SharedPrefManager.getInstance(getActivity()).getInt(Const.ID);

        listener();
    }

    @Override
    public void onResume() {
        super.onResume();
        strAvatar = SharedPrefManager.getInstance(getActivity()).getString(Const.AVATAR);
        avatar.setImageDrawable(Const.loadImageFromInternal(getActivity(),strAvatar));
    }

    public void listener() {
        lnLogout.setOnClickListener(this);
        goPersonal.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_personal_logout:
                logout();
                break;
            case R.id.personal_go:
                Intent intentPersonal = new Intent(getActivity(),PersonalActivity.class);
                Bundle bundlePersonal = new Bundle();
                bundlePersonal.putInt(Const.ID,useId);
                intentPersonal.putExtra(Const.PACKAGE,bundlePersonal);
                startActivity(intentPersonal);
                break;
        }
    }

    public void logout() {
        HashMap<String, String> params = new HashMap<>();
        params.put(Const.TOKEN, SharedPrefManager.getInstance(getActivity()).getString(Const.TOKEN));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        CustomRequest customRequest = new CustomRequest(Request.Method.POST, Const.URL_LOGOUT, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt(Const.CODE) == Const.CODE_OK){
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                clearPre();
                                startActivity(intent);
                            }
                            else
                                Toasty.error(getActivity(),getActivity().getResources().getString(R.string.notifi_error),Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Toasty.error(getActivity(),getActivity().getResources().getString(R.string.notifi_error),Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toasty.error(getActivity(),getActivity().getResources().getString(R.string.notifi_error),Toast.LENGTH_SHORT).show();

                    }
                });
        customRequest.setShouldCache(false);
        requestQueue.add(customRequest);


    }

    public void clearPre() {
        SharedPrefManager.getInstance(getActivity()).savePreferences(Const.TOKEN, "");
        SharedPrefManager.getInstance(getActivity()).savePreferences(Const.USERNAME, "");
        SharedPrefManager.getInstance(getActivity()).savePreferences(Const.ID, 0);
        SharedPrefManager.getInstance(getActivity()).savePreferences(Const.IS_LOGIN, Const.NO_LOGIN);
        SharedPrefManager.getInstance(getActivity()).savePreferences(Const.DISPLAY_NAME, "");
    }
}
