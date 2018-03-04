package io.github.wulkanowy.ui.login;

import android.os.AsyncTask;

public class LoginTask extends AsyncTask<Void, Integer, Boolean> {

    private String email;

    private String password;

    private String symbol;

    private LoginContract.Presenter presenter;

    private Exception exception;

    LoginTask(LoginContract.Presenter presenter, String email, String password, String symbol) {
        this.presenter = presenter;
        this.email = email;
        this.password = password;
        this.symbol = symbol;
    }

    @Override
    protected void onPreExecute() {
        presenter.onStartAsync();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            publishProgress(1);
            presenter.getRepository().loginUser(email, password, symbol);

            publishProgress(2);
            presenter.getRepository().syncAll();
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
