package com.example.intro.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyPagerAdapter extends FragmentPagerAdapter {
    private String TAG = "INTROVERT:" + getClass().getSimpleName();


    MyPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }


    @Override
    public int getCount() {
        return 3;
    }


    @Override // Fragment to display
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return RetroFragment.newInstance(0, "Retro");
            case 1:
            default:
                return TodayFragment.newInstance(1, "Today");
            case 2:
                return FutureFragment.newInstance(2, "Future");
        }
    }


    @Override // Tab's title
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Retro";
            case 1:
                return "Today";
            case 2:
                return "Future";
            default:
                return null;
        }
    }
}