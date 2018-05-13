package io.github.wulkanowy.ui.main;


import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.ui.base.BasePresenter;

public class MainPresenter extends BasePresenter<MainContract.View>
        implements MainContract.Presenter {

    private int fragmentCount = 0;

    @Inject
    MainPresenter(RepositoryContract repository) {
        super(repository);
    }

    @Override
    public void onStart(MainContract.View view, int tabPositionIntent) {
        super.onStart(view);
        getView().showProgressBar(true);
        getView().hideActionBar();

        int tabPosition;

        if (tabPositionIntent != -1) {
            tabPosition = tabPositionIntent;
        } else {
            tabPosition = getRepository().getSharedRepo().getStartupTab();
        }

        getView().initiationBottomNav(tabPosition);
        getView().initiationViewPager(tabPosition);

        if (getRepository().getSharedRepo().isServicesEnable()) {
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
