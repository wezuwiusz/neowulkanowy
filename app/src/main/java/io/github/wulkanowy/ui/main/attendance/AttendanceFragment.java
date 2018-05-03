package io.github.wulkanowy.ui.main.attendance;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.wulkanowy.R;
import io.github.wulkanowy.di.component.FragmentComponent;
import io.github.wulkanowy.ui.base.BaseFragment;
import io.github.wulkanowy.ui.base.BasePagerAdapter;
import io.github.wulkanowy.ui.main.OnFragmentIsReadyListener;

public class AttendanceFragment extends BaseFragment implements AttendanceContract.View {

    private static final String CURRENT_ITEM_KEY = "CurrentItem";

    @BindView(R.id.attendance_fragment_viewpager)
    ViewPager viewPager;

    @BindView(R.id.attendance_fragment_tab_layout)
    TabLayout tabLayout;

    @Inject
    BasePagerAdapter pagerAdapter;

    @Inject
    AttendanceContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);

        FragmentComponent component = getFragmentComponent();
        if (component != null) {
            component.inject(this);
            setButterKnife(ButterKnife.bind(this, view));
            presenter.onStart(this, (OnFragmentIsReadyListener) getActivity());

            if (savedInstanceState != null) {
                presenter.setRestoredPosition(savedInstanceState.getInt(CURRENT_ITEM_KEY));
            }
        }

        return view;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (presenter != null) {
            presenter.onFragmentActivated(menuVisible);
        }
    }

    @Override
    public void setTabDataToAdapter(String date) {
        pagerAdapter.addFragment(AttendanceTabFragment.newInstance(date), date);
    }

    @Override
    public void setAdapterWithTabLayout() {
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void scrollViewPagerToPosition(int position) {
        viewPager.setCurrentItem(position, false);
    }

    @Override
    public void setActivityTitle() {
        setTitle(getString(R.string.attendance_text));
    }

    @Override
    public void onError(String message) {
        if (getActivity() != null) {
            Snackbar.make(getActivity().findViewById(R.id.main_activity_view_pager),
                    message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_ITEM_KEY, viewPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        presenter.onDestroy();
        super.onDestroyView();
    }
}
