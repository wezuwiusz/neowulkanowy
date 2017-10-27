package io.github.wulkanowy.activity.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.WulkanowyApp;
import io.github.wulkanowy.activity.dashboard.DashboardActivity;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.security.CryptoException;
import io.github.wulkanowy.services.LoginSession;
import io.github.wulkanowy.services.VulcanSynchronization;
import io.github.wulkanowy.services.jobs.GradeJob;
import io.github.wulkanowy.utilities.ConnectionUtilities;

public class LoginTask extends AsyncTask<String, Integer, Integer> {

    private Activity activity;

    private ProgressDialog progress;

    public LoginTask(Activity activity) {
        this.activity = activity;
        this.progress = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progress.setTitle(activity.getText(R.string.login_text));
        progress.setMessage(activity.getText(R.string.please_wait_text));
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    protected Integer doInBackground(String... credentials) {

        if (ConnectionUtilities.isOnline(activity)) {
            VulcanSynchronization vulcanSynchronization = new VulcanSynchronization(new LoginSession());
            DaoSession daoSession = ((WulkanowyApp) activity.getApplication()).getDaoSession();
            try {
                vulcanSynchronization
                        .loginNewUser(credentials[0], credentials[1], credentials[2], activity, daoSession, new Vulcan());
            } catch (BadCredentialsException e) {
                return R.string.login_bad_credentials_text;
            } catch (AccountPermissionException e) {
                return R.string.login_bad_account_permission_text;
            } catch (CryptoException e) {
                return R.string.encrypt_failed_text;
            } catch (NotLoggedInErrorException | IOException e) {
                return R.string.login_denied_text;
            }

            vulcanSynchronization.syncSubjectsAndGrades();

            return R.string.login_accepted_text;

        } else {
            return R.string.noInternet_text;
        }
    }

    protected void onPostExecute(Integer messageID) {
        super.onPostExecute(messageID);

        GradeJob gradesSync = new GradeJob();
        gradesSync.scheduledJob(activity);

        progress.dismiss();

        Toast.makeText(activity, activity.getString(messageID), Toast.LENGTH_LONG).show();

        if (messageID == R.string.login_accepted_text || messageID == R.string.root_failed_text
                || messageID == R.string.encrypt_failed_text) {
            Intent intent = new Intent(activity, DashboardActivity.class);
            activity.startActivity(intent);
        }
    }
}
