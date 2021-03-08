package com.milosvalovic.sigfoxparking.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.milosvalovic.sigfoxparking.R;
import com.milosvalovic.sigfoxparking.activities.BaseActivity;
import com.milosvalovic.sigfoxparking.activities.main.ARFragment;
import com.milosvalovic.sigfoxparking.activities.main.ListFragment;
import com.milosvalovic.sigfoxparking.activities.main.MainActivity;
import com.milosvalovic.sigfoxparking.activities.main.MapFragment;

public class MainScreenAdapter extends FragmentStatePagerAdapter {

    MainActivity act;
    public ListFragment listFragment;
    public MapFragment mapFragment;
    public ARFragment arFragment;
    public int currentIndex = 0;

    public MainScreenAdapter(@NonNull FragmentManager fm, MainActivity act) {
        super(fm);
        this.act = act;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        currentIndex = position;
        switch (position){
            case 0:
                if(listFragment == null)
                    listFragment = new ListFragment(act);
                return listFragment;
            case 1:
                if(mapFragment == null)
                    mapFragment = new MapFragment(act);
                return mapFragment;
            default:
                return getItem(0);

        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return act.getString(R.string.list);
            case 1:
                return act.getString(R.string.map);
            case 2:
                return act.getString(R.string.ar);
            default:
                return act.getString(R.string.list);

        }
    }
}
