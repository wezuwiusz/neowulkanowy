package io.github.wulkanowy.ui.main.timetable;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.data.db.dao.entities.Day;
import io.github.wulkanowy.data.db.dao.entities.TimetableLesson;
import io.github.wulkanowy.data.db.dao.entities.Week;
import io.github.wulkanowy.ui.base.BasePresenter;
import io.github.wulkanowy.utils.async.AbstractTask;
import io.github.wulkanowy.utils.async.AsyncListeners;

public class TimetableTabPresenter extends BasePresenter<TimetableTabContract.View>
        implements TimetableTabContract.Presenter, AsyncListeners.OnRefreshListener,
        AsyncListeners.OnFirstLoadingListener {

    private AbstractTask refreshTask;

    private AbstractTask loadingTask;

    private List<TimetableHeaderItem> headerItems = new ArrayList<>();

    private String date;

    private String freeWeekName;

    private boolean isFirstSight = false;

    @Inject
    TimetableTabPresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void onStart(TimetableTabContract.View view, boolean isPrimary) {
        super.onStart(view);
        getView().showProgressBar(true);
        getView().showNoItem(false);
        onFragmentSelected(isPrimary);
    }

    @Override
    public void onFragmentSelected(boolean isSelected) {
        if (!isFirstSight && isSelected) {
            isFirstSight = true;

            loadingTask = new AbstractTask();
            loadingTask.setOnFirstLoadingListener(this);
            loadingTask.execute();
        }
    }

    @Override
    public void onRefresh() {
        if (getView().isNetworkConnected()) {
            refreshTask = new AbstractTask();
            refreshTask.setOnRefreshListener(this);
            refreshTask.execute();
        } else {
            getView().onNoNetworkError();
            getView().hideRefreshingBar();
        }
    }

    @Override
    public void onDoInBackgroundRefresh() throws Exception {
        syncData();
    }

    @Override
    public void onCanceledRefreshAsync() {
        if (isViewAttached()) {
            getView().hideRefreshingBar();
        }
    }

    @Override
    public void onEndRefreshAsync(boolean result, Exception exception) {
        if (result) {
            loadingTask = new AbstractTask();
            loadingTask.setOnFirstLoadingListener(this);
            loadingTask.execute();

            getView().onRefreshSuccess();
        } else {
            getView().onError(getRepository().getErrorLoginMessage(exception));
        }
        getView().hideRefreshingBar();
    }

    @Override
    public void onDoInBackgroundLoading() throws Exception {
        Week week = getRepository().getWeek(date);

        if (week == null || !week.getIsTimetableSynced()) {
            syncData();
            week = getRepository().getWeek(date);
        }

        List<Day> dayList = week.getDayList();

        headerItems = new ArrayList<>();

        boolean isFreeWeek = true;

        for (Day day : dayList) {
            TimetableHeaderItem headerItem = new TimetableHeaderItem(day);

            if (isFreeWeek) {
                isFreeWeek = day.getIsFreeDay();
            }

            List<TimetableLesson> lessonList = day.getTimetableLessons();

            List<TimetableSubItem> subItems = new ArrayList<>();

            for (TimetableLesson lesson : lessonList) {
                subItems.add(new TimetableSubItem(headerItem, lesson));
            }

            headerItem.setSubItems(subItems);
            headerItem.setExpanded(false);
            headerItems.add(headerItem);
        }

        if (isFreeWeek) {
            freeWeekName = dayList.get(4).getFreeDayName();
            headerItems = new ArrayList<>();
        }
    }

    @Override
    public void onCanceledLoadingAsync() {
        // do nothing
    }

    @Override
    public void onEndLoadingAsync(boolean result, Exception exception) {
        if (headerItems.isEmpty()) {
            getView().showNoItem(true);
            getView().setFreeWeekName(freeWeekName);
            getView().updateAdapterList(null);
        } else {
            getView().updateAdapterList(headerItems);
            getView().showNoItem(false);
        }
        getView().showProgressBar(false);
    }

    @Override
    public void setArgumentDate(String date) {
        this.date = date;
    }

    private void syncData() throws Exception {
        getRepository().syncTimetable(date);
    }

    @Override
    public void onDestroy() {
        isFirstSight = false;

        if (refreshTask != null) {
            refreshTask.cancel(true);
            refreshTask = null;
        }
        if (loadingTask != null) {
            loadingTask.cancel(true);
            loadingTask = null;
        }
        super.onDestroy();
    }
}
