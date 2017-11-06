package io.github.wulkanowy.services.jobs;

import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.io.IOException;
import java.lang.ref.WeakReference;

import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.security.CryptoException;

public abstract class VulcanService extends JobService {

    private SyncTask syncTask;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(VulcanJobHelper.DEBUG_TAG, "Wulkanowy services start");
        syncTask = new SyncTask(this, params);
        syncTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e(VulcanJobHelper.DEBUG_TAG, "Wulkanowy services stop");
        if (syncTask != null) {
            syncTask.cancel(true);
        }
        return true;
    }

    public abstract void workToBePerformed() throws CryptoException, BadCredentialsException,
            NotLoggedInErrorException, AccountPermissionException, IOException;

    private static class SyncTask extends AsyncTask<Void, Void, Void> {

        private JobParameters jobParameters;

        private WeakReference<VulcanService> vulcanService;

        public SyncTask(VulcanService vulcanService, JobParameters jobParameters) {
            this.jobParameters = jobParameters;
            this.vulcanService = new WeakReference<>(vulcanService);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                vulcanService.get().workToBePerformed();
            } catch (Exception e) {
                Log.e(VulcanJobHelper.DEBUG_TAG, "User logging in the background failed", e);
            } finally {
                vulcanService.get().jobFinished(jobParameters, false);
            }
            return null;
        }
    }
}
