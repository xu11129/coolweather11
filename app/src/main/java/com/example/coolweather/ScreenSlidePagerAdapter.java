package com.example.coolweather;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    List<CityWeatherFragment> fragmentList;
    public ScreenSlidePagerAdapter(FragmentManager fragmentManager, List<CityWeatherFragment> list){
        super(fragmentManager);
        this.fragmentList=list;
    }
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
   /*
    public Fragment createFragment(int position){
        return new CityWeatherFragment();
    }
    public int getItemCount(){
        return fragmentList.size();
    }

    */
   int childCount = 0;   //表示ViewPager包含的页数
    //    当ViewPager的页数发生改变时，必须要重写两个函数
    @Override
    public void notifyDataSetChanged() {
        this.childCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (childCount>0) {
            childCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
}
