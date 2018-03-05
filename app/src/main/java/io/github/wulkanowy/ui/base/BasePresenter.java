package io.github.wulkanowy.ui.base;

import javax.inject.Inject;

import io.github.wulkanowy.data.RepositoryContract;

public class BasePresenter<V extends BaseContract.View> implements BaseContract.Presenter<V> {

    private final RepositoryContract repository;

    private V view;

    @Inject
    public BasePresenter(RepositoryContract repository) {
        this.repository = repository;
    }

    @Override
    public void onStart(V view) {
        this.view = view;
    }

    @Override
    public void onDestroy() {
        view = null;
    }

    protected boolean isViewAttached() {
        return view != null;
    }

    public final RepositoryContract getRepository() {
        return repository;
    }

    public V getView() {
        return view;
    }
}
