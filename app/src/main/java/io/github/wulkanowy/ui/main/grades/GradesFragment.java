package io.github.wulkanowy.ui.main.grades;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.R;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.VulcanOfflineException;
import io.github.wulkanowy.dao.DatabaseAccess;
import io.github.wulkanowy.dao.entities.Account;
import io.github.wulkanowy.dao.entities.AccountDao;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.dao.entities.Grade;
import io.github.wulkanowy.dao.entities.Subject;
import io.github.wulkanowy.services.LoginSession;
import io.github.wulkanowy.services.VulcanSynchronization;
import io.github.wulkanowy.services.jobs.VulcanJobHelper;
import io.github.wulkanowy.ui.WulkanowyApp;
import io.github.wulkanowy.utilities.ConnectionUtilities;

public class GradesFragment extends Fragment {

    private static List<SubjectWithGrades> subjectWithGradesList = new ArrayList<>();

    private static long userId;

    public GradesFragment() {
        //empty constructor for fragments
    }

    private static void createExpList(View mainView, Activity activity) {

        RecyclerView recyclerView = mainView.findViewById(R.id.subject_grade_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        GradesAdapter gradesAdapter = new GradesAdapter(subjectWithGradesList, activity);
        recyclerView.setAdapter(gradesAdapter);
    }

    private static void downloadGradesFormDatabase(DaoSession daoSession) {

        subjectWithGradesList = new ArrayList<>();

        AccountDao accountDao = daoSession.getAccountDao();
        Account account = accountDao.load(userId);

        for (Subject subject : account.getSubjectList()) {
            List<Grade> gradeList = subject.getGradeList();
            if (!gradeList.isEmpty()) {
                SubjectWithGrades subjectWithGrades = new SubjectWithGrades(subject.getName(), gradeList);
                subjectWithGradesList.add(subjectWithGrades);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grades, container, false);
        view.findViewById(R.id.fragment_no_grades).setVisibility(View.GONE);

        if (getActivity() != null) {
            DaoSession daoSession = ((WulkanowyApp) getActivity().getApplication()).getDaoSession();
            userId = getActivity().getSharedPreferences("LoginData", Context.MODE_PRIVATE)
                    .getLong("userId", 0);

            prepareRefreshLayout(view, daoSession);

            if (subjectWithGradesList.equals(new ArrayList<>())) {
                createExpList(view, getActivity());
                new GenerateListTask(getActivity(), view, daoSession).execute();
            } else {
                createExpList(view, getActivity());
                view.findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
            }
        }
        return view;
    }

    private void prepareRefreshLayout(final View mainView, final DaoSession daoSession) {

        final SwipeRefreshLayout swipeRefreshLayout = mainView.findViewById(R.id.grade_swipe_refresh);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.black,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (ConnectionUtilities.isOnline(getContext())) {
                    new RefreshTask(getActivity(), mainView, daoSession).execute();
                } else {
                    Toast.makeText(mainView.getContext(), R.string.noInternet_text, Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private static class GenerateListTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<View> mainView;

        private WeakReference<Activity> activity;

        private DaoSession daoSession;

        public GenerateListTask(Activity activity, View mainView, DaoSession daoSession) {
            this.activity = new WeakReference<>(activity);
            this.mainView = new WeakReference<>(mainView);
            this.daoSession = daoSession;
        }

        @Override
        protected Void doInBackground(Void... params) {
            downloadGradesFormDatabase(daoSession);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            createExpList(mainView.get(), activity.get());
            mainView.get().findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
            if (subjectWithGradesList.isEmpty()) {
                mainView.get().findViewById(R.id.fragment_no_grades).setVisibility(View.VISIBLE);
            }
        }
    }

    private static class RefreshTask extends AsyncTask<Void, Void, Integer> {

        private DaoSession daoSession;

        private WeakReference<Activity> activity;

        private WeakReference<View> mainView;

        public RefreshTask(Activity activity, View mainView, DaoSession daoSession) {
            this.activity = new WeakReference<>(activity);
            this.daoSession = daoSession;
            this.mainView = new WeakReference<>(mainView);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            VulcanSynchronization vulcanSynchronization = new VulcanSynchronization(new LoginSession());
            try {
                vulcanSynchronization.loginCurrentUser(activity.get(), daoSession, new Vulcan());
                vulcanSynchronization.syncGrades();
                downloadGradesFormDatabase(daoSession);
                return 1;
            } catch (VulcanOfflineException e) {
                Log.e(VulcanJobHelper.DEBUG_TAG, "There was a synchronization problem, because vulcan is offline", e);
                return R.string.error_host_offline;
            } catch (Exception e) {
                Log.e(VulcanJobHelper.DEBUG_TAG, "There was a synchronization problem", e);
                return R.string.refresh_error_text;
            }
        }

        @Override
        protected void onPostExecute(Integer messageID) {
            super.onPostExecute(messageID);

            if (1 == messageID) {
                if (mainView.get() != null && activity.get() != null) {
                    createExpList(mainView.get(), activity.get());
                }

                int volumeGrades = new DatabaseAccess().getNewGrades(daoSession).size();

                if (volumeGrades == 0) {
                    Snackbar.make(activity.get().findViewById(R.id.fragment_container),
                            R.string.snackbar_no_grades,
                            Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(activity.get().findViewById(R.id.fragment_container),
                            activity.get().getString(R.string.snackbar_new_grade, volumeGrades),
                            Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity.get(), messageID, Toast.LENGTH_SHORT).show();
            }

            if (mainView.get() != null) {
                SwipeRefreshLayout swipeRefreshLayout = mainView.get().findViewById(R.id.grade_swipe_refresh);
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}
