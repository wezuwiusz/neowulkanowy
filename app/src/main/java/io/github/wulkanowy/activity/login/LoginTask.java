package io.github.wulkanowy.activity.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.WulkanowyApp;
import io.github.wulkanowy.activity.dashboard.DashboardActivity;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.security.CryptoException;
import io.github.wulkanowy.services.LoginSession;
import io.github.wulkanowy.services.VulcanSynchronization;
import io.github.wulkanowy.services.jobs.GradeJob;
import io.github.wulkanowy.utilities.ConnectionUtilities;

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
        if (ConnectionUtilities.isOnline(activity.get())) {
            VulcanSynchronization vulcanSynchronization = new VulcanSynchronization(new LoginSession());

            DaoSession daoSession = ((WulkanowyApp) activity.get().getApplication()).getDaoSession();

            try {
                publishProgress("1", activity.get().getResources().getString(R.string.step_connecting));
                vulcanSynchronization.firstLoginConnectStep(email, password, symbol);

                publishProgress("2", activity.get().getResources().getString(R.string.step_login));
                vulcanSynchronization.firstLoginSignInStep(activity.get(), daoSession);

                publishProgress("3", activity.get().getResources().getString(R.string.step_synchronization));
                vulcanSynchronization.syncSubjectsAndGrades();

            } catch (BadCredentialsException e) {
                return R.string.login_bad_credentials_text;
            } catch (AccountPermissionException e) {
                return R.string.error_bad_account_permission;
            } catch (CryptoException e) {
                return R.string.encrypt_failed_text;
            } catch (NotLoggedInErrorException | IOException e) {
                return R.string.login_denied_text;
            } catch (UnsupportedOperationException e) {
                return -1;
            }

            GradeJob gradeJob = new GradeJob();
            gradeJob.scheduledJob(activity.get());

            return R.string.login_accepted_text;

        } else {
            return R.string.noInternet_text;
        }
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        showText.get().setText(String.format("%1$s/3 - %2$s...", progress[0], progress[1]));
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
                showSoftKeyboard(passwordView);
                break;

            // if no permission
            case R.string.error_bad_account_permission:
                // Change to visible symbol input view
                TextInputLayout symbolLayout = activity.get().findViewById(R.id.to_symbol_input_layout);
                symbolLayout.setVisibility(View.VISIBLE);

                EditText symbolView = activity.get().findViewById(R.id.symbol);
                symbolView.setError(activity.get().getString(R.string.error_bad_account_permission));
                symbolView.requestFocus();
                showSoftKeyboard(symbolView);
                break;

            // if rooted and SDK < 18
            case -1:
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity.get())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.alert_dialog_blocked_app)
                        .setMessage(R.string.alert_dialog_blocked_app_message)
                        .setPositiveButton(R.string.dialog_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();
                break;

            default:
                Snackbar.make(activity.get().findViewById(R.id.coordinatorLayout),
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

    private void showSoftKeyboard(EditText editText) {
        InputMethodManager manager = (InputMethodManager)
                activity.get().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.showSoftInput(editText,
                    InputMethodManager.SHOW_IMPLICIT);
        }
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
