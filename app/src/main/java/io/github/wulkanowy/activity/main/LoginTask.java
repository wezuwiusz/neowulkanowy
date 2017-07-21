package io.github.wulkanowy.activity.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.dashboard.DashboardActivity;
import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.login.*;

public class LoginTask extends AsyncTask<String, Integer, Integer> {

    private Activity activity;

    private ProgressDialog progress;

    public LoginTask(Activity context) {
        activity = context;
        progress = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progress.setTitle(activity.getText(R.string.login_title));
        progress.setMessage(activity.getText(R.string.please_wait));
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    protected Integer doInBackground(String... credentials) {
        Cookies cookies = new Cookies();
        Login login = new Login(cookies);

        try {
            login.login(credentials[0], credentials[1], credentials[2]);
        } catch (BadCredentialsException e) {
            return R.string.login_bad_credentials;
        } catch (AccountPermissionException e) {
            return R.string.login_bad_account_permission;
        } catch (LoginErrorException e) {
            return R.string.login_denied;
        }

        //Map<String, String> cookiesList = login.getJar();

        return R.string.login_accepted;
    }

    protected void onPostExecute(Integer messageID) {
        super.onPostExecute(messageID);

        progress.dismiss();

        Toast.makeText(activity, activity.getString(messageID), Toast.LENGTH_LONG).show();

        if (messageID == R.string.login_accepted){
            Intent intent = new Intent(activity, DashboardActivity.class);
            activity.startActivity(intent);
        }
    }
}
