package io.github.wulkanowy.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.github.wulkanowy.R;
import io.github.wulkanowy.WulkanowyApp;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.api.login.VulcanOfflineException;
import io.github.wulkanowy.db.dao.entities.DaoSession;
import io.github.wulkanowy.services.jobs.FullSyncJob;
import io.github.wulkanowy.services.sync.LoginSession;
import io.github.wulkanowy.services.sync.VulcanSync;
import io.github.wulkanowy.ui.main.DashboardActivity;
import io.github.wulkanowy.utils.KeyboardUtils;
import io.github.wulkanowy.utils.NetworkUtils;
import io.github.wulkanowy.utils.security.CryptoException;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class LoginTask extends AsyncTask<Void, String, Integer> {

    private final String email;

    private final String password;

    private final String symbol;

    private WeakReference<Activity> activity;

    private WeakReference<View> progressView;

    private WeakReference<View> loginFormView;

    private WeakReference<TextView> showText;

    public LoginTask(Activity activity, String email, String password, String symbol) {
        this.activity = new WeakReference<>(activity);
        this.email = email;
        this.password = password;
        this.symbol = symbol;
    }

    @Override
    protected void onPreExecute() {
        showText = new WeakReference<>((TextView) activity.get().findViewById(R.id.login_progress_text));
    }

    @Override
    protected Integer doInBackground(Void... params) {
        if (NetworkUtils.isOnline(activity.get())) {
            DaoSession daoSession = ((WulkanowyApp) activity.get().getApplication()).getDaoSession();
            VulcanSync vulcanSync = new VulcanSync(new LoginSession());

            try {
                publishProgress("1", activity.get().getResources().getString(R.string.step_login));
                vulcanSync.firstLoginSignInStep(activity.get(), daoSession, email, password, symbol);

                publishProgress("2", activity.get().getResources().getString(R.string.step_synchronization));
                vulcanSync.syncAll();
            } catch (BadCredentialsException e) {
                return R.string.login_bad_credentials_text;
            } catch (AccountPermissionException e) {
                return R.string.error_bad_account_permission;
            } catch (CryptoException e) {
                return R.string.encrypt_failed_text;
            } catch (UnknownHostException e) {
                return R.string.noInternet_text;
            } catch (SocketTimeoutException e) {
                return R.string.generic_timeout_error;
            } catch (NotLoggedInErrorException | IOException e) {
                return R.string.login_denied_text;
            } catch (VulcanOfflineException e) {
                return R.string.error_host_offline;
            } catch (UnsupportedOperationException e) {
                return -1;
            }

            new FullSyncJob().scheduledJob(activity.get());

            return R.string.login_accepted_text;

        } else {
            return R.string.noInternet_text;
        }
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        showText.get().setText(String.format("%1$s/2 - %2$s...", progress[0], progress[1]));
    }

    @Override
    protected void onPostExecute(final Integer messageID) {
        showProgress(false);

        switch (messageID) {
            // if success
            case R.string.login_accepted_text:
                Intent intent = new Intent(activity.get(), DashboardActivity.class);
                activity.get().finish();
                activity.get().startActivity(intent);
                break;

            // if bad credentials entered
            case R.string.login_bad_credentials_text:
                EditText passwordView = activity.get().findViewById(R.id.password);
                passwordView.setError(activity.get().getString(R.string.error_incorrect_password));
                passwordView.requestFocus();
                KeyboardUtils.showSoftInput(passwordView, activity.get());
                break;

            // if no permission
            case R.string.error_bad_account_permission:
                // Change to visible symbol input view
                TextInputLayout symbolLayout = activity.get().findViewById(R.id.to_symbol_input_layout);
                symbolLayout.setVisibility(View.VISIBLE);

                EditText symbolView = activity.get().findViewById(R.id.symbol);
                symbolView.setError(activity.get().getString(R.string.error_bad_account_permission));
                symbolView.requestFocus();
                KeyboardUtils.showSoftInput(symbolView, activity.get());
                break;

            // if rooted and SDK < 18
            case -1:
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity.get())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.alert_dialog_blocked_app)
                        .setMessage(R.string.alert_dialog_blocked_app_message)
                        .setPositiveButton(R.string.generic_dialog_close, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                alertDialog.show();
                break;

            default:
                Snackbar.make(activity.get().findViewById(R.id.fragment_container),
                        messageID, Snackbar.LENGTH_LONG).show();
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
        loginFormView = new WeakReference<>(activity.get().findViewById(R.id.login_form));
        progressView = new WeakReference<>(activity.get().findViewById(R.id.login_progress));

        int animTime = activity.get().getResources().getInteger(android.R.integer.config_shortAnimTime);

        changeLoginFormVisibility(show, animTime);
        changeProgressVisibility(show, animTime);
    }

    private void changeLoginFormVisibility(final boolean show, final int animTime) {
        loginFormView.get().setVisibility(show ? View.GONE : View.VISIBLE);
        loginFormView.get().animate().setDuration(animTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginFormView.get().setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
    }

    private void changeProgressVisibility(final boolean show, final int animTime) {
        progressView.get().setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.get().animate().setDuration(animTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.get().setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
