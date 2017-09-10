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
import io.github.wulkanowy.api.grades.Subject;
import io.github.wulkanowy.database.grades.GradesDatabase;
import io.github.wulkanowy.database.subjects.SubjectsDatabase;

public class GradesFragment extends Fragment {

    private List<SubjectWithGrades> subjectWithGradesList = new ArrayList<>();

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_grades, container, false);

        if (subjectWithGradesList.size() == 0) {
            new MarksTask(container.getContext()).execute();
        } else if (subjectWithGradesList.size() > 1) {
            createExpListView();
            view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }

        return view;
    }

    public void createExpListView() {

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.subject_grade_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        GradesAdapter gradesAdapter = new GradesAdapter(subjectWithGradesList, view.getContext());
        recyclerView.setAdapter(gradesAdapter);

    }

    public class MarksTask extends AsyncTask<Void, Void, Void> {

        private Context context;

        MarksTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {

            SubjectsDatabase subjectsDatabase = new SubjectsDatabase(context);
            GradesDatabase gradesDatabase = new GradesDatabase(context);

            gradesDatabase.open();

            for (Subject subject : subjectsDatabase.getAllSubjectsNames()) {
                List<GradeItem> gradeItems = gradesDatabase.getSubjectGrades(context.getSharedPreferences("LoginData", Context.MODE_PRIVATE).getLong("isLogin", 0),
                        SubjectsDatabase.getSubjectId(subject.getName()));
                if (gradeItems.size() > 0) {
                    subjectWithGradesList.add(new SubjectWithGrades(subject.getName(), gradeItems));
                }
            }

            gradesDatabase.close();

            return null;
        }

        protected void onPostExecute(Void result) {
            createExpListView();

            view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            super.onPostExecute(result);
        }
    }
}
