package io.github.wulkanowy.ui.main.grades;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import io.github.wulkanowy.ui.main.OnFragmentIsReadyListener;

public class GradesFragment extends BaseFragment implements GradesContract.View {

    @BindView(R.id.grade_fragment_summary_container)
    View summary;

    @BindView(R.id.grade_fragment_details_container)
    View details;

    @BindView(R.id.grade_fragment_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.grade_fragment_summary_recycler)
    RecyclerView summaryRecyclerView;

    @BindView(R.id.grade_fragment_no_item_container)
    View noItemView;

    @BindView(R.id.grade_fragment_swipe_refresh)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.grade_fragment_summary_predicted_average)
    TextView predictedAverage;

    @BindView(R.id.grade_fragment_summary_calculated_average)
    TextView calculatedAverage;

    @BindView(R.id.grade_fragment_summary_final_average)
    TextView finalAverage;

    @Inject
    FlexibleAdapter<GradesHeader> adapter;

    @Inject
    FlexibleAdapter<GradesSummarySubItem> summaryAdapter;

    @Inject
    GradesContract.Presenter presenter;

    int currentSemester = -1;

    public GradesFragment() {
        // empty constructor for fragment
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grades, container, false);
        injectViews(view);

        presenter.attachView(this, (OnFragmentIsReadyListener) getActivity());
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.grades_action_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_semester_switch:
                presenter.onSemesterSwitchActive();
                CharSequence[] items = new CharSequence[]{
                        getResources().getString(R.string.semester_text, 1),
                        getResources().getString(R.string.semester_text, 2),
                };
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.switch_semester)
                        .setNegativeButton(R.string.cancel, null)
                        .setSingleChoiceItems(items, this.currentSemester, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                presenter.onSemesterChange(which);
                                dialog.cancel();
                            }
                        }).show();
                return true;
            case R.id.action_summary_switch:
                boolean isDetailsVisible = details.getVisibility() == View.VISIBLE;

                item.setTitle(isDetailsVisible ? R.string.action_title_details : R.string.action_title_summary);
                details.setVisibility(isDetailsVisible ? View.INVISIBLE : View.VISIBLE);
                summary.setVisibility(isDetailsVisible ? View.VISIBLE : View.INVISIBLE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        noItemView.setVisibility(View.GONE);
        summary.setVisibility(View.INVISIBLE);
        details.setVisibility(View.VISIBLE);

        adapter.setAutoCollapseOnExpand(true);
        adapter.setAutoScrollOnExpand(true);
        adapter.expandItemsAtStartUp();
        summaryAdapter.setDisplayHeadersAtStartUp(true);

        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
        summaryRecyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(view.getContext()));
        summaryRecyclerView.setAdapter(summaryAdapter);
        summaryRecyclerView.setNestedScrollingEnabled(false);

        refreshLayout.setColorSchemeResources(android.R.color.black);
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (presenter != null) {
            presenter.onFragmentVisible(menuVisible);
        }
    }

    @Override
    public void setSummaryAverages(String calculatedValue, String predictedValue, String finalValue) {
        calculatedAverage.setText(calculatedValue);
        predictedAverage.setText(predictedValue);
        finalAverage.setText(finalValue);
    }

    @Override
    public void setActivityTitle() {
        setTitle(getString(R.string.grades_text));
    }

    public void setCurrentSemester(int currentSemester) {
        this.currentSemester = currentSemester;
    }

    @Override
    public void onRefresh() {
        presenter.onRefresh();
    }

    @Override
    public void showNoItem(boolean show) {
        noItemView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void hideRefreshingBar() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void updateAdapterList(List<GradesHeader> headerItems) {
        adapter.updateDataSet(headerItems);
    }

    @Override
    public void updateSummaryAdapterList(List<GradesSummarySubItem> summarySubItems) {
        summaryAdapter.updateDataSet(summarySubItems);
    }

    @Override
    public void onRefreshSuccessNoGrade() {
        showMessage(R.string.snackbar_no_grades);
    }

    @Override
    public void onRefreshSuccess(int number) {
        showMessage(getString(R.string.snackbar_new_grade, number));
    }

    @Override
    public void onDestroyView() {
        presenter.detachView();
        super.onDestroyView();
    }
}
