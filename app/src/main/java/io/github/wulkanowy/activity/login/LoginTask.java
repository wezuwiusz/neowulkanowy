package io.github.wulkanowy.activity.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.WulkanowyApp;
import io.github.wulkanowy.activity.dashboard.DashboardActivity;
import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.Login;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.security.CryptoException;
import io.github.wulkanowy.services.LoginSession;
import io.github.wulkanowy.services.VulcanSynchronization;
import io.github.wulkanowy.services.synchronisation.AccountRegistration;
import io.github.wulkanowy.utilities.ConnectionUtilities;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class LoginTask extends AsyncTask<Void, String, Integer> {

    private final String email;

    private final String password;

    private final String symbol;

    private Activity activity;

    private View progressView;

    private View loginFormView;

    private TextView showText;

    public LoginTask(Activity activity, String email, String password, String symbol) {
        this.activity = activity;
        this.email = email;
        this.password = password;
        this.symbol = symbol;
    }

    @Override
    protected void onPreExecute() {
        showText = activity.findViewById(R.id.login_progress_text);
    }

    @Override
    protected Integer doInBackground(Void... params) {
        if (ConnectionUtilities.isOnline(activity)) {
            AccountRegistration accountRegistration = new AccountRegistration(
                    new Login(new Cookies()),
                    new Vulcan(),
                    email, password, symbol);

            DaoSession daoSession = ((WulkanowyApp) activity.getApplication()).getDaoSession();

            try {
                publishProgress("1", activity.getResources().getString(R.string.step_connecting));
                String certificate = accountRegistration.connect();

                publishProgress("2", activity.getResources().getString(R.string.step_login));
                LoginSession loginSession = accountRegistration.login(activity, daoSession, certificate);

                publishProgress("3", activity.getResources().getString(R.string.step_synchronization));
                VulcanSynchronization vulcanSynchronization = new VulcanSynchronization(loginSession);
                vulcanSynchronization.syncSubjectsAndGrades();

            } catch (BadCredentialsException e) {
                return R.string.login_bad_credentials_text;
            } catch (AccountPermissionException e) {
                return R.string.error_bad_account_permission;
            } catch (CryptoException e) {
                return R.string.encrypt_failed_text;
            } catch (NotLoggedInErrorException | IOException e) {
                return R.string.login_denied_text;
            }

            accountRegistration.scheduleSynchronization(activity);

            return R.string.login_accepted_text;

        } else {
            return R.string.noInternet_text;
        }
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        showText.setText(progress[0] + "/3 - " + progress[1] + "...");
    }

    @Override
    protected void onPostExecute(final Integer messageID) {
        showProgress(false);

        switch (messageID) {
            // if success
            case R.string.login_accepted_text:
                Intent intent = new Intent(activity, DashboardActivity.class);
                activity.finish();
                activity.startActivity(intent);
                break;

            // if bad credentials entered
            case R.string.login_bad_credentials_text:
                EditText passwordView = activity.findViewById(R.id.password);
                passwordView.setError(activity.getString(R.string.error_incorrect_password));
                passwordView.requestFocus();
                break;

            // if no permission
            case R.string.error_bad_account_permission:
                // Change to visible symbol input view
                TextInputLayout symbolLayout = activity.findViewById(R.id.to_symbol_input_layout);
                symbolLayout.setVisibility(View.VISIBLE);

                EditText symbolView = activity.findViewById(R.id.symbol);
                symbolView.setError(activity.getString(R.string.error_bad_account_permission));
                symbolView.requestFocus();
                break;

            default:
                Snackbar
                        .make(activity.findViewById(R.id.coordinatorLayout), messageID, Snackbar.LENGTH_LONG)
                        .show();
                break;
        }
    }

    @Override
    protected void onCancelled() {
        showProgress(false);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show) {
        loginFormView = activity.findViewById(R.id.login_form);
        progressView = activity.findViewById(R.id.login_progress);

        int animTime = activity.getResources().getInteger(android.R.integer.config_shortAnimTime);

        changeLoginFormVisibility(show, animTime);
        changeProgressVisibility(show, animTime);
    }

    private void changeLoginFormVisibility(final boolean show, final int animTime) {
        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        loginFormView.animate().setDuration(animTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
    }

    private void changeProgressVisibility(final boolean show, final int animTime) {
        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(animTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
