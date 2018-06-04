package io.github.wulkanowy.ui.main.exams.tab;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.data.db.dao.entities.Day;
import io.github.wulkanowy.data.db.dao.entities.Exam;
import io.github.wulkanowy.data.db.dao.entities.Week;
import io.github.wulkanowy.ui.base.BasePresenter;
import io.github.wulkanowy.utils.FabricUtils;
import io.github.wulkanowy.utils.async.AbstractTask;
import io.github.wulkanowy.utils.async.AsyncListeners;

public class ExamsTabPresenter extends BasePresenter<ExamsTabContract.View>
        implements ExamsTabContract.Presenter, AsyncListeners.OnFirstLoadingListener,
        AsyncListeners.OnRefreshListener {

    private AbstractTask refreshTask;

    private AbstractTask loadingTask;

    private List<ExamsSubItem> subItems = new ArrayList<>();

    private String date;

    private boolean isFirstSight = false;

    @Inject
    ExamsTabPresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void attachView(@NonNull ExamsTabContract.View view) {
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
    public void setArgumentDate(String date) {
        this.date = date;
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

        FabricUtils.logRefresh("Exams", result, date);
    }

    @Override
    public void onDoInBackgroundLoading() throws Exception {
        Week week = getRepository().getDbRepo().getWeek(date);

        if (week == null || !week.getExamsSynced()) {
            syncData();
            week = getRepository().getDbRepo().getWeek(date);
        }

        week.resetDayList();
        List<Day> dayList = week.getDayList();

        subItems = new ArrayList<>();

        for (Day day : dayList) {
            day.resetExams();
            ExamsHeader headerItem = new ExamsHeader(day);

            List<Exam> examList = day.getExams();

            for (Exam exam : examList) {
                subItems.add(new ExamsSubItem(headerItem, exam));
            }
        }
    }

    @Override
    public void onCanceledLoadingAsync() {
        // do nothing
    }

    @Override
    public void onEndLoadingAsync(boolean result, Exception exception) {
        if (subItems.isEmpty()) {
            getView().showNoItem(true);
            getView().updateAdapterList(null);
        } else {
            getView().updateAdapterList(subItems);
            getView().showNoItem(false);
        }
        getView().showProgressBar(false);
    }

    private void syncData() throws Exception {
        getRepository().getSyncRepo().syncExams(0, date);
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
        isFirstSight = false;
        cancelAsyncTasks();
        super.detachView();
    }
}
