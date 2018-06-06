package io.github.wulkanowy.utils.async;

import android.os.AsyncTask;

import timber.log.Timber;

public class AbstractTask extends AsyncTask<Void, Integer, Boolean> {

    private Exception exception;

    private AsyncListeners.OnRefreshListener onRefreshListener;

    private AsyncListeners.OnFirstLoadingListener onFirstLoadingListener;

    public void setOnFirstLoadingListener(AsyncListeners.OnFirstLoadingListener onFirstLoadingListener) {
        this.onFirstLoadingListener = onFirstLoadingListener;
    }

    public void setOnRefreshListener(AsyncListeners.OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            if (onFirstLoadingListener != null) {
                onFirstLoadingListener.onDoInBackgroundLoading();
            } else if (onRefreshListener != null) {
                onRefreshListener.onDoInBackgroundRefresh();
            } else {
                Timber.e("AbstractTask does not have a listener assigned");
            }
            return true;
        } catch (Exception e) {
            exception = e;
            return false;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (onFirstLoadingListener != null) {
            onFirstLoadingListener.onCanceledLoadingAsync();
        } else if (onRefreshListener != null) {
            onRefreshListener.onCanceledRefreshAsync();
        } else {
            Timber.e("AbstractTask does not have a listener assigned");
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (onFirstLoadingListener != null) {
            onFirstLoadingListener.onEndLoadingAsync(result, exception);
        } else if (onRefreshListener != null) {
            onRefreshListener.onEndRefreshAsync(result, exception);
        } else {
            Timber.e("AbstractTask does not have a listener assigned");
        }
    }
}
