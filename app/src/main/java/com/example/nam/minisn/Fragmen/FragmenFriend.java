package com.example.nam.minisn.Fragmen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.android.volley.toolbox.Volley;
import com.example.nam.minisn.Activity.ChatActivity;
import com.example.nam.minisn.Activity.RequestFriendActivity;
import com.example.nam.minisn.Adapter.ListviewFriendAdapter;
import com.example.nam.minisn.ItemListview.Friend;
import com.example.nam.minisn.ItemListview.ItemDeleteFriend;
import com.example.nam.minisn.R;
import com.example.nam.minisn.UseVoley.CustomRequest;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SQLiteDataController;
import com.example.nam.minisn.Util.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nam on 2/24/2017.
 */

public class FragmenFriend extends Fragment implements View.OnClickListener {
    private View rootView;
    private Bundle bundle = new Bundle();
    private ListView lvFriend;
    private ProgressDialog progressDialog;
    private static ListviewFriendAdapter adapter;


    private static ArrayList<ItemDeleteFriend> friends = new ArrayList<>();
    private int use_id;
    private static LinearLayout requestFriend;
    private TextView newRequestFriendSize;
    private LinearLayout newRequest;
    private SQLiteDataController database;
    private FloatingActionButton fabMain, fabConversation, fabFriend, fabRequestFriend;
    private static Animation show_fab1, hide_fab1, show_fab2, hide_fab2, show_fab3, hide_fab3;
    private static EditText inputSearch;
    private static LinearLayout layoutDelete;
    private static boolean isSearch = false;
    private static boolean isDelete = false;
    private boolean isOpenSubMenu = false;
    private static LinearLayout space;
    private static String textSearch = "";
    private static FrameLayout frame;
    private static TextView tvCount;
    private Intent intent;
    private int newIdConversation = 0;
    private static CheckBox cbAll;
    private Bundle bundleChat;

    public FragmenFriend() {
    }

    public static FragmenFriend newInstance(Bundle bundle) {
        FragmenFriend fragmen = new FragmenFriend();

        fragmen.setArguments(bundle);
        return fragmen;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dialog);
//        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        rootView = inflater.inflate(R.layout.layout_tab_friend, container, false);
        bundle = getArguments();
//        friends = (ArrayList<Friend>)bundle.getSerializable(Const.DB_FRIEND);
        init();
        animation();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        database.openDataBase();
        int countRequest = database.getCountRequestFriend(use_id);
        newRequestFriendSize.setText(String.valueOf(countRequest));
        if (countRequest > 0) {
            newRequest.setVisibility(View.VISIBLE);
        } else
            newRequest.setVisibility(View.INVISIBLE);

        friends.clear();
        getListFriend();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isOpenSubMenu) {
            hideSubMenu();
            isOpenSubMenu = !isOpenSubMenu;
        }
        database.close();
    }

    public void init() {
        bundleChat = new Bundle();
        cbAll = (CheckBox) rootView.findViewById(R.id.friend_cb_all);
        tvCount = (TextView) rootView.findViewById(R.id.friend_count_delete);
        frame = (FrameLayout) rootView.findViewById(R.id.friend_frame);
        layoutDelete = (LinearLayout) rootView.findViewById(R.id.friend_delete);
        space = (LinearLayout) rootView.findViewById(R.id.tab_Friend_space);
        inputSearch = (EditText) rootView.findViewById(R.id.search_friend_input);
        fabMain = (FloatingActionButton) rootView.findViewById(R.id.main_fab);
        fabConversation = (FloatingActionButton) rootView.findViewById(R.id.fab_1);
        fabFriend = (FloatingActionButton) rootView.findViewById(R.id.fab_2);
        fabRequestFriend = (FloatingActionButton) rootView.findViewById(R.id.fab_3);
        database = new SQLiteDataController(getActivity());
        requestFriend = (LinearLayout) rootView.findViewById(R.id.friend_request);
        newRequestFriendSize = (TextView) rootView.findViewById(R.id.friend_newRequest_count);
        newRequest = (LinearLayout) rootView.findViewById(R.id.friend_newRequest);
        use_id = SharedPrefManager.getInstance(getActivity()).getInt(Const.ID);
        lvFriend = (ListView) rootView.findViewById(R.id.tab_Friend_lvFriend);

        adapter = new ListviewFriendAdapter(getActivity(), R.layout.item_lvfriend, friends);
        lvFriend.setAdapter(adapter);
        intent = new Intent(getActivity(), ChatActivity.class);
        addListener();
    }

    public void addListener() {
        cbAll.setOnClickListener(this);
        requestFriend.setOnClickListener(this);
        fabConversation.setOnClickListener(this);
        fabMain.setOnClickListener(this);
        fabFriend.setOnClickListener(this);
        fabRequestFriend.setOnClickListener(this);
        inputSearch.addTextChangedListener(changeInput);
        lvFriend.setOnItemClickListener(itemLvFriendClick);
    }


    public void getListFriend() {
        String sql = Const.SELECT +

                " * " +
                Const.FROM +
                Const.DB_FRIEND +
                Const.WHERE +
                Const.FRIENDS_COL4 +
                "= '" +
                use_id +
                "'";
        Cursor cursor = database.getDatabase().rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int fri_id = cursor.getInt(1);
            String fri_username = cursor.getString(2);
            String fri_display = cursor.getString(3);
            int choose = cursor.getInt(7);

            Friend friend = new Friend(fri_id, fri_username, fri_display);
            friends.add(new ItemDeleteFriend(friend, isDelete, choose));
        }

        adapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }

    public void requestFriendClick() {

        Intent intent = new Intent(getActivity(), RequestFriendActivity.class);
        startActivity(intent);
    }


    AdapterView.OnItemClickListener lvFriendClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };

    public void animation() {
        show_fab1 = AnimationUtils.loadAnimation(getActivity(), R.anim.fab1_show);
        hide_fab1 = AnimationUtils.loadAnimation(getActivity(), R.anim.fab1_hide);
        show_fab2 = AnimationUtils.loadAnimation(getActivity(), R.anim.fab2_show);
        hide_fab2 = AnimationUtils.loadAnimation(getActivity(), R.anim.fab2_hide);
        show_fab3 = AnimationUtils.loadAnimation(getActivity(), R.anim.fab3_show);
        hide_fab3 = AnimationUtils.loadAnimation(getActivity(), R.anim.fab3_hide);


//        hideFab();
    }

    public void showSubMenu() {
        fabMain.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_forward));
        showEachFab(fabConversation, show_fab1, (float) 1.7, (float) 0.25);
        showEachFab(fabFriend, show_fab2, (float) 1.5, (float) 1.5);
        showEachFab(fabRequestFriend, show_fab3, (float) 0.25, (float) 1.7);
    }

    public void hideSubMenu() {
        fabMain.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_backward));
        hideEachFab(fabConversation, hide_fab1, (float) 1.7, (float) 0.25);
        hideEachFab(fabFriend, hide_fab2, (float) 1.5, (float) 1.5);
        hideEachFab(fabRequestFriend, hide_fab3, (float) 0.25, (float) 1.7);
    }

    public void showEachFab(FloatingActionButton fab, Animation anim, float x, float y) {
        fab.startAnimation(anim);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab.getLayoutParams();
        layoutParams.rightMargin += (int) (fab.getWidth() * x);
        layoutParams.bottomMargin += (int) (fab.getHeight() * y);
        fab.setLayoutParams(layoutParams);
        fab.setVisibility(View.VISIBLE);
        fab.setClickable(true);
    }

    public void hideEachFab(FloatingActionButton fab, Animation anim, float x, float y) {
        fab.startAnimation(anim);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab.getLayoutParams();
        layoutParams.rightMargin -= (int) (fab.getWidth() * x);
        layoutParams.bottomMargin -= (int) (fab.getHeight() * y);
        fab.setLayoutParams(layoutParams);
        fab.setVisibility(View.INVISIBLE);
        fab.setClickable(false);
    }


    public static void showSearch() {
        if (!isSearch) {
            inputSearch.setVisibility(View.VISIBLE);
            requestFriend.setVisibility(View.INVISIBLE);
            space.setVisibility(View.INVISIBLE);
            isSearch = !isSearch;
        }
    }

    public static void hideSearch() {
        if (isSearch) {
            inputSearch.setVisibility(View.INVISIBLE);
            requestFriend.setVisibility(View.VISIBLE);
            space.setVisibility(View.VISIBLE);
            isSearch = !isSearch;
        }
    }

    public void showDelete() {
        if (!isDelete) {
            isDelete = true;
            showSearch();
            for (int i = 0; i < friends.size(); i++) {
                friends.get(i).setShowCheckBox(true);
            }
            layoutDelete.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frame.getLayoutParams();
            layoutParams.topMargin += 100;
            frame.setLayoutParams(layoutParams);
        }
    }

    public static void hideDelete() {
        if (isDelete) {
            isDelete = false;
            for (int i = 0; i < friends.size(); i++) {
                friends.get(i).setShowCheckBox(false);
            }
            layoutDelete.setVisibility(View.INVISIBLE);
            adapter.notifyDataSetChanged();
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frame.getLayoutParams();
            layoutParams.topMargin -= 100;
            frame.setLayoutParams(layoutParams);
        }
    }

    public TextWatcher changeInput = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            database.openDataBase();
            String name = inputSearch.getText().toString();
            textSearch = name;
            friends.clear();
            friends.addAll(database.searchFriends(name, use_id));
            adapter.notifyDataSetChanged();
            int countChoose = 0;
            for (int i = 0; i < friends.size(); i++) {
                if (friends.get(i).getTpeChoose() == Const.TYPE_CHOOSE)
                    countChoose++;
            }
            if (countChoose == friends.size())
                cbAll.setChecked(true);
            else
                cbAll.setChecked(false);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public void changeCheckAll() {
        int choose = cbAll.isChecked() ? Const.TYPE_CHOOSE : Const.TYPE_NO_CHOOSE;
        for (int i = 0; i < friends.size(); i++) {
            friends.get(i).setTypeChoose(choose);
            database.updateChooseFriend(use_id, friends.get(i).getFriend().getId(), choose);
        }
        tvCount.setText(String.valueOf(database.getCountChooseFriend()));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_fab:
                if (isOpenSubMenu) {
                    hideSubMenu();
                    isOpenSubMenu = false;
                } else {
                    showSubMenu();
                    isOpenSubMenu = true;
                }
                break;
            case R.id.fab_3:
                fab3Click();
                break;
            case R.id.fab_2:
//                conversationTab.setVisibility(View.INVISIBLE);
                break;
            case R.id.fab_1:
                fab1Click();
                break;
            case R.id.friend_request:
                requestFriendClick();
                break;
            case R.id.friend_cb_all:
                changeCheckAll();
                break;

        }
    }

    public void fab1Click() {
        if (!isSearch) {

            if (isOpenSubMenu) {
                hideSubMenu();
                isOpenSubMenu = !isOpenSubMenu;
            }
            showSearch();
        }
    }

    public void fab3Click() {
        if (!isDelete) {
            if (isOpenSubMenu) {
                hideSubMenu();
                isOpenSubMenu = !isOpenSubMenu;
            }
            showDelete();
        }
    }

    public static boolean isDelete() {
        return isDelete;
    }

    public static String getTextSearch() {
        return textSearch;
    }

    public static boolean isSearch() {
        return isSearch;
    }


    public static TextView getTvCount() {
        return tvCount;
    }

    public static void setTvCount(TextView tvCount) {
        FragmenFriend.tvCount = tvCount;
    }

    public static ArrayList<ItemDeleteFriend> getFriends() {
        return friends;
    }

    public static void setFriends(ArrayList<ItemDeleteFriend> friends) {
        FragmenFriend.friends = friends;
    }


    public static CheckBox getCbAll() {
        return cbAll;
    }

    public static void setCbAll(CheckBox cbAll) {
        FragmenFriend.cbAll = cbAll;
    }

    public AdapterView.OnItemClickListener itemLvFriendClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Friend item = friends.get(position).getFriend();
            int idConversation = database.getConversationFriend(item.getId(), use_id);
            Log.d(Const.TAG, "click");
            Log.d(Const.TAG, "idCon: " + idConversation);
            if (idConversation > 0) {
                String name = database.getNameConversation(idConversation, use_id);
                bundleChat.putInt(Const.CONVERSATION_ID, item.getId());
                bundleChat.putString(Const.NAME_CONVERSATION, name);
                intent.putExtra(Const.PACKAGE, bundleChat);
                startActivity(intent);
            } else {
                HashMap<String,String> params = new HashMap<>();
                params.put(Const.TOKEN,bundle.getString(Const.TOKEN));
                params.put(Const.ID_USER_FRIEND+"0",String.valueOf(item.getId()));
                addNewConversation(params,item.getUsername());
            }
        }
    };

    public void addNewConversation(HashMap<String,String> params, final String name){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, Const.URL_ADD_NEW_CONVERSATION, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt(Const.CODE) != Const.CODE_OK) {
                                bundleChat.putInt(Const.CONVERSATION_ID,response.getInt(Const.DATA));
                                bundleChat.putString(Const.NAME_CONVERSATION,name);
                                intent.putExtra(Const.PACKAGE,bundle);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(),"Có lỗi xảy ra. Vui lòng thử lại",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(),"Có lỗi xảy ra. Vui lòng thử lại",Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),"Có lỗi xảy ra. Vui lòng thử lại",Toast.LENGTH_SHORT).show();
                    }
                });


        jsObjRequest.setShouldCache(false);
        requestQueue.add(jsObjRequest);
    }
}
