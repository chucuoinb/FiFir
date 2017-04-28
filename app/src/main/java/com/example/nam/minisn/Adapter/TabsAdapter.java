package com.example.nam.minisn.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.nam.minisn.Activity.Main;
import com.example.nam.minisn.Fragmen.FragmenConversation;
import com.example.nam.minisn.Fragmen.FragmenFriend;


/**
 * Created by LaiVanNam on 18/11/2015.
 */
public class TabsAdapter extends FragmentPagerAdapter {
    Bundle bundle;
    public TabsAdapter(FragmentManager fm, Bundle bundle) {
        super(fm);
        this.bundle=bundle;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return FragmenConversation.newInstance(bundle);
            case 1:
                return FragmenFriend.newInstance(bundle);
//            case 2:
//                return FragmentStatus.newInstance(bundle);
//            case 3:
//                return FragmentIndividual.newInstance(bundle);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

}
