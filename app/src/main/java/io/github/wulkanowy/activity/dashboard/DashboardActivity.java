package io.github.wulkanowy.activity.dashboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.dashboard.attendance.AttendanceFragment;
import io.github.wulkanowy.activity.dashboard.board.BoardFragment;
import io.github.wulkanowy.activity.dashboard.lessonplan.LessonPlanFragment;
import io.github.wulkanowy.activity.dashboard.marks.MarksFragment;

public class DashboardActivity extends AppCompatActivity {

    private MarksFragment marksFragment = new MarksFragment();
    private AttendanceFragment attendanceFragment = new AttendanceFragment();
    private BoardFragment boardFragment = new BoardFragment();
    private LessonPlanFragment lessonPlanFragment = new LessonPlanFragment();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_marks:
                    setTitle(R.string.title_marks);
                    transaction.replace(R.id.fragment_container, marksFragment);
                    transaction.commit();
                    return true;

                case R.id.navigation_attendance:
                    setTitle(R.string.title_attendance);
                    transaction.replace(R.id.fragment_container, attendanceFragment);
                    transaction.commit();
                    return true;

                case R.id.navigation_lessonplan:
                    setTitle(R.string.title_lessonplan);
                    transaction.replace(R.id.fragment_container, lessonPlanFragment);
                    transaction.commit();
                    return true;

                case R.id.navigation_dashboard:
                default:
                    setTitle(R.string.title_dashboard);
                    transaction.replace(R.id.fragment_container, boardFragment);
                    transaction.commit();
                    return true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        setTitle(R.string.title_dashboard);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_dashboard);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, boardFragment).commit();
    }

    public void onBackPressed() {

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        if (navigation.getSelectedItemId() != R.id.navigation_dashboard) {
            navigation.setSelectedItemId(R.id.navigation_dashboard);
        } else if (navigation.getSelectedItemId() == R.id.navigation_dashboard) {
            moveTaskToBack(true);
        }
    }
}
