package com.example.android.cyclebuddy;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainPagerAdapter extends FragmentPagerAdapter{

    private Context context;

    private String tabTitles[] = new String[]{"Ride", "Search", "Offer", "Messages"};

    public MainPagerAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new RideFragment();
        } else if (position == 1) {
            return new SearchFragment();
        } else if (position == 2) {
            return new OfferFragment();
        } else {
            return new MessagesFragment();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getCount() {
        return 4;
    }
}
