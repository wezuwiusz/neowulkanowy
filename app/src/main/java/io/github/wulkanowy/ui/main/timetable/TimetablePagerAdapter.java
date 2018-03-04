package io.github.wulkanowy.ui.main.timetable;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TimetablePagerAdapter extends FragmentStatePagerAdapter {

    private TabsData tabsData;

    public TimetablePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    void setTabsData(TabsData tabsData) {
        this.tabsData = tabsData;
    }

    @Override
    public Fragment getItem(int position) {
        return tabsData.getFragment(position);
    }


    @Override
    public int getCount() {
        return tabsData.getFragmentsCount();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabsData.getTitle(position);
    }
}
