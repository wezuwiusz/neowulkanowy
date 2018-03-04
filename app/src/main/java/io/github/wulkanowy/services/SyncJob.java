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

import java.util.List;

import javax.inject.Inject;

import io.github.wulkanowy.R;
import io.github.wulkanowy.WulkanowyApp;
import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.data.db.dao.entities.Grade;
import io.github.wulkanowy.ui.main.MainActivity;
import io.github.wulkanowy.utils.LogUtils;

public class SyncJob extends SimpleJobService {

    private static final int DEFAULT_INTERVAL_START = 60 * 50;

    private static final int DEFAULT_INTERVAL_END = DEFAULT_INTERVAL_START + (60 * 40);

    public static final String EXTRA_INTENT_KEY = "cardId";

    private List<Grade> gradeList;

    @Inject
    RepositoryContract repository;

    public static void start(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        dispatcher.mustSchedule(dispatcher.newJobBuilder()
                .setLifetime(Lifetime.FOREVER)
                .setService(SyncJob.class)
                .setTag("SyncJob")
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(DEFAULT_INTERVAL_START, DEFAULT_INTERVAL_END))
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setReplaceCurrent(false)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((WulkanowyApp) getApplication()).getApplicationComponent().inject(this);
    }

    @Override
    public int onRunJob(JobParameters job) {
        try {
            repository.loginCurrentUser();
            repository.syncAll();

            gradeList = repository.getNewGrades();

            if (!gradeList.isEmpty()) {
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
                .setSmallIcon(R.drawable.ic_notification)
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
