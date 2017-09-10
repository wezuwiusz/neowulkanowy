package io.github.wulkanowy.services.jobs;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.io.IOException;

import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.security.CryptoException;
import io.github.wulkanowy.services.synchronisation.DataSynchronisation;
import io.github.wulkanowy.services.synchronisation.VulcanSynchronisation;

public class SubjectsSync extends VulcanSync {

    public static final String UNIQUE_TAG = "SubjectsSync34512";

    public static final int DEFAULT_INTERVAL_START = 0;

    public static final int DEFAULT_INTERVAL_END = DEFAULT_INTERVAL_START + 10;

    @Override
    protected Job createJob(FirebaseJobDispatcher dispatcher) {
        return dispatcher.newJobBuilder()
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setService(SubjectJob.class)
                .setTag(UNIQUE_TAG)
                .setRecurring(false)
                .setTrigger(Trigger.executionWindow(DEFAULT_INTERVAL_START, DEFAULT_INTERVAL_END))
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();
    }

    private class SubjectJob extends VulcanJob {

        @Override
        public void workToBePerformed() throws CryptoException, BadCredentialsException,
                LoginErrorException, AccountPermissionException, IOException {

            VulcanSynchronisation vulcanSynchronisation = new VulcanSynchronisation();
            DataSynchronisation dataSynchronisation = new DataSynchronisation(getApplicationContext());
            vulcanSynchronisation.loginCurrentUser(getApplicationContext());
            dataSynchronisation.syncSubjects(vulcanSynchronisation);

        }
    }
}
