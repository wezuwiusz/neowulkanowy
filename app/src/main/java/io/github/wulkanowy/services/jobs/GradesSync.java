package io.github.wulkanowy.services.jobs;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.io.IOException;

import io.github.wulkanowy.activity.WulkanowyApp;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.security.CryptoException;
import io.github.wulkanowy.services.synchronisation.DataSynchronisation;
import io.github.wulkanowy.services.synchronisation.VulcanSynchronisation;

public class GradesSync extends VulcanSync {

    public static final String UNIQUE_TAG = "GradesSync34512";

    public static final int DEFAULT_INTERVAL_START = 60 * 50;

    public static final int DEFAULT_INTERVAL_END = DEFAULT_INTERVAL_START + (60 * 10);

    @Override
    protected Job createJob(FirebaseJobDispatcher dispatcher) {
        return dispatcher.newJobBuilder()
                .setLifetime(Lifetime.FOREVER)
                .setService(GradeJob.class)
                .setTag(UNIQUE_TAG)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(DEFAULT_INTERVAL_START, DEFAULT_INTERVAL_END))
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();
    }

    public static class GradeJob extends VulcanJob {

        @Override
        public void workToBePerformed() throws CryptoException, BadCredentialsException,
                LoginErrorException, AccountPermissionException, IOException {

            DaoSession daoSession = ((WulkanowyApp) getApplication()).getDaoSession();

            VulcanSynchronisation vulcanSynchronisation = new VulcanSynchronisation();
            DataSynchronisation dataSynchronisation = new DataSynchronisation(daoSession);
            vulcanSynchronisation.loginCurrentUser(getApplicationContext(), daoSession);
            dataSynchronisation.syncGrades(vulcanSynchronisation);
        }
    }
}
