package io.github.wulkanowy.ui.main.attendance;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.ui.base.BasePresenter;
import io.github.wulkanowy.ui.main.OnFragmentIsReadyListener;

public class AttendancePresenter extends BasePresenter<AttendanceContract.View>
        implements AttendanceContract.Presenter {

    private OnFragmentIsReadyListener listener;

    @Inject
    AttendancePresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void onStart(AttendanceContract.View view, OnFragmentIsReadyListener listener) {
        super.onStart(view);
        this.listener = listener;

        if (getView().isMenuVisible()) {
            getView().setActivityTitle();
        }

        this.listener.onFragmentIsReady();
    }

    @Override
    public void onFragmentVisible(boolean isVisible) {
        if (isVisible) {
            getView().setActivityTitle();
        }
    }
}
