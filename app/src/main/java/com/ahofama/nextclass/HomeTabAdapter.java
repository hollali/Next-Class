package com.ahofama.nextclass;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class HomeTabAdapter extends FragmentStateAdapter {

    public HomeTabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new NotificationsFragment();
            case 1: return new MyCourseFragment();
            case 2: return new WishlistFragment();
            case 3: return new ExploreFragment();
            case 4: return new SearchFragment();
            default: return new MyCourseFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5; // now 5 tabs
    }

}
