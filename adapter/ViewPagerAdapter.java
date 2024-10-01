package com.example.project_fakebook.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.project_fakebook.framents.HomeFragment;
import com.example.project_fakebook.framents.NotifyFragment;
import com.example.project_fakebook.framents.ProfileFragment;
import com.example.project_fakebook.framents.SettingFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {

        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new ProfileFragment();
            case 2:
                return new NotifyFragment();
            case 3:
                return new SettingFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
