package io.github.wulkanowy.ui.main;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import io.github.wulkanowy.R;

public class RefreshTask extends AsyncTask<Void, Void, List<?>> {

    public static final String DEBUG_TAG = "RefreshTask";

    private int stringEventId = 0;

    private AbstractFragment abstractFragment;

    public RefreshTask(AbstractFragment abstractFragment) {
        this.abstractFragment = abstractFragment;
    }

    @Override
    protected List<?> doInBackground(Void... voids) {
        try {
            abstractFragment.onRefresh();
            return abstractFragment.getItems();
        } catch (UnknownHostException e) {
            stringEventId = R.string.noInternet_text;
            Log.i(DEBUG_TAG, "Synchronization is failed because occur problem with internet",
                    new IOException());
            return null;
        } catch (SocketTimeoutException e) {
            stringEventId = R.string.generic_timeout_error;
            Log.i(DEBUG_TAG, "Too long wait for connection with internet", e);
            return null;
        } catch (Exception e) {
            stringEventId = R.string.refresh_error_text;
            Log.e(DEBUG_TAG, "There was a synchronization problem", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPostExecute(List<?> objects) {
        super.onPostExecute(objects);
        abstractFragment.onRefreshProcessFinish(objects, stringEventId);
    }
}
