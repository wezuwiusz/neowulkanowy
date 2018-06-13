package io.github.wulkanowy.ui.login;

import android.os.AsyncTask;

public class LoginTask extends AsyncTask<Void, Integer, Integer> {

    public final static int LOGIN_AND_SYNC_SUCCESS = 1;

    public final static int LOGIN_FAILED = -1;

    public final static int SYNC_FAILED = 2;

    private LoginContract.Presenter presenter;

    private Exception exception;

    LoginTask(LoginContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onPreExecute() {
        presenter.onStartAsync();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        try {
            publishProgress(1);
            presenter.onDoInBackground(1);
        } catch (Exception e) {
            exception = e;
            return LOGIN_FAILED;
        }

        try {
            publishProgress(2);
            presenter.onDoInBackground(2);
        } catch (Exception e) {
            exception = e;
            return SYNC_FAILED;
        }
        return LOGIN_AND_SYNC_SUCCESS;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        presenter.onLoginProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(Integer success) {
        presenter.onEndAsync(success, exception);
    }

    @Override
    protected void onCancelled() {
        presenter.onCanceledAsync();
    }
}
