package leszcz_team.wulkanowy.activity.started;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import leszcz_team.wulkanowy.activity.main.MainActivity;

public class LoadingTask extends AsyncTask<Void, Void, Void> {

    Context activity;

    LoadingTask(Context main) {
        activity = main;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void result) {

        Intent intent = new Intent(activity,MainActivity.class);
        activity.startActivity(intent);

    }
}

