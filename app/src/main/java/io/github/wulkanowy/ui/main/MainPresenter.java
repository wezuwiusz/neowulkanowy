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
    public void onStart(MainContract.View view) {
        super.onStart(view);
        getView().showProgressBar(true);
        getView().hideActionBar();
    }

    @Override
    public void onTabSelected(int position, boolean wasSelected) {
        if (!wasSelected) {
            getView().setCurrentPage(position);
        }
    }

    @Override
    public void onFragmentIsReady() {
        if (fragmentCount < 5) {
            fragmentCount++;
        }

        if (fragmentCount == 5) {
            getView().showActionBar();
            getView().showProgressBar(false);
        }
    }
}
