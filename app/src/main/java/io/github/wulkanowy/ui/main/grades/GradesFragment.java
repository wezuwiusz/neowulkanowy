package io.github.wulkanowy.ui.main.grades;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import io.github.wulkanowy.ui.main.OnFragmentIsReadyListener;

public class GradesFragment extends BaseFragment implements GradesContract.View {

    @BindView(R.id.grade_fragment_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.grade_fragment_no_item_container)
    View noItemView;

    @BindView(R.id.grade_fragment_swipe_refresh)
    SwipeRefreshLayout refreshLayout;

    @Inject
    FlexibleAdapter<GradeHeaderItem> adapter;

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

        FragmentComponent component = getFragmentComponent();
        if (component != null) {
            component.inject(this);
            setButterKnife(ButterKnife.bind(this, view));
            presenter.onStart(this, (OnFragmentIsReadyListener) getActivity());
        }

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.semester_switch, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
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
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void setUpOnViewCreated(View fragmentView) {
        noItemView.setVisibility(View.GONE);

        adapter.setAutoCollapseOnExpand(true);
        adapter.setAutoScrollOnExpand(true);
        adapter.expandItemsAtStartUp();

        recyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(fragmentView.getContext()));
        recyclerView.setAdapter(adapter);

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
    public void updateAdapterList(List<GradeHeaderItem> headerItems) {
        adapter.updateDataSet(headerItems);
    }

    @Override
    public void onRefreshSuccessNoGrade() {
        onError(R.string.snackbar_no_grades);
    }

    @Override
    public void onRefreshSuccess(int number) {
        onError(getString(R.string.snackbar_new_grade, number));
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
        presenter.onDestroy();
        super.onDestroyView();
    }
}
