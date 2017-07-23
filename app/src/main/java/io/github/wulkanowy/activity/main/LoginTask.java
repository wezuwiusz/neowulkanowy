package io.github.wulkanowy.activity.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.SQLException;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.dashboard.DashboardActivity;
import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.Login;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.api.user.BasicInformation;
import io.github.wulkanowy.api.user.PersonalData;
import io.github.wulkanowy.database.accounts.AccountData;
import io.github.wulkanowy.database.accounts.DatabaseAccount;

public class LoginTask extends AsyncTask<String, Integer, Integer> {

    private Activity activity;

    private ProgressDialog progress;

    private boolean save;

    public LoginTask(Activity context, boolean save) {
        activity = context;
        progress = new ProgressDialog(activity);
        this.save = save;
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

        try {
            String cookiesPath = activity.getFilesDir().getPath() + "/cookies.txt";
            FileOutputStream out = new FileOutputStream(cookiesPath);
            ObjectOutputStream outputStream = new ObjectOutputStream(out);
            outputStream.writeObject(login.getJar());
            outputStream.flush();
        } catch (IOException e) {
            return R.string.login_cookies_save_failed;
        }

        if (save) {
            try {
                BasicInformation userInfo = new BasicInformation(login.getCookies(), credentials[2]);
                PersonalData personalData = userInfo.getPersonalData();
                String firstAndLastName = personalData.getFirstAndLastName();

                AccountData accountData = new AccountData()
                        .setName(firstAndLastName)
                        .setEmail(credentials[0])
                        .setPassword(credentials[1])
                        .setCounty(credentials[2]);

                DatabaseAccount databaseAccount = new DatabaseAccount(activity);

                databaseAccount.open();
                databaseAccount.put(accountData);
                databaseAccount.close();
            } catch (SQLException e) {
                return R.string.SQLite_ioError_text;
            } catch (IOException | LoginErrorException e) {
                return R.string.login_denied;
            }
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
