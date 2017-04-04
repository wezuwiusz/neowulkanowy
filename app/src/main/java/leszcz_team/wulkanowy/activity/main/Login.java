package leszcz_team.wulkanowy.activity.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import leszcz_team.wulkanowy.R;

public class Login extends AsyncTask<Void, Void, Void> {

    String email;
    String password;
    String county;
    Activity activity;

    public Login(String emailT, String passwordT, String countyT, Activity mainAC){

        email = emailT;
        password = passwordT;
        county = "powiat" + countyT;
        activity = mainAC;
    }

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }

    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        new AlertDialog.Builder(activity)
                .setTitle(R.string.warning_label)
                .setMessage(R.string.error_feature_text)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
