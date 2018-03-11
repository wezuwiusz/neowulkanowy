package io.github.wulkanowy.ui.login;

import android.os.AsyncTask;

public class LoginTask extends AsyncTask<Void, Integer, Boolean> {

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
    protected Boolean doInBackground(Void... params) {
        try {
            publishProgress(1);
            presenter.onDoInBackground(1);

            publishProgress(2);
            presenter.onDoInBackground(2);
        } catch (Exception e) {
            exception = e;
            return false;
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        presenter.onLoginProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(Boolean success) {
        presenter.onEndAsync(success, exception);
    }

    @Override
    protected void onCancelled() {
        presenter.onCanceledAsync();
    }
}
