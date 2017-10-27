package io.github.wulkanowy.activity.dashboard.grades;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.WulkanowyApp;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.dao.DatabaseAccess;
import io.github.wulkanowy.dao.entities.Account;
import io.github.wulkanowy.dao.entities.AccountDao;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.dao.entities.Grade;
import io.github.wulkanowy.dao.entities.Subject;
import io.github.wulkanowy.services.LoginSession;
import io.github.wulkanowy.services.VulcanSynchronization;
import io.github.wulkanowy.services.jobs.VulcanJobHelper;
import io.github.wulkanowy.utilities.ConnectionUtilities;

public class GradesFragment extends Fragment {

    private List<SubjectWithGrades> subjectWithGradesList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;

    private View view;

    private RefreshTask refreshTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_grades, container, false);

        swipeRefreshLayout = view.findViewById(R.id.grade_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.black,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!ConnectionUtilities.isOnline(view.getContext())) {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(view.getContext(), R.string.noInternet_text, Toast.LENGTH_SHORT).show();
                } else {
                    refreshTask = new RefreshTask();
                    refreshTask.execute(((WulkanowyApp) getActivity().getApplication()).getDaoSession());
                }
            }
        });

        if (new ArrayList<>().equals(subjectWithGradesList)) {
            createExpListView();
            new GradesTask().execute(((WulkanowyApp) getActivity().getApplication()).getDaoSession());
        } else if (subjectWithGradesList.size() > 0) {
            createExpListView();
            view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (refreshTask != null && refreshTask.getStatus() == AsyncTask.Status.RUNNING) {
            refreshTask.cancel(true);
        }
    }

    private void createExpListView() {

        RecyclerView recyclerView = view.findViewById(R.id.subject_grade_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        GradesAdapter gradesAdapter = new GradesAdapter(subjectWithGradesList, getActivity());
        recyclerView.setAdapter(gradesAdapter);
    }

    private void prepareSubjectsWithGradesList(DaoSession daoSession) {
        subjectWithGradesList = new ArrayList<>();

        long userId = getContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                .getLong("userId", 0);

        AccountDao accountDao = daoSession.getAccountDao();
        Account account = accountDao.load(userId);

        for (Subject subject : account.getSubjectList()) {
            List<Grade> gradeList = subject.getGradeList();
            if (gradeList.size() != 0) {
                SubjectWithGrades subjectWithGrades = new SubjectWithGrades(subject.getName(), gradeList);
                subjectWithGradesList.add(subjectWithGrades);
            }
        }
    }

    private class GradesTask extends AsyncTask<DaoSession, Void, Void> {

        @Override
        protected Void doInBackground(DaoSession... params) {
            prepareSubjectsWithGradesList(params[0]);
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            createExpListView();
            view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }
    }

    private class RefreshTask extends AsyncTask<DaoSession, Void, Boolean> {

        @Override
        protected Boolean doInBackground(DaoSession... params) {
            VulcanSynchronization vulcanSynchronization = new VulcanSynchronization(new LoginSession());
            try {
                vulcanSynchronization.loginCurrentUser(getContext(), params[0], new Vulcan());
                vulcanSynchronization.syncGrades();
                prepareSubjectsWithGradesList(params[0]);
                return true;
            } catch (Exception e) {
                Log.e(VulcanJobHelper.DEBUG_TAG, "There was a synchronization problem", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                prepareSubjectsWithGradesList(((WulkanowyApp) getActivity().getApplication()).getDaoSession());
                createExpListView();
                swipeRefreshLayout.setRefreshing(false);


                int volumeGrades = DatabaseAccess.getNewGrades(((WulkanowyApp) getActivity().getApplication()).getDaoSession()).size();

                if (volumeGrades == 0) {
                    Snackbar.make(getActivity().findViewById(R.id.fragment_container),
                            R.string.snackbar_no_grades,
                            Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(getActivity().findViewById(R.id.fragment_container),
                            getString(R.string.snackbar_new_grade, volumeGrades),
                            Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), R.string.refresh_error_text, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}
