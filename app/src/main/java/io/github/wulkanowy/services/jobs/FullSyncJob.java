package io.github.wulkanowy.services.jobs;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.List;
import java.util.Random;

import io.github.wulkanowy.R;
import io.github.wulkanowy.dao.DatabaseAccess;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.dao.entities.Grade;
import io.github.wulkanowy.services.VulcanSynchronization;
import io.github.wulkanowy.services.notifications.NotificationBuilder;
import io.github.wulkanowy.ui.WulkanowyApp;
import io.github.wulkanowy.ui.main.DashboardActivity;

public class FullSyncJob extends VulcanJobHelper<FullSyncJob> {

    private static final String UNIQUE_TAG = "FullSync";

    private static final int DEFAULT_INTERVAL_START = 60 * 50;

    private static final int DEFAULT_INTERVAL_END = DEFAULT_INTERVAL_START + (60 * 40);

    @Override
    protected Job createJob(FirebaseJobDispatcher dispatcher) {
        return dispatcher.newJobBuilder()
                .setLifetime(Lifetime.FOREVER)
                .setService(SyncService.class)
                .setTag(UNIQUE_TAG)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(DEFAULT_INTERVAL_START, DEFAULT_INTERVAL_END))
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setReplaceCurrent(false)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();
    }

    public static class SyncService extends VulcanService {

        @Override
        public void workToBePerformed() throws Exception {
            DaoSession daoSession = ((WulkanowyApp) getApplication()).getDaoSession();

            VulcanSynchronization synchronization = new VulcanSynchronization()
                    .loginCurrentUser(getApplicationContext(), daoSession);
            synchronization.syncAll();
            List<Grade> newGradeList = new DatabaseAccess().getNewGrades(daoSession);

            if (newGradeList.size() == 1) {
                buildNotify(getResources().getQuantityString(R.plurals.newGradePlurals, 1),
                        newGradeList.get(0).getSubject());
            } else if (newGradeList.size() > 1) {
                buildNotify(getResources().getQuantityString(R.plurals.newGradePlurals, 2),
                        getResources().getQuantityString(R.plurals.receivedNewGradePlurals, newGradeList.size(), newGradeList.size()));
            }
        }

        private void buildNotify(String title, String bodyText) {
            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
            intent.putExtra("cardID", 1);
            PendingIntent pendingIntent = PendingIntent
                    .getActivity(getApplicationContext(), 0, intent, 0);

            NotificationBuilder notificationBuilder = new NotificationBuilder(getApplicationContext());
            NotificationCompat.Builder builder = notificationBuilder
                    .getNotifications(title, bodyText, pendingIntent);
            notificationBuilder.getManager().notify(new Random().nextInt(10000), builder.build());
        }
    }
}