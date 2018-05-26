package io.github.wulkanowy.ui.main.attendance.tab;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import io.github.wulkanowy.R;
import io.github.wulkanowy.ui.base.BaseFragment;

public class AttendanceTabFragment extends BaseFragment implements AttendanceTabContract.View,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String ARGUMENT_KEY = "date";

    @BindView(R.id.attendance_tab_fragment_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.attendance_tab_fragment_swipe_refresh)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.attendance_tab_fragment_progress_bar)
    View progressBar;

    @BindView(R.id.attendance_tab_fragment_no_item_container)
    View noItemView;

    @Inject
    AttendanceTabContract.Presenter presenter;

    @Inject
    FlexibleAdapter<AttendanceHeaderItem> adapter;

    private boolean isFragmentVisible = false;

    public static AttendanceTabFragment newInstance(String date) {
        AttendanceTabFragment fragmentTab = new AttendanceTabFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_KEY, date);
        fragmentTab.setArguments(bundle);

        return fragmentTab;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance_tab, container, false);
        injectViews(view);

        if (getArguments() != null) {
            presenter.setArgumentDate(getArguments().getString(ARGUMENT_KEY));
        }

        presenter.attachView(this);
        presenter.onFragmentActivated(isFragmentVisible);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        adapter.setAutoCollapseOnExpand(true);
        adapter.setAutoScrollOnExpand(true);
        adapter.expandItemsAtStartUp();

        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        refreshLayout.setColorSchemeResources(android.R.color.black);
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void updateAdapterList(List<AttendanceHeaderItem> headerItems) {
        adapter.updateDataSet(headerItems);
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
    public void onRefresh() {
        presenter.onRefresh();
    }

    @Override
    public void onRefreshSuccess() {
        showMessage(R.string.sync_completed);
    }

    @Override
    public void hideRefreshingBar() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void showProgressBar(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void showNoItem(boolean show) {
        noItemView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onDestroyView() {
        presenter.detachView();
        super.onDestroyView();
    }
}
