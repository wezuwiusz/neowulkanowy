package io.github.wulkanowy.ui.main;

import android.os.AsyncTask;

import java.util.List;

public class DatabaseQueryTask extends AsyncTask<Void, Void, List<?>> {

    private AbstractFragment abstractFragment;

    public DatabaseQueryTask(AbstractFragment<?> abstractFragment) {
        this.abstractFragment = abstractFragment;
    }

    @Override
    protected List<?> doInBackground(Void... voids) {
        try {
            return abstractFragment.getItems();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPostExecute(List<?> objects) {
        super.onPostExecute(objects);
        abstractFragment.onQuarryProcessFinish(objects);
    }
}
