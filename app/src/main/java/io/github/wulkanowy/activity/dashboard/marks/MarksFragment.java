package io.github.wulkanowy.activity.dashboard.marks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.wulkanowy.R;
import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.grades.Grade;
import io.github.wulkanowy.api.grades.GradesList;
import io.github.wulkanowy.api.grades.Subject;
import io.github.wulkanowy.api.grades.SubjectsList;

public class MarksFragment extends Fragment {

    private ArrayList<String> subject = new ArrayList<>();

    private View view;

    public MarksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_marks, container, false);

        if (subject.size() == 0) {
            new MarksTask(container.getContext()).execute();
        } else if (subject.size() > 1) {
            createGrid();
            view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }

        return view;
    }

    public void createGrid() {

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(view.getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        ImageAdapter adapter = new ImageAdapter(view.getContext(), subject);
        recyclerView.setAdapter(adapter);
    }

    public class MarksTask extends AsyncTask<Void, Void, Void> {

        private Context mContext;
        private Map<String, String> loginCookies;

        MarksTask(Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String cookiesPath = mContext.getFilesDir().getPath() + "/cookies.txt";

            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cookiesPath));
                loginCookies = (Map<String, String>) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Cookies cookies = new Cookies();
                cookies.setItems(loginCookies);
                SubjectsList subjectsList = new SubjectsList(cookies, "powiatjaroslawski");
                List<Subject> subjects = subjectsList.getAll();
                for (Subject item : subjects) {
                    subject.add(item.getName());
                }

                GradesList gradesList = new GradesList(cookies, "powiatjaroslawski");
                List<Grade> grades = gradesList.getAll();
                for (Grade item : grades) {
                    System.out.println(item.getSubject() + ": " + item.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            createGrid();

            view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            super.onPostExecute(result);
        }
    }
}
