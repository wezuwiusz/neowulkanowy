package io.github.wulkanowy.ui.main.attendance;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.data.db.dao.entities.AttendanceLesson;
import io.github.wulkanowy.data.db.dao.entities.Day;
import io.github.wulkanowy.data.db.dao.entities.Week;
import io.github.wulkanowy.ui.base.BasePresenter;
import io.github.wulkanowy.utils.async.AbstractTask;
import io.github.wulkanowy.utils.async.AsyncListeners;

public class AttendanceTabPresenter extends BasePresenter<AttendanceTabContract.View>
        implements AttendanceTabContract.Presenter, AsyncListeners.OnRefreshListener,
        AsyncListeners.OnFirstLoadingListener {

    private AbstractTask refreshTask;

    private AbstractTask loadingTask;

    private List<AttendanceHeaderItem> headerItems = new ArrayList<>();

    private String date;

    private boolean isFirstSight = false;

    @Inject
    AttendanceTabPresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void onStart(AttendanceTabContract.View view, boolean isPrimary) {
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

        if (week == null || !week.getIsAttendanceSynced()) {
            syncData();
            week = getRepository().getWeek(date);
        }

        List<Day> dayList = week.getDayList();

        headerItems = new ArrayList<>();

        boolean isEmptyWeek = true;

        for (Day day : dayList) {
            AttendanceHeaderItem headerItem = new AttendanceHeaderItem(day);

            if (isEmptyWeek) {
                isEmptyWeek = day.getAttendanceLessons().isEmpty();
            }

            List<AttendanceLesson> lessonList = day.getAttendanceLessons();

            List<AttendanceSubItem> subItems = new ArrayList<>();

            for (AttendanceLesson lesson : lessonList) {
                lesson.setDescription(getRepository().getAttendanceLessonDescription(lesson));
                subItems.add(new AttendanceSubItem(headerItem, lesson));
            }

            headerItem.setSubItems(subItems);
            headerItem.setExpanded(false);
            headerItems.add(headerItem);
        }

        if (isEmptyWeek) {
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
        getRepository().loginCurrentUser();
        getRepository().syncAttendance(date);
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
