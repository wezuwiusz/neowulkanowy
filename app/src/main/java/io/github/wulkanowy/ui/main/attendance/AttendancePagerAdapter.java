package io.github.wulkanowy.ui.main.attendance;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import io.github.wulkanowy.ui.main.TabsData;

public class AttendancePagerAdapter extends FragmentStatePagerAdapter {

    private TabsData tabsData;

    public AttendancePagerAdapter(FragmentManager fragmentManager) {
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
