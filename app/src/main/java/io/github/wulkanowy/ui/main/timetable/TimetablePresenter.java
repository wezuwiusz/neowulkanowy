package io.github.wulkanowy.ui.main.timetable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.ui.base.BasePresenter;
import io.github.wulkanowy.ui.main.OnFragmentIsReadyListener;
import io.github.wulkanowy.utils.TimeUtils;
import io.github.wulkanowy.utils.async.AbstractTask;
import io.github.wulkanowy.utils.async.AsyncListeners;

public class TimetablePresenter extends BasePresenter<TimetableContract.View>
        implements TimetableContract.Presenter, AsyncListeners.OnFirstLoadingListener {

    private AbstractTask loadingTask;

    private List<String> dates = new ArrayList<>();

    private OnFragmentIsReadyListener listener;

    private int positionToScroll = 0;

    private boolean isFirstSight = false;

    @Inject
    TimetablePresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void onStart(TimetableContract.View view, OnFragmentIsReadyListener listener) {
        super.onStart(view);
        this.listener = listener;

        if (getView().isMenuVisible()) {
            getView().setActivityTitle();
        }

        if (dates.isEmpty()) {
            dates = TimeUtils.getMondaysFromCurrentSchoolYear();
        }

        if (positionToScroll == 0) {
            positionToScroll = dates.indexOf(TimeUtils.getDateOfCurrentMonday(true));
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
    public void onDoInBackgroundLoading() throws Exception {
        for (String date : dates) {
            getView().setTabDataToAdapter(date);
        }
    }

    @Override
    public void onCanceledLoadingAsync() {
        //do nothing
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
    public void setRestoredPosition(int position) {
        this.positionToScroll = position;
    }

    @Override
    public void onDestroy() {
        isFirstSight = false;

        if (loadingTask != null) {
            loadingTask.cancel(true);
            loadingTask = null;
        }
        super.onDestroy();
    }
}
