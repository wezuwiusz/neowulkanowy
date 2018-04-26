package io.github.wulkanowy.ui.main.grades;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.data.db.dao.entities.Grade;
import io.github.wulkanowy.data.db.dao.entities.Subject;
import io.github.wulkanowy.ui.base.BasePresenter;
import io.github.wulkanowy.ui.main.OnFragmentIsReadyListener;
import io.github.wulkanowy.utils.async.AbstractTask;
import io.github.wulkanowy.utils.async.AsyncListeners;

public class GradesPresenter extends BasePresenter<GradesContract.View>
        implements GradesContract.Presenter, AsyncListeners.OnRefreshListener,
        AsyncListeners.OnFirstLoadingListener {

    private AbstractTask refreshTask;

    private AbstractTask loadingTask;

    private OnFragmentIsReadyListener listener;

    private List<GradeHeaderItem> headerItems = new ArrayList<>();

    private boolean isFirstSight = false;

    @Inject
    GradesPresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void onStart(GradesContract.View view, OnFragmentIsReadyListener listener) {
        super.onStart(view);
        this.listener = listener;

        if (getView().isMenuVisible()) {
            getView().setActivityTitle();
        }

        if (!isFirstSight) {
            isFirstSight = true;

            loadingTask = new AbstractTask();
            loadingTask.setOnFirstLoadingListener(this);
            loadingTask.execute();
        }
    }

    @Override
    public void onFragmentVisible(boolean isVisible) {
        if (isVisible) {
            getView().setActivityTitle();
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
        getRepository().syncSubjects();
        getRepository().syncGrades();
    }

    @Override
    public void onCanceledRefreshAsync() {
        if (isViewAttached()) {
            getView().hideRefreshingBar();
        }
    }

    @Override
    public void onEndRefreshAsync(boolean success, Exception exception) {
        if (success) {
            loadingTask = new AbstractTask();
            loadingTask.setOnFirstLoadingListener(this);
            loadingTask.execute();

            int numberOfNewGrades = getRepository().getNewGrades().size();

            if (numberOfNewGrades <= 0) {
                getView().onRefreshSuccessNoGrade();
            } else {
                getView().onRefreshSuccess(numberOfNewGrades);
            }
        } else {
            getView().onError(getRepository().getErrorLoginMessage(exception));
        }
        getView().hideRefreshingBar();
    }

    @Override
    public void onDoInBackgroundLoading() {
        List<Subject> subjectList = getRepository().getSubjectList();
        boolean isShowSummary = getRepository().isShowGradesSummary();

        headerItems = new ArrayList<>();

        for (Subject subject : subjectList) {
            subject.resetGradeList();
            List<Grade> gradeList = subject.getGradeList();

            if (!gradeList.isEmpty()) {
                GradeHeaderItem headerItem = new GradeHeaderItem(subject, isShowSummary);

                List<GradesSubItem> subItems = new ArrayList<>();

                for (Grade grade : gradeList) {
                    subItems.add(new GradesSubItem(headerItem, grade));
                }

                headerItem.setSubItems(subItems);
                headerItem.setExpanded(false);
                headerItems.add(headerItem);
            }
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
        } else {
            getView().updateAdapterList(headerItems);
            getView().showNoItem(false);
        }
        listener.onFragmentIsReady();
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
