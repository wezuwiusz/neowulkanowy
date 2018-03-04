package io.github.wulkanowy.ui.main.timetable;

import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

class TabsData {

    private List<Fragment> fragments = new ArrayList<>();

    private List<String> titles = new ArrayList<>();

    Fragment getFragment(int index) {
        return fragments.get(index);
    }

    void addFragment(Fragment fragment) {
        if (fragment != null) {
            fragments.add(fragment);
        }
    }

    int getFragmentsCount() {
        return fragments.size();
    }

    String getTitle(int index) {
        return titles.get(index);
    }

    void addTitle(String title) {
        if (title != null) {
            titles.add(title);
        }
    }
}
