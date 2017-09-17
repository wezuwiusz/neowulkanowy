package io.github.wulkanowy.activity.dashboard.grades;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.WulkanowyApp;
import io.github.wulkanowy.dao.entities.Account;
import io.github.wulkanowy.dao.entities.AccountDao;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.dao.entities.Grade;
import io.github.wulkanowy.dao.entities.Subject;

public class GradesFragment extends Fragment {

    private List<SubjectWithGrades> subjectWithGradesList = new ArrayList<>();

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_grades, container, false);

        DaoSession daoSession = ((WulkanowyApp) getActivity().getApplication()).getDaoSession();

        if (subjectWithGradesList.equals(new ArrayList<>())) {
            new GradesTask(daoSession).execute();
        } else if (subjectWithGradesList.size() > 0) {
            createExpListView();
            view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }

        return view;
    }

    public void createExpListView() {

        RecyclerView recyclerView = view.findViewById(R.id.subject_grade_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        GradesAdapter gradesAdapter = new GradesAdapter(subjectWithGradesList, view.getContext());
        recyclerView.setAdapter(gradesAdapter);

    }

    private class GradesTask extends AsyncTask<Void, Void, Void> {

        private DaoSession daoSession;

        GradesTask(DaoSession daoSession) {
            this.daoSession = daoSession;
        }

        @Override
        protected Void doInBackground(Void... params) {

            long userId = getActivity().getSharedPreferences("LoginData", Context.MODE_PRIVATE)
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
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            createExpListView();

            view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }
    }
}
