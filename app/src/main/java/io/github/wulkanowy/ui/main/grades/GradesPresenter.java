package io.github.wulkanowy.ui.main.grades;

import android.support.annotation.NonNull;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.data.db.dao.entities.Grade;
import io.github.wulkanowy.data.db.dao.entities.Subject;
import io.github.wulkanowy.ui.base.BasePresenter;
import io.github.wulkanowy.ui.main.OnFragmentIsReadyListener;
import io.github.wulkanowy.utils.FabricUtils;
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

    private int semesterName;

    @Inject
    GradesPresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void attachView(@NonNull GradesContract.View view, OnFragmentIsReadyListener listener) {
        super.attachView(view);
        this.listener = listener;

        if (getView().isMenuVisible()) {
            getView().setActivityTitle();
        }

        semesterName = getRepository().getDbRepo().getCurrentSemesterName();
        getView().setCurrentSemester(semesterName - 1);

        if (!isFirstSight) {
            isFirstSight = true;

            reloadGrades();
        }
    }

    @Override
    public void onSemesterSwitchActive() {
        cancelAsyncTasks();
    }

    @Override
    public void onSemesterChange(int which) {
        semesterName = which + 1;
        getView().setCurrentSemester(which);
        reloadGrades();

        Answers.getInstance().logCustom(new CustomEvent("Semester change")
                .putCustomAttribute("Name", semesterName));
    }

    private void reloadGrades() {
        loadingTask = new AbstractTask();
        loadingTask.setOnFirstLoadingListener(this);
        loadingTask.execute();
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
            getView().showNoNetworkMessage();
            getView().hideRefreshingBar();
        }
    }

    @Override
    public void onDoInBackgroundRefresh() throws Exception {
        getRepository().getSyncRepo().syncSubjects(semesterName);
        getRepository().getSyncRepo().syncGrades(semesterName);
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
            reloadGrades();

            int numberOfNewGrades = getRepository().getDbRepo().getNewGrades(semesterName).size();

            if (numberOfNewGrades <= 0) {
                getView().onRefreshSuccessNoGrade();
            } else {
                getView().onRefreshSuccess(numberOfNewGrades);
            }
        } else {
            getView().showMessage(getRepository().getResRepo().getErrorLoginMessage(exception));
        }
        getView().hideRefreshingBar();

        FabricUtils.logRefresh("Grades", result, LocalDate.now().toString());
    }

    @Override
    public void onDoInBackgroundLoading() {
        List<Subject> subjectList = getRepository().getDbRepo().getSubjectList(semesterName);
        boolean isShowSummary = getRepository().getSharedRepo().isShowGradesSummary();

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
        getView().showNoItem(headerItems.isEmpty());
        getView().updateAdapterList(headerItems);
        listener.onFragmentIsReady();
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
