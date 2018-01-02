package io.github.wulkanowy.ui.main.timetable;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.R;
import io.github.wulkanowy.db.dao.entities.Day;
import io.github.wulkanowy.db.dao.entities.Lesson;
import io.github.wulkanowy.db.dao.entities.Week;
import io.github.wulkanowy.db.dao.entities.WeekDao;
import io.github.wulkanowy.services.sync.VulcanSync;
import io.github.wulkanowy.ui.main.AbstractFragment;

public class TimetableFragmentTab extends AbstractFragment<TimetableHeaderItem> {

    private final String DATE_PATTERN = "yyyy-MM-dd";

    private int positionToScroll;

    private String date;

    public static TimetableFragmentTab newInstance(String date) {
        TimetableFragmentTab fragmentTab = new TimetableFragmentTab();

        Bundle argument = new Bundle();
        argument.putString("date", date);
        fragmentTab.setArguments(argument);

        return fragmentTab;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            date = getArguments().getString("date");
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_timetable_tab;
    }

    @Override
    public int getRecyclerViewId() {
        return R.id.timetable_recycler;
    }

    @Override
    public int getRefreshLayoutId() {
        return R.id.timetable_refresh_layout;
    }

    @Override
    public int getLoadingBarId() {
        return R.id.timetable_progress_bar;
    }

    @NonNull
    @Override
    public List<TimetableHeaderItem> getItems() throws Exception {
        Week week = getWeek();

        if (week == null) {
            onRefresh();
            return getItems();
        }

        List<Day> dayEntityList = week.getDayList();

        List<TimetableHeaderItem> dayList = new ArrayList<>();

        int iterator = -1;

        for (Day day : dayEntityList) {
            List<TimetableSubItem> timetableSubItems = new ArrayList<>();

            TimetableHeaderItem headerItem = new TimetableHeaderItem(day);

            for (Lesson lesson : day.getLessons()) {
                TimetableSubItem subItem = new TimetableSubItem(headerItem, lesson, getFragmentManager());
                timetableSubItems.add(subItem);
            }

            iterator++;

            boolean isExpanded = getExpanded(day.getDate());

            if (isExpanded) {
                positionToScroll = iterator;
            }

            headerItem.setExpanded(isExpanded);
            headerItem.setSubItems(timetableSubItems);
            dayList.add(headerItem);
        }
        return dayList;
    }

    @Override
    protected void setAdapterOnRecyclerView(@NonNull RecyclerView recyclerView) {
        super.setAdapterOnRecyclerView(recyclerView);
        recyclerView.scrollToPosition(positionToScroll);
    }

    @Override
    public void onRefresh() throws Exception {
        VulcanSync synchronization = new VulcanSync();
        synchronization.loginCurrentUser(getContext(), getDaoSession());
        synchronization.syncTimetable(date);
    }

    @Override
    public void onPostRefresh(int stringResult) {
        if (stringResult == 0) {
            stringResult = R.string.timetable_refresh_success;
        }
        Snackbar.make(getActivityWeakReference().findViewById(R.id.fragment_container),
                stringResult, Snackbar.LENGTH_SHORT).show();
    }

    private boolean getExpanded(String dayDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DATE_PATTERN);
        DateTime dayTime = dateTimeFormatter.parseDateTime(dayDate);

        DateTime currentDate = new DateTime();

        if (currentDate.getDayOfWeek() == DateTimeConstants.SATURDAY) {
            currentDate = currentDate.plusDays(2);
        } else if (currentDate.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            currentDate = currentDate.plusDays(1);
        }

        return DateTimeComparator.getDateOnlyInstance().compare(currentDate, dayTime) == 0;
    }

    private Week getWeek() {
        if (date == null) {
            LocalDate currentMonday = new LocalDate().withDayOfWeek(DateTimeConstants.MONDAY);
            date = currentMonday.toString(DATE_PATTERN);
        }
        return getDaoSession().getWeekDao().queryBuilder()
                .where(WeekDao.Properties.StartDayDate.eq(date),
                        WeekDao.Properties.UserId.eq(getUserId()))
                .unique();
    }
}