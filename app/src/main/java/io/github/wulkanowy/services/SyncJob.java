package io.github.wulkanowy.services;

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

import io.github.wulkanowy.R;
import io.github.wulkanowy.WulkanowyApp;
import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.data.db.dao.entities.Grade;
import io.github.wulkanowy.ui.main.MainActivity;
import io.github.wulkanowy.utils.LogUtils;

public class SyncJob extends SimpleJobService {

    public static final String EXTRA_INTENT_KEY = "cardId";

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
        ((WulkanowyApp) getApplication()).getApplicationComponent().inject(this);
    }

    @Override
    public int onRunJob(JobParameters job) {
        try {
            repository.initLastUser();
            repository.syncAll();

            gradeList = repository.getNewGrades();

            if (!gradeList.isEmpty() && repository.isNotifyEnable()) {
                showNotification();
            }
            return JobService.RESULT_SUCCESS;
        } catch (Exception e) {
            LogUtils.error("During background synchronization an error occurred", e);
            return JobService.RESULT_FAIL_RETRY;
        }
    }

    private void showNotification() {
        NotificationService service = new NotificationService(getApplicationContext());

        service.notify(service.notificationBuilder()
                .setContentTitle(getStringTitle())
                .setContentText(getStringContent())
                .setSmallIcon(R.drawable.ic_stat_notify)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0,
                        MainActivity.getStartIntent(getApplicationContext()).putExtra(EXTRA_INTENT_KEY, 0)
                        , 0
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
}
