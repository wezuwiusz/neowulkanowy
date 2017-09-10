package io.github.wulkanowy.activity.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.dashboard.DashboardActivity;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.security.CryptoException;
import io.github.wulkanowy.services.jobs.GradesSync;
import io.github.wulkanowy.services.synchronisation.DataSynchronisation;
import io.github.wulkanowy.services.synchronisation.VulcanSynchronisation;
import io.github.wulkanowy.utilities.ConnectionUtilities;

public class LoginTask extends AsyncTask<String, Integer, Integer> {

    private Context context;

    private ProgressDialog progress;

    public LoginTask(Context context) {
        this.context = context;
        this.progress = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progress.setTitle(context.getText(R.string.login_text));
        progress.setMessage(context.getText(R.string.please_wait_text));
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    protected Integer doInBackground(String... credentials) {

        if (ConnectionUtilities.isOnline(context)) {
            VulcanSynchronisation vulcanSynchronisation = new VulcanSynchronisation();
            try {
                vulcanSynchronisation.loginNewUser(credentials[0], credentials[1], credentials[2], context);
            } catch (BadCredentialsException e) {
                return R.string.login_bad_credentials_text;
            } catch (AccountPermissionException e) {
                return R.string.login_bad_account_permission_text;
            } catch (CryptoException e) {
                return R.string.encrypt_failed_text;
            } catch (LoginErrorException | IOException e) {
                return R.string.login_denied_text;
            }

            DataSynchronisation dataSynchronisation = new DataSynchronisation(context);
            dataSynchronisation.syncGradesAndSubjects(vulcanSynchronisation);

            return R.string.login_accepted_text;

        } else {
            return R.string.noInternet_text;
        }
    }

    protected void onPostExecute(Integer messageID) {
        super.onPostExecute(messageID);

        GradesSync gradesSync = new GradesSync();
        gradesSync.scheduledJob(context);

        progress.dismiss();

        Toast.makeText(context, context.getString(messageID), Toast.LENGTH_LONG).show();

        if (messageID == R.string.login_accepted_text || messageID == R.string.root_failed_text
                || messageID == R.string.encrypt_failed_text) {
            Intent intent = new Intent(context, DashboardActivity.class);
            context.startActivity(intent);
        }
    }
}
