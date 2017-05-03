package com.example.nam.minisn.Fragmen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nam.minisn.Activity.ChatActivity;
import com.example.nam.minisn.Adapter.ListviewConversationAdapter;
import com.example.nam.minisn.ItemListview.Conversation;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;
import com.example.nam.minisn.Util.SQLiteDataController;
import com.example.nam.minisn.Util.SharedPrefManager;

import java.util.ArrayList;

/**
 * Created by Nam on 2/21/2017.
 */

public class FragmenConversation extends Fragment implements View.OnClickListener {
    private static final String SHOW_DELETE = "s_delete";
    private static final String SHOW_SEARCH = "s_search";
    private static final int TYPE_SHOW = 1;
    private View rootView;
    private ListView lvConversation;
    public static ArrayList<Conversation> data = new ArrayList<>();
    public static ArrayList<Conversation> dataClone = new ArrayList<>();
    private ArrayList<Conversation> dataSingle = new ArrayList<>();
    private ArrayList<Conversation> dataGroup = new ArrayList<>();
    private Bundle bundle;
    private ListviewConversationAdapter adapter;
    private ProgressDialog progressDialog;
    private Intent intent;
    private String token;
    private int use_id;
    private SQLiteDataController database;
    private TextView single, group;
    private ImageView singleNew, groupNew;
    private FloatingActionButton fabMain, fabConversation, fabFriend, fabRequestFriend;
    private Animation show_fab1, hide_fab1, show_fab2, hide_fab2, show_fab3, hide_fab3;
    private boolean isOpenSubMenu = false;
    private LinearLayout conversationTab, space;
    private EditText inputSearch;
    private ImageView btnDelete;
    public static String search;
    public static boolean isSearch = false;
    private LinearLayout layoutDelete;
    private TextView tvDelete;
    public static TextView tvCount;
    private CheckBox checkAll;
    private FrameLayout frame;
    private boolean isShowDelete = false;

    public FragmenConversation() {

    }

    public static FragmenConversation newInstance(Bundle bundle) {
        FragmenConversation fragmen = new FragmenConversation();

        fragmen.setArguments(bundle);
        return fragmen;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dialog);
//        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading");
        progressDialog.show();
        rootView = inflater.inflate(R.layout.layout_tab_conversation, container, false);
        bundle = getArguments();
//        data = (ArrayList<Conversation>) bundle.getSerializable(Const.DB_CONVERSATION);
        use_id = SharedPrefManager.getInstance(getActivity()).getInt(Const.ID);
        token = SharedPrefManager.getInstance(getActivity()).getString(Const.TOKEN);
        init();
        animation();
        return rootView;
    }

    public void init() {
        frame = (FrameLayout) rootView.findViewById(R.id.frame);
        checkAll = (CheckBox) rootView.findViewById(R.id.conversation_cb_all);
        tvCount = (TextView) rootView.findViewById(R.id.conversation_count_delete);
        tvDelete = (TextView) rootView.findViewById(R.id.conversation_bt_delete);
        layoutDelete = (LinearLayout) rootView.findViewById(R.id.conversation_delete);
        btnDelete = (ImageView) rootView.findViewById(R.id.btn_delete);
        inputSearch = (EditText) rootView.findViewById(R.id.search_conversation_input);
        space = (LinearLayout) rootView.findViewById(R.id.space);
        fabMain = (FloatingActionButton) rootView.findViewById(R.id.main_fab);
        fabConversation = (FloatingActionButton) rootView.findViewById(R.id.fab_1);
        fabFriend = (FloatingActionButton) rootView.findViewById(R.id.fab_2);
        fabRequestFriend = (FloatingActionButton) rootView.findViewById(R.id.fab_3);
        conversationTab = (LinearLayout) rootView.findViewById(R.id.tab_conversation_tab);
        singleNew = (ImageView) rootView.findViewById(R.id.single_new);
        groupNew = (ImageView) rootView.findViewById(R.id.group_new);
        single = (TextView) rootView.findViewById(R.id.lv_conversation_single);
        group = (TextView) rootView.findViewById(R.id.lv_conversation_group);


        single.setOnClickListener(singleClick);
        group.setOnClickListener(groupClick);
        fabConversation.setOnClickListener(this);
        fabMain.setOnClickListener(this);
        fabFriend.setOnClickListener(this);
        fabRequestFriend.setOnClickListener(this);
        database = new SQLiteDataController(getActivity());
        lvConversation = (ListView) rootView.findViewById(R.id.tab_Conversation_lv);

        adapter = new ListviewConversationAdapter(getActivity(), R.layout.item_lvconversation, data);
        lvConversation.setAdapter(adapter);
        lvConversation.setOnItemClickListener(itemLvConversationCkick);
        inputSearch.addTextChangedListener(changeInput);
        search = new String();

        checkAll.setOnCheckedChangeListener(chanCheckAll);
    }

    @Override
    public void onResume() {
        super.onResume();
        database.openDataBase();
        setConversationSelect(Const.CONVERSATION_TYPE_SINGLE);
        data.clear();
        dataSingle.clear();
        dataGroup.clear();
        getListConversation();
        if (isShowDelete){
            for (int i = 0; i < data.size(); i++) {
                data.get(i).setShowCheckBox(true);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
//

    public void getListConversation() {
//        SQLiteDataController db = new SQLiteDataController(getActivity());
//            database.isCreatedDatabase();
//        database.openDataBase();
        String sql = Const.SELECT +
                " * " +
                Const.FROM +
                Const.DB_CONVERSATION +
                Const.WHERE +
                Const.CONVERSATION_COL5 +
                "= '" +
                use_id +
                "'" +
                Const.ORDER_BY +
                Const.CONVERSATION_COL4 +
                ","+
                Const.CONVERSATION_COL1+
                Const.ASC;
        Cursor cursor = database.getDatabase().rawQuery(sql, null);
        while (cursor.moveToNext()) {
            int idConversation = cursor.getInt(cursor.getColumnIndex(Const.CONVERSATION_COL2));
            String nameConversation = cursor.getString(cursor.getColumnIndex(Const.CONVERSATION_COL1));
            String lastMessage = cursor.getString(3);
            long time = 0;
            if (cursor.getLong(4) > 0)
                time = cursor.getLong(4);
            boolean isNew = cursor.getInt(cursor.getColumnIndex(Const.CONVERSATION_COL6)) == 1;
            int typeChoose = cursor.getInt(cursor.getColumnIndex(Const.CONVERSATION_COL8));
            Conversation conversation = new Conversation(idConversation, nameConversation, lastMessage, time, isNew,typeChoose);
            int size = cursor.getInt(7);
            if (size > 2) {
                if (conversation.isNew())
                    groupNew.setVisibility(View.VISIBLE);
                else
                    groupNew.setVisibility(View.INVISIBLE);
                dataGroup.add(conversation);
            } else {
                if (conversation.isNew())
                    single.setVisibility(View.VISIBLE);
                else
                    singleNew.setVisibility(View.INVISIBLE);
                dataSingle.add(conversation);
            }
            dataClone.add(conversation);
        }
//        database.close();
//        data = new ArrayList<>(dataSingle);
        if (isSearch)
            data.addAll(dataClone);
        else
            data.addAll(dataSingle);
        adapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }

    public AdapterView.OnItemClickListener itemLvConversationCkick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            intent = new Intent(getActivity(), ChatActivity.class);
            Conversation item = data.get(position);
            if (item.isNew()) {
//                database.openDataBase();
                database.setNewMessageConversation(item.getId(), use_id);
//                database.close();
                item.setNew(false);
//                adapter.notifyDataSetChanged();
            }
            bundle.putInt(Const.CONVERSATION_ID, item.getId());
            bundle.putString(Const.NAME_CONVERSATION, item.getNameConservation());
            intent.putExtra(Const.PACKAGE, bundle);
            startActivity(intent);
        }
    };

    public View.OnClickListener singleClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setConversationSelect(Const.CONVERSATION_TYPE_SINGLE);
            data.clear();
            data.addAll(dataSingle);
            adapter.notifyDataSetChanged();
        }
    };
    public View.OnClickListener groupClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setConversationSelect(Const.CONVERSATION_TYPE_GROUP);
            data.clear();
            data.addAll(dataGroup);
            adapter.notifyDataSetChanged();
        }
    };

    public void setConversationSelect(int choose) {
        if (choose == Const.CONVERSATION_TYPE_SINGLE) {
            single.setTextColor(getResources().getColor(R.color.colorTextChoose));
            group.setTextColor(getResources().getColor(R.color.colorText));
        } else {
            group.setTextColor(getResources().getColor(R.color.colorTextChoose));
            single.setTextColor(getResources().getColor(R.color.colorText));
        }
    }

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
                if (isOpenSubMenu) {
                    hideSubMenu();
                    isOpenSubMenu = false;
                }
                showDelete();
                break;
            case R.id.fab_2:
//                conversationTab.setVisibility(View.INVISIBLE);
                break;
            case R.id.fab_1:
                if (isOpenSubMenu) {
                    hideSubMenu();
                    isOpenSubMenu = false;
                }
                showSearch();
                break;
        }
    }

    public void showSearch(){
        this.isSearch = true;
        conversationTab.setVisibility(View.INVISIBLE);
        space.setVisibility(View.INVISIBLE);
        inputSearch.setVisibility(View.VISIBLE);
        data.clear();
        data.addAll(dataClone);
        adapter.notifyDataSetChanged();
    }

    public void showDelete(){
        if (!isShowDelete) {
            isShowDelete = true;
            showSearch();
            for (int i = 0; i < data.size(); i++) {
                data.get(i).setShowCheckBox(true);
            }
            layoutDelete.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frame.getLayoutParams();
            layoutParams.topMargin += 100;
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
            FragmenConversation.search = name;
            data.clear();
            ArrayList<Conversation> temp = new ArrayList<>();

            temp.addAll(database.searchConversation(name, use_id));
            for (int i =0;i<temp.size();i++){
                int id = temp.get(i).getId();
                for( int j=0;j<dataClone.size();j ++){
                    if (dataClone.get(j).getId() == id)
                        data.add(dataClone.get(j));
                }
            }
            adapter.notifyDataSetChanged();
//            database.close();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public CompoundButton.OnCheckedChangeListener chanCheckAll = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int choose = isChecked?Const.CONVERSATION_TYPE_CHOOSE:Const.CONVERSATION_TYPE_NO_CHOOSE;
            for (int i = 0; i < data.size(); i++) {
                data.get(i).setTypeChoose(choose);
            }
            database.setAllChoose(choose);
            tvCount.setText(String.valueOf(database.getCountChoose()));
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        int showDelete = isShowDelete?this.TYPE_SHOW:0;
        int showSearch = isSearch?this.TYPE_SHOW:0;
        SharedPrefManager.getInstance(getActivity()).savePreferences(this.SHOW_DELETE,showDelete);
        SharedPrefManager.getInstance(getActivity()).savePreferences(this.SHOW_SEARCH,showSearch);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        database.setAllChoose(Const.CONVERSATION_TYPE_NO_CHOOSE);
        database.close();
    }

}
