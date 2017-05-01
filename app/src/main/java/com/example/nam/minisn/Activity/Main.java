package com.example.nam.minisn.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;

import com.example.nam.minisn.Adapter.TabsAdapter;
import com.example.nam.minisn.Fragmen.FragmenConversation;
import com.example.nam.minisn.ItemListview.Conversation;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

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

//    private FloatingActionButton fabMain,fabConversation,fabFriend,fabRequestFriend;
//    private Animation show_fab1, hide_fab1,show_fab2, hide_fab2,show_fab3, hide_fab3;
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

//        fabMain = (FloatingActionButton)findViewById(R.id.main_fab);
//        fabConversation = (FloatingActionButton)findViewById(R.id.fab_1);
//        fabFriend = (FloatingActionButton)findViewById(R.id.fab_2);
//        fabRequestFriend = (FloatingActionButton)findViewById(R.id.fab_3);

        intent = getIntent();
        bundle = intent.getBundleExtra(Const.PACKAGE);
        tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mViewPager = (ViewPager) findViewById(R.id.main_viewPager);
        mSectionsPagerAdapter = new TabsAdapter(getSupportFragmentManager(), bundle);
        mViewPager.setAdapter(mSectionsPagerAdapter);
//        mViewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setOnTabSelectedListener(changeTab);
        tabLayout.getTabAt(0).setIcon(iconTabs[0]);
        tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.color_tab_select),PorterDuff.Mode.SRC_IN);
        for (int i = 1; i < 2; i++) {
            tabLayout.getTabAt(i).setIcon(iconTabs[i]);
            tabLayout.getTabAt(i).getIcon().setColorFilter(getResources().getColor(R.color.color_tab),PorterDuff.Mode.SRC_IN);
        }
//        animation();
    }

    @Override
    public void onBackPressed() {
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

    TabLayout.OnTabSelectedListener changeTab = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            tab.getIcon().setColorFilter(getResources().getColor(R.color.color_tab_select),PorterDuff.Mode.SRC_IN);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            tab.getIcon().setColorFilter(getResources().getColor(R.color.color_tab), PorterDuff.Mode.SRC_IN);
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

//    public void animation(){
//        show_fab1 = AnimationUtils.loadAnimation(getApplication(),R.anim.fab1_show);
//        hide_fab1 = AnimationUtils.loadAnimation(getApplication(),R.anim.fab1_hide);
//        show_fab2 = AnimationUtils.loadAnimation(getApplication(),R.anim.fab2_show);
//        hide_fab2 = AnimationUtils.loadAnimation(getApplication(),R.anim.fab2_hide);
//        show_fab3 = AnimationUtils.loadAnimation(getApplication(),R.anim.fab3_show);
//        hide_fab3 = AnimationUtils.loadAnimation(getApplication(),R.anim.fab3_hide);
//
//
////        hideFab();
//    }
//
//    public void showSubMenu(){
//        fabMain.startAnimation(AnimationUtils.loadAnimation(getApplication(),R.anim.rotate_forward));
//        showEachFab(fabConversation,show_fab1,(float)1.7,(float)0.25);
//        showEachFab(fabFriend,show_fab2,(float) 1.5,(float) 1.5);
//        showEachFab(fabRequestFriend,show_fab3,(float)0.25,(float)1.7);
//    }
//    public void hideSubMenu(){
//        fabMain.startAnimation(AnimationUtils.loadAnimation(getApplication(),R.anim.rotate_backward));
//        hideEachFab(fabConversation,hide_fab1,(float)1.7,(float)0.25);
//        hideEachFab(fabFriend,hide_fab2,(float) 1.5,(float) 1.5);
//        hideEachFab(fabRequestFriend,hide_fab3,(float)0.25,(float)1.7);
//    }
//    public void showEachFab(FloatingActionButton fab, Animation anim, float x, float y){
//        fab.startAnimation(anim);
//        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)fab.getLayoutParams();
//        layoutParams.rightMargin += (int) (fab.getWidth() * x);
//        layoutParams.bottomMargin += (int) (fab.getHeight() * y);
//        fab.setLayoutParams(layoutParams);
//        fab.setVisibility(View.VISIBLE);
//        fab.setClickable(true);
//    }
//    public void hideEachFab(FloatingActionButton fab, Animation anim,float x, float y){
//        fab.startAnimation(anim);
//        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab.getLayoutParams();
//        layoutParams.rightMargin -= (int) (fab.getWidth() * x);
//        layoutParams.bottomMargin -= (int) (fab.getHeight() * y);
//        fab.setLayoutParams(layoutParams);
//        fab.setVisibility(View.INVISIBLE);
//        fab.setClickable(false);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.main_fab:
//                if (isOpenSubMenu){
//                    hideSubMenu();
//                    isOpenSubMenu = false;
//                }
//                else{
//                    showSubMenu();
//                    isOpenSubMenu = true;
//                }
//                break;
//            case R.id.fab_3:
////                Intent intent = new Intent(getApplicationContext(),RequestFriendActivity.class);
////                startActivity(intent);
//                break;
//            case R.id.fab_2:
//                FragmenConversation.conversationTab.setVisibility(View.INVISIBLE);
//                break;
//            case R.id.fab_1:
//                fab1Click();
////                Toast.makeText(Main.this, String.valueOf(tabLayout.getSelectedTabPosition()), Toast.LENGTH_SHORT).show();
//                break;
//        }
//    }
//
//    public void fab1Click(){
//        switch (tabLayout.getSelectedTabPosition()){
//            case 0:
//                searchConversation();
//                break;
//        }
//    }
//    public void searchConversation(){
//        Intent intent = new Intent(Main.this,SearchConversation.class);
//        startActivity(intent);
//    }
}
