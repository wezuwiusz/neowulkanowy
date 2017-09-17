package io.github.wulkanowy.activity.started;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.dashboard.DashboardActivity;
import io.github.wulkanowy.activity.main.MainActivity;
import io.github.wulkanowy.services.jobs.GradesSync;
import io.github.wulkanowy.utilities.ConnectionUtilities;

public class LoadingTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;

    LoadingTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ConnectionUtilities.isOnline(context);
    }

    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        if (!result) {
            Toast.makeText(context, R.string.noInternet_text, Toast.LENGTH_LONG).show();
        }

        if (context.getSharedPreferences("LoginData", Context.MODE_PRIVATE).getLong("userId", 0) == 0) {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        } else {
            GradesSync gradesSync = new GradesSync();
            gradesSync.scheduledJob(context);

            Intent intent = new Intent(context, DashboardActivity.class);
            context.startActivity(intent);
        }


    }
}
