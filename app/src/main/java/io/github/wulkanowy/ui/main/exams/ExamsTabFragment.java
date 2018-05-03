package io.github.wulkanowy.ui.main.exams;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import io.github.wulkanowy.R;
import io.github.wulkanowy.di.component.FragmentComponent;
import io.github.wulkanowy.ui.base.BaseFragment;

public class ExamsTabFragment extends BaseFragment implements ExamsTabContract.View,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String ARGUMENT_KEY = "date";

    @BindView(R.id.exams_tab_fragment_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.exams_tab_fragment_swipe_refresh)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.exams_tab_fragment_progress_bar)
    View progressBar;

    @BindView(R.id.exams_tab_fragment_no_item_container)
    View noItemView;

    @Inject
    ExamsTabContract.Presenter presenter;

    @Inject
    FlexibleAdapter<ExamsSubItem> adapter;

    private boolean isFragmentVisible = false;

    public static ExamsTabFragment newInstance(String date) {
        ExamsTabFragment tabFragment = new ExamsTabFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_KEY, date);
        tabFragment.setArguments(bundle);

        return tabFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exams_tab, container, false);

        FragmentComponent component = getFragmentComponent();
        if (component != null) {
            component.inject(this);
            setButterKnife(ButterKnife.bind(this, view));

            if (getArguments() != null) {
                presenter.setArgumentDate(getArguments().getString(ARGUMENT_KEY));
            }
            presenter.onStart(this);
            presenter.onFragmentActivated(isFragmentVisible);
        }
        return view;
    }

    @Override
    protected void setUpOnViewCreated(View fragmentView) {
        adapter.setDisplayHeadersAtStartUp(true);

        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(fragmentView.getContext()));
        recyclerView.setAdapter(adapter);

        refreshLayout.setColorSchemeResources(android.R.color.black);
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        isFragmentVisible = menuVisible;
        if (presenter != null) {
            presenter.onFragmentActivated(menuVisible);
        }
    }

    @Override
    public void updateAdapterList(List<ExamsSubItem> headerItems) {
        adapter.updateDataSet(headerItems);
    }

    @Override
    public void onRefresh() {
        presenter.onRefresh();
    }

    @Override
    public void onRefreshSuccess() {
        onError(R.string.sync_completed);
    }

    @Override
    public void hideRefreshingBar() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void showNoItem(boolean show) {
        noItemView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void showProgressBar(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onError(String message) {
        if (getActivity() != null) {
            Snackbar.make(getActivity().findViewById(R.id.main_activity_view_pager),
                    message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroy();
    }
}
