package com.example.nam.minisn.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.nam.minisn.Fragmen.FragmentConversation;
import com.example.nam.minisn.Fragmen.FragmentFriend;
import com.example.nam.minisn.Fragmen.FragmentStatus;
import com.example.nam.minisn.Util.Const;


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
            case Const.TAB_CONVERSATION:
                return FragmentConversation.newInstance(bundle);
            case Const.TAB_FRIENDS:
                return FragmentFriend.newInstance(bundle);
            case Const.TAB_STATUS:
                return FragmentStatus.newInstance(bundle);
        }
        return null;
    }

    @Override
    public int getCount() {
        return Const.TAB_SIZE;
    }

}
