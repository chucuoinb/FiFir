package com.example.nam.minisn.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.example.nam.minisn.Adapter.TabsAdapter;
import com.example.nam.minisn.R;
import com.example.nam.minisn.Util.Const;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class Main extends AppCompatActivity {

    private TabsAdapter mSectionsPagerAdapter;
    private Intent intent;
    private Bundle bundle;
    public static int[] iconTabs = {R.drawable.chat_24,
            R.drawable.group_24,
            R.drawable.globe_24,
            R.drawable.guest_24};
    private ViewPager mViewPager;
    public static TabLayout tabLayout;

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
        tabLayout.setupWithViewPager(mViewPager);
        for (int i = 0; i < 2; i++) {
            tabLayout.getTabAt(i).setIcon(iconTabs[i]);
        }
    }

    public static void changeIcon(int position){
        for (int i = 0; i < 2; i++) {
            if (i!= position)
                tabLayout.getTabAt(i).setIcon(iconTabs[i]);
            else
                tabLayout.getTabAt(i).setIcon(R.drawable.globe_24);
        }

    }
}
