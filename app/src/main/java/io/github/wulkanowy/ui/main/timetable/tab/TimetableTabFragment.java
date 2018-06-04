package io.github.wulkanowy.ui.main.timetable.tab;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import io.github.wulkanowy.R;
import io.github.wulkanowy.ui.base.BaseFragment;

public class TimetableTabFragment extends BaseFragment implements TimetableTabContract.View,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String ARGUMENT_KEY = "date";

    @BindView(R.id.timetable_tab_fragment_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.timetable_tab_fragment_swipe_refresh)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.timetable_tab_fragment_progress_bar)
    View progressBar;

    @BindView(R.id.timetable_tab_fragment_no_item_container)
    View noItemView;

    @BindView(R.id.timetable_tab_fragment_no_item_name)
    TextView noItemName;

    @Inject
    TimetableTabContract.Presenter presenter;

    @Inject
    FlexibleAdapter<TimetableHeader> adapter;

    private boolean isFragmentVisible = false;

    public static TimetableTabFragment newInstance(String date) {
        TimetableTabFragment fragmentTab = new TimetableTabFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_KEY, date);
        fragmentTab.setArguments(bundle);

        return fragmentTab;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable_tab, container, false);
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
    public void updateAdapterList(List<TimetableHeader> headerItems) {
        adapter.updateDataSet(headerItems);
    }

    @Override
    public void expandItem(int position) {
        adapter.expand(adapter.getItem(position), true);
        recyclerView.scrollToPosition(position);
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
    public void setFreeWeekName(String text) {
        noItemName.setText(text);
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
