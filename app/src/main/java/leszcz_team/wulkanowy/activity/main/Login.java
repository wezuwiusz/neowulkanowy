package leszcz_team.wulkanowy.activity.main;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;

public class Login extends AsyncTask<Void, Void, Void> {

    String email;
    String password;
    String county;
    Activity activity;
    String test;

    public Login(String emailT, String passwordT, String countyT, Activity mainAC){

        email = emailT;
        password = passwordT;

        county = "powiat" + countyT.substring(7);
        activity = mainAC;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String htmlDefault = "https://cufs.vulcan.net.pl/Default/Account/LogOn";

        try {
            Connection.Response initial = Jsoup
                    .connect(htmlDefault)
                    .data("LoginName", email)
                    .data("Password", password)
                    .method(Connection.Method.POST)
                    .execute();

            Map<String, String> loginCookies = initial.cookies();

        }
        catch (IOException e){
            test = e.toString();
        }

        return null;
    }

    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        Toast.makeText(activity, test, Toast.LENGTH_LONG).show();
    }
}
