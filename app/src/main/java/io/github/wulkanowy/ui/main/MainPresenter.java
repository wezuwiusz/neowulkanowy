package io.github.wulkanowy.ui.main;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.ui.base.BasePresenter;

import static io.github.wulkanowy.utils.TimeUtilsKt.isHolidays;

public class MainPresenter extends BasePresenter<MainContract.View>
        implements MainContract.Presenter {

    private int fragmentCount = 0;

    @Inject
    MainPresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void attachView(@NonNull MainContract.View view, int initTabId) {
        super.attachView(view);
        getView().showProgressBar(true);
        getView().hideActionBar();

        int tabPosition;

        if (initTabId != -1) {
            tabPosition = initTabId;
        } else {
            tabPosition = getRepository().getSharedRepo().getStartupTab();
        }

        getView().initiationBottomNav(tabPosition);
        getView().initiationViewPager(tabPosition);

        if (getRepository().getSharedRepo().isServicesEnable() && !isHolidays()) {
            getView().startSyncService(getRepository().getSharedRepo().getServicesInterval(),
                    getRepository().getSharedRepo().isMobileDisable());
        }
    }

    @Override
    public void onTabSelected(int position, boolean wasSelected) {
        if (!wasSelected) {
            getView().setCurrentPage(position);
        }
    }

    @Override
    public void onFragmentIsReady() {
        if (fragmentCount < 4) {
            fragmentCount++;
        }

        if (fragmentCount == 4) {
            getView().showActionBar();
            getView().showProgressBar(false);
        }
    }
}
