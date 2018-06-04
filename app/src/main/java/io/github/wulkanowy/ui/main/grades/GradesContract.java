package io.github.wulkanowy.ui.main.grades;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.List;

import io.github.wulkanowy.ui.base.BaseContract;
import io.github.wulkanowy.ui.main.OnFragmentIsReadyListener;

public interface GradesContract {

    interface View extends BaseContract.View, SwipeRefreshLayout.OnRefreshListener {

        void updateAdapterList(List<GradesHeader> headerItems);

        void updateSummaryAdapterList(List<GradesSummarySubItem> summarySubItems);

        void showNoItem(boolean show);

        void onRefreshSuccessNoGrade();

        void onRefreshSuccess(int number);

        void hideRefreshingBar();

        void setActivityTitle();

        void setCurrentSemester(int semester);

        boolean isMenuVisible();

        void setSummaryAverages(String calculatedValue, String predictedValue, String finalValue );

    }

    interface Presenter extends BaseContract.Presenter<View> {

        void onFragmentVisible(boolean isVisible);

        void onRefresh();

        void attachView(@NonNull View view, OnFragmentIsReadyListener listener);

        void onSemesterChange(int which);

        void onSemesterSwitchActive();
    }
}
