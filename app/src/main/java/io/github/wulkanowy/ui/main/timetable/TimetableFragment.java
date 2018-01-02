package io.github.wulkanowy.ui.main.timetable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.R;
import io.github.wulkanowy.utils.TimeUtils;

public class TimetableFragment extends Fragment {

    private final String DATE_PATTERN = "yyyy-MM-dd";

    private List<String> dateStringList = new ArrayList<>();

    private TimetablePagerAdapter pagerAdapter;

    private ViewPager viewPager;

    private TabLayout tabLayout;

    public TimetableFragment() {
        //empty constructor for fragment
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);
        viewPager = view.findViewById(R.id.timetable_fragment_viewpager);
        tabLayout = view.findViewById(R.id.timetable_fragment_tab);
        new CreateTabTask(this).execute();
        return view;
    }

    private TimetablePagerAdapter getPagerAdapter() {
        TimetablePagerAdapter pagerAdapter = new TimetablePagerAdapter(getChildFragmentManager());
        for (String date : dateStringList) {
            pagerAdapter.addFragment(TimetableFragmentTab.newInstance(date), date);
        }
        return pagerAdapter;
    }

    private String getDateOfCurrentMonday() {
        DateTime currentDate = new DateTime();

        if (currentDate.getDayOfWeek() == DateTimeConstants.SATURDAY) {
            currentDate = currentDate.plusDays(2);
        } else if (currentDate.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            currentDate = currentDate.plusDays(1);
        } else {
            currentDate = currentDate.withDayOfWeek(DateTimeConstants.MONDAY);
        }
        return currentDate.toString(DATE_PATTERN);
    }

    private void setAdapterOnViewPager() {
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(dateStringList.indexOf(getDateOfCurrentMonday()));
    }

    private void setDateStringList() {
        if (dateStringList.isEmpty()) {
            dateStringList = TimeUtils.getMondaysFromCurrentSchoolYear(DATE_PATTERN);
        }
    }

    private void setViewPagerOnTabLayout() {
        tabLayout.setupWithViewPager(viewPager);
    }

    protected final void setLoadingBarInvisible() {
        if (getView() != null) {
            getView().findViewById(R.id.timetable_tab_progress_bar).setVisibility(View.GONE);
        }
    }

    private static class CreateTabTask extends AsyncTask<Void, Void, Void> {

        private TimetableFragment fragment;

        public CreateTabTask(TimetableFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            fragment.setDateStringList();
            fragment.pagerAdapter = fragment.getPagerAdapter();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            fragment.setAdapterOnViewPager();
            fragment.setViewPagerOnTabLayout();
            fragment.setLoadingBarInvisible();
        }
    }
}