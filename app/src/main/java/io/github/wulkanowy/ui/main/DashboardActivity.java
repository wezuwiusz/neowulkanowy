package io.github.wulkanowy.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import io.github.wulkanowy.R;
import io.github.wulkanowy.services.Updater;
import io.github.wulkanowy.ui.main.attendance.AttendanceFragment;
import io.github.wulkanowy.ui.main.board.BoardFragment;
import io.github.wulkanowy.ui.main.grades.GradesFragment;
import io.github.wulkanowy.ui.main.timetable.TimetableFragment;

public class DashboardActivity extends AppCompatActivity {

    private Fragment currentFragment;

    private GradesFragment gradesFragment = new GradesFragment();

    private AttendanceFragment attendanceFragment = new AttendanceFragment();

    private BoardFragment boardFragment = new BoardFragment();

    private TimetableFragment timetableFragment = new TimetableFragment();

    private Updater updater;

    private boolean showed;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_marks:
                    setTitle(R.string.grades_text);
                    currentFragment = gradesFragment;
                    break;

                case R.id.navigation_attendance:
                    setTitle(R.string.attendance_text);
                    currentFragment = attendanceFragment;
                    break;

                case R.id.navigation_lessonplan:
                    setTitle(R.string.lessonplan_text);
                    currentFragment = timetableFragment;
                    break;

                case R.id.navigation_dashboard:
                default:
                    setTitle(R.string.dashboard_text);
                    currentFragment = boardFragment;
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, currentFragment);
            transaction.commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_marks);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState != null) {
            currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");
            setTitle(savedInstanceState.getString("activityTitle"));
        } else {
            currentFragment = gradesFragment;
            setTitle(R.string.grades_text);
        }

        int cardID = getIntent().getIntExtra("cardID", 0);

        if (cardID == 1) {
            currentFragment = gradesFragment;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, currentFragment).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!showed) {
            updater = new Updater(this).checkForUpdates();
            showed = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        updater.onRequestPermissionsResult(requestCode, grantResults);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("activityTitle", getTitle().toString());
        getSupportFragmentManager().putFragment(outState, "currentFragment", currentFragment);
    }

    public void onBackPressed() {

        BottomNavigationView navigation = findViewById(R.id.navigation);

        if (navigation.getSelectedItemId() != R.id.navigation_dashboard) {
            navigation.setSelectedItemId(R.id.navigation_dashboard);
        } else if (navigation.getSelectedItemId() == R.id.navigation_dashboard) {
            moveTaskToBack(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        updater.onDestroy(this);
    }
}
