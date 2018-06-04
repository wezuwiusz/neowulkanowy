package io.github.wulkanowy.ui.main.attendance.tab;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.data.db.dao.entities.AttendanceLesson;
import io.github.wulkanowy.data.db.dao.entities.Day;
import io.github.wulkanowy.data.db.dao.entities.Week;
import io.github.wulkanowy.ui.base.BasePresenter;
import io.github.wulkanowy.utils.FabricUtils;
import io.github.wulkanowy.utils.async.AbstractTask;
import io.github.wulkanowy.utils.async.AsyncListeners;

public class AttendanceTabPresenter extends BasePresenter<AttendanceTabContract.View>
        implements AttendanceTabContract.Presenter, AsyncListeners.OnRefreshListener,
        AsyncListeners.OnFirstLoadingListener {

    private AbstractTask refreshTask;

    private AbstractTask loadingTask;

    private List<AttendanceHeader> headerItems = new ArrayList<>();

    private String date;

    private boolean isFirstSight = false;

    @Inject
    AttendanceTabPresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void attachView(@NonNull AttendanceTabContract.View view) {
        super.attachView(view);

        getView().showProgressBar(true);
        getView().showNoItem(false);
    }

    @Override
    public void onFragmentActivated(boolean isSelected) {
        if (!isFirstSight && isSelected && isViewAttached()) {
            isFirstSight = true;

            loadingTask = new AbstractTask();
            loadingTask.setOnFirstLoadingListener(this);
            loadingTask.execute();
        } else if (!isSelected) {
            cancelAsyncTasks();
        }
    }

    @Override
    public void onRefresh() {
        if (getView().isNetworkConnected()) {
            refreshTask = new AbstractTask();
            refreshTask.setOnRefreshListener(this);
            refreshTask.execute();
        } else {
            getView().showNoNetworkMessage();
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
            getView().showMessage(getRepository().getResRepo().getErrorLoginMessage(exception));
        }
        getView().hideRefreshingBar();

        FabricUtils.logRefresh("Attendance", result, date);
    }

    @Override
    public void onDoInBackgroundLoading() throws Exception {
        Week week = getRepository().getDbRepo().getWeek(date);
        boolean isShowPresent = getRepository().getSharedRepo().isShowAttendancePresent();

        if (week == null || !week.getAttendanceSynced()) {
            syncData();
            week = getRepository().getDbRepo().getWeek(date);
        }

        week.resetDayList();
        List<Day> dayList = week.getDayList();

        headerItems = new ArrayList<>();

        boolean isEmptyWeek = true;

        for (Day day : dayList) {
            day.resetAttendanceLessons();
            AttendanceHeader headerItem = new AttendanceHeader(day);

            if (isEmptyWeek) {
                isEmptyWeek = day.getAttendanceLessons().isEmpty();
            }

            List<AttendanceLesson> lessonList = day.getAttendanceLessons();

            List<AttendanceSubItem> subItems = new ArrayList<>();

            for (AttendanceLesson lesson : lessonList) {
                if (!isShowPresent && lesson.getPresence()) {
                    continue;
                }

                lesson.setDescription(getRepository().getResRepo().getAttendanceLessonDescription(lesson));
                subItems.add(new AttendanceSubItem(headerItem, lesson));
            }

            if (!isShowPresent && subItems.isEmpty()) {
                continue;
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
        getRepository().getSyncRepo().syncAttendance(0, date);
    }

    private void cancelAsyncTasks() {
        if (refreshTask != null) {
            refreshTask.cancel(true);
            refreshTask = null;
        }
        if (loadingTask != null) {
            loadingTask.cancel(true);
            loadingTask = null;
        }
    }

    @Override
    public void detachView() {
        cancelAsyncTasks();
        isFirstSight = false;
        super.detachView();
    }
}
