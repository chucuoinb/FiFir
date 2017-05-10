package com.example.nam.minisn.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.example.nam.minisn.Adapter.TabsAdapter;
import com.example.nam.minisn.Fragmen.FragmentConversation;
import com.example.nam.minisn.Fragmen.FragmentFriend;
import com.example.nam.minisn.ItemListview.Conversation;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class Main extends AppCompatActivity {
    //    private boolean isOpenSubMenu = false;
    private TabsAdapter mSectionsPagerAdapter;
    private Intent intent;
    private Bundle bundle;
    public static int[] iconTabs = {R.drawable.tabchat,
            R.drawable.tabfriend,
            R.drawable.tabstatus,
    };
    private ViewPager mViewPager;
    public static TabLayout tabLayout;
    private ArrayList<Conversation> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init() {


        intent = getIntent();
        bundle = intent.getBundleExtra(Const.PACKAGE);
        tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mViewPager = (ViewPager) findViewById(R.id.main_viewPager);
        mSectionsPagerAdapter = new TabsAdapter(getSupportFragmentManager(), bundle);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(Const.TAB_SIZE-1);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setOnTabSelectedListener(changeTab);
        tabLayout.getTabAt(0).setIcon(iconTabs[0]);
        tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.color_tab_select), PorterDuff.Mode.SRC_IN);
        for (int i = 1; i < Const.TAB_SIZE; i++) {
            tabLayout.getTabAt(i).setIcon(iconTabs[i]);
            tabLayout.getTabAt(i).getIcon().setColorFilter(getResources().getColor(R.color.color_tab), PorterDuff.Mode.SRC_IN);
        }
//        animation();
    }

    @Override
    public void onBackPressed() {
        if (tabLayout.getSelectedTabPosition() == Const.TAB_CONVERSATION) {
            if (FragmentConversation.isShowDelete) {
                FragmentConversation.hideDelete();

            } else {
                if (FragmentConversation.isSearch) {
                    FragmentConversation.hideSearch();

                } else
                    showAlertIsCloseApp();
            }
        }
        else if(tabLayout.getSelectedTabPosition() == Const.TAB_FRIENDS){
            if (FragmentFriend.isDelete()) {
                FragmentFriend.hideDelete();

            } else {
                if (FragmentFriend.isSearch()) {
                    FragmentFriend.hideSearch();

                } else
                    showAlertIsCloseApp();
            }
        }
    }

    TabLayout.OnTabSelectedListener changeTab = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            tab.getIcon().setColorFilter(getResources().getColor(R.color.color_tab_select), PorterDuff.Mode.SRC_IN);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            tab.getIcon().setColorFilter(getResources().getColor(R.color.color_tab), PorterDuff.Mode.SRC_IN);
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    public void showAlertIsCloseApp() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Đóng ứng dụng?");
        alertDialogBuilder
                .setMessage("Click Yes để đóng ứng dụng!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

}
