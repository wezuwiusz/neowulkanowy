package io.github.wulkanowy.services.jobs;

import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.SimpleJobService;
import com.firebase.jobdispatcher.Trigger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.github.wulkanowy.R;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.data.db.dao.entities.Grade;
import io.github.wulkanowy.data.sync.NotRegisteredUserException;
import io.github.wulkanowy.services.notifies.GradeNotify;
import io.github.wulkanowy.ui.main.MainActivity;
import io.github.wulkanowy.utils.FabricUtils;
import timber.log.Timber;

public class SyncJob extends SimpleJobService {

    public static final String JOB_TAG = "SyncJob";

    private List<Grade> gradeList = new ArrayList<>();

    @Inject
    RepositoryContract repository;

    public static void start(Context context, int interval, boolean useOnlyWifi) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        dispatcher.mustSchedule(dispatcher.newJobBuilder()
                .setLifetime(Lifetime.FOREVER)
                .setService(SyncJob.class)
                .setTag(JOB_TAG)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(interval * 60, (interval + 10) * 60))
                .setConstraints(useOnlyWifi ? Constraint.ON_UNMETERED_NETWORK : Constraint.ON_ANY_NETWORK)
                .setReplaceCurrent(false)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build());
    }

    public static void stop(Context context) {
        new FirebaseJobDispatcher(new GooglePlayDriver(context)).cancel(JOB_TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
    }

    @Override
    public int onRunJob(JobParameters job) {
        try {
            repository.getSyncRepo().initLastUser();
            repository.getSyncRepo().syncAll();

            gradeList = repository.getDbRepo().getNewGrades(repository.getDbRepo().getCurrentSemesterName());

            if (!gradeList.isEmpty() && repository.getSharedRepo().isNotifyEnable()) {
                showNotification();
            }

            FabricUtils.logLogin("Background", true);

            return JobService.RESULT_SUCCESS;
        } catch (NotRegisteredUserException e) {
            logError(e);
            stop(getApplicationContext());

            return JobService.RESULT_FAIL_NORETRY;
        } catch (BadCredentialsException e) {
            logError(e);
            repository.cleanAllData();
            stop(getApplicationContext());

            return JobService.RESULT_FAIL_NORETRY;
        } catch (Exception e) {
            logError(e);

            return JobService.RESULT_FAIL_RETRY;
        }
    }

    private void showNotification() {
        GradeNotify gradeNotify = new GradeNotify(getApplicationContext());

        gradeNotify.notify(gradeNotify.notificationBuilder()
                .setContentTitle(getStringTitle())
                .setContentText(getStringContent())
                .setSmallIcon(R.drawable.ic_notify_grade)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0,
                        MainActivity.getStartIntent(getApplicationContext())
                                .putExtra(MainActivity.EXTRA_CARD_ID_KEY, 0)
                        , PendingIntent.FLAG_UPDATE_CURRENT
                ))
                .build());
    }

    private String getStringTitle() {
        if (gradeList.size() == 1) {
            return getResources().getQuantityString(R.plurals.newGradePlurals, 1);
        } else {
            return getResources().getQuantityString(R.plurals.newGradePlurals, 2);
        }
    }

    private String getStringContent() {
        if (gradeList.size() == 1) {
            return gradeList.get(0).getSubject();
        } else {
            return getResources().getQuantityString(R.plurals.receivedNewGradePlurals,
                    gradeList.size(), gradeList.size());
        }
    }

    private void logError(Exception e) {
        FabricUtils.logLogin("Background", false);
        Timber.e(e, "During background synchronization an error occurred");
    }
}
