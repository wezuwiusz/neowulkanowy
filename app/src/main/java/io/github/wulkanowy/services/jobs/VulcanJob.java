package io.github.wulkanowy.services.jobs;

import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.io.IOException;

import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.security.CryptoException;

public abstract class VulcanJob extends JobService {

    private SyncTask syncTask = new SyncTask();

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(VulcanSync.DEBUG_TAG, "Wulkanowy services start");
        syncTask.execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e(VulcanSync.DEBUG_TAG, "Wulkanowy serives stop");
        syncTask.cancel(true);
        return true;
    }

    public abstract void workToBePerformed() throws CryptoException, BadCredentialsException,
            LoginErrorException, AccountPermissionException, IOException;

    private class SyncTask extends AsyncTask<JobParameters, Void, Void> {

        @Override
        protected Void doInBackground(JobParameters... params) {
            try {
                workToBePerformed();
            } catch (Exception e) {
                Log.e(VulcanSync.DEBUG_TAG, "User logging in the background failed", e);
            } finally {
                jobFinished(params[0], false);
            }
            return null;
        }
    }
}
