package io.github.wulkanowy.ui.main.timetable;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import io.github.wulkanowy.R;
import io.github.wulkanowy.di.component.FragmentComponent;
import io.github.wulkanowy.ui.base.BaseFragment;

public class TimetableTabFragment extends BaseFragment implements TimetableTabContract.View,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String ARGUMENT_KEY = "date";

    private static final String SAVED_KEY = "isSelected";

    private boolean isPrimary = false;

    private boolean isSelected = false;

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
    FlexibleAdapter<TimetableHeaderItem> adapter;

    public static TimetableTabFragment newInstance(String date) {
        TimetableTabFragment fragmentTab = new TimetableTabFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_KEY, date);
        fragmentTab.setArguments(bundle);

        return fragmentTab;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            isSelected = savedInstanceState.getBoolean(SAVED_KEY, isSelected);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable_tab, container, false);

        FragmentComponent component = getFragmentComponent();
        if (component != null) {
            component.inject(this);
            setButterKnife(ButterKnife.bind(this, view));

            if (getArguments() != null) {
                presenter.setArgumentDate(getArguments().getString(ARGUMENT_KEY));
            }

            presenter.onStart(this, isPrimary);
        }
        return view;
    }

    @Override
    protected void setUpOnViewCreated(View fragmentView) {
        adapter.setAutoCollapseOnExpand(true);
        adapter.setAutoScrollOnExpand(true);
        adapter.expandItemsAtStartUp();

        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(fragmentView.getContext()));
        recyclerView.setAdapter(adapter);

        refreshLayout.setColorSchemeResources(android.R.color.black);
        refreshLayout.setOnRefreshListener(this);

    }

    @Override
    public void updateAdapterList(List<TimetableHeaderItem> headerItems) {
        adapter.updateDataSet(headerItems);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (presenter != null && getView() != null) {
            presenter.onFragmentSelected(isSelected);
        } else if (isSelected) {
            isPrimary = true;
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
        onError(R.string.timetable_refresh_success);
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

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public void onError(String message) {
        if (getActivity() != null) {
            Snackbar.make(getActivity().findViewById(R.id.main_activity_view_pager),
                    message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(SAVED_KEY, isSelected);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        isPrimary = false;
        presenter.onDestroy();
        super.onDestroyView();
    }
}
