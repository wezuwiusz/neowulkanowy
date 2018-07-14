package io.github.wulkanowy.ui.main.exams;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.ui.base.BasePresenter;
import io.github.wulkanowy.ui.main.OnFragmentIsReadyListener;
import io.github.wulkanowy.utils.async.AbstractTask;
import io.github.wulkanowy.utils.async.AsyncListeners;

import static io.github.wulkanowy.utils.TimeUtilsKt.getFirstDayOfCurrentWeek;
import static io.github.wulkanowy.utils.TimeUtilsKt.getMondaysFromCurrentSchoolYear;

public class ExamsPresenter extends BasePresenter<ExamsContract.View>
        implements ExamsContract.Presenter, AsyncListeners.OnFirstLoadingListener {

    private AbstractTask loadingTask;

    private List<String> dates = new ArrayList<>();

    private OnFragmentIsReadyListener listener;

    private int positionToScroll = 0;

    private boolean isFirstSight = false;

    @Inject
    ExamsPresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void attachView(@NonNull ExamsContract.View view, OnFragmentIsReadyListener listener) {
        super.attachView(view);
        this.listener = listener;

        if (getView().isMenuVisible()) {
            getView().setActivityTitle();
        }

        if (dates.isEmpty()) {
            dates = getMondaysFromCurrentSchoolYear();
        }

        if (positionToScroll == 0) {
            positionToScroll = dates.indexOf(getFirstDayOfCurrentWeek());
        }

        if (!isFirstSight) {
            isFirstSight = true;

            loadingTask = new AbstractTask();
            loadingTask.setOnFirstLoadingListener(this);
            loadingTask.execute();
        }
    }

    @Override
    public void onFragmentActivated(boolean isVisible) {
        if (isVisible) {
            getView().setActivityTitle();
        }
    }

    @Override
    public void setRestoredPosition(int position) {
        this.positionToScroll = position;
    }

    @Override
    public void onDoInBackgroundLoading() {
        for (String date : dates) {
            getView().setTabDataToAdapter(date);
        }
    }

    @Override
    public void onCanceledLoadingAsync() {
        // do nothing
    }

    @Override
    public void onEndLoadingAsync(boolean result, Exception exception) {
        if (result) {
            getView().setAdapterWithTabLayout();
            getView().setThemeForTab(positionToScroll);
            getView().scrollViewPagerToPosition(positionToScroll);
            listener.onFragmentIsReady();
        }
    }

    @Override
    public void detachView() {
        isFirstSight = false;

        if (loadingTask != null) {
            loadingTask.cancel(true);
            loadingTask = null;
        }
        super.detachView();
    }
}
