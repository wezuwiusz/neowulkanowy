package io.github.wulkanowy.ui.main;

import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class TabsData {

    private List<Fragment> fragments = new ArrayList<>();

    private List<String> titles = new ArrayList<>();

    public Fragment getFragment(int index) {
        return fragments.get(index);
    }

    public void addFragment(Fragment fragment) {
        if (fragment != null) {
            fragments.add(fragment);
        }
    }

    public int getFragmentsCount() {
        return fragments.size();
    }

    public String getTitle(int index) {
        return titles.get(index);
    }

    public void addTitle(String title) {
        if (title != null) {
            titles.add(title);
        }
    }
}
