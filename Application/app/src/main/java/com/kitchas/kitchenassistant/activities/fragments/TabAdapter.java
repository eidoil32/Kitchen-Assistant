package com.kitchas.kitchenassistant.activities.fragments;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.kitchas.kitchenassistant.assistant.user.User;

public class TabAdapter extends FragmentPagerAdapter {
    private Context context;
    private int total_tabs;

    public TabAdapter(Context context, FragmentManager manager, int totalTabs) {
        super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
        this.total_tabs = totalTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                HomeFragment home_fragment = new HomeFragment();
                return home_fragment;
            case 1:
                CommunityFragment community_fragment = new CommunityFragment();
                return community_fragment;
            case 2:
                FavoritesFragment favorites_fragment = new FavoritesFragment();
                return favorites_fragment;
            case 3:
                User user = User.getInstance(this.context);
                ProfileFragment profile_fragment = new ProfileFragment(user);
                return profile_fragment;
            default:
                return null;
        }
    }

    public int getCount() {
        return total_tabs;
    }
}
