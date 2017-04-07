package leszcz_team.wulkanowy.activity.main;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;

import leszcz_team.wulkanowy.R;

public class Login extends AsyncTask<Void, Void, Void> {

    String email;
    String password;
    String county;

    Map<String, String> loginCookies;

    Activity activity;
    String userMesage;

    String urlForStepOne;
    String urlForStepTwo;
    String urlForStepThree;

    public Login(String emailT, String passwordT, String countyT, Activity mainAC){

        activity = mainAC;

        if (emailT.equals("Debug")){
            urlForStepOne = activity.getString(R.string.urlStepOneDebug);
            urlForStepTwo = activity.getString(R.string.urlStepTwoDebug);
            urlForStepThree = activity.getString(R.string.urlStepThreeDebug);
            county = activity.getString(R.string.countyDebug);
            email = activity.getString(R.string.emailDebug);
            password = passwordT;
        }
        else{
            urlForStepOne = activity.getString(R.string.urlStepOneRelease);
            urlForStepTwo = activity.getString(R.string.urlStepTwoRelease);
            urlForStepThree = activity.getString(R.string.urlStepThreeRelease);
            county = "powiat" + countyT.substring(7);
            email = emailT;
            password = passwordT;
        }
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            if (!stepOne()) {
                return null;
            }

            Document certificate = stepTwo();

            Connection.Response step3 = stepThree(certificate);
            Document dashboardHtml = step3.parse();

            String helloText = dashboardHtml.getElementsByClass("welcome").text();

            if (helloText.equals("Dzień dobry!")) {
                userMesage = activity.getString(R.string.login_accepted);
            }
            else {
                userMesage = activity.getString(R.string.login_denied);
            }
        }
        catch (IOException e){
            userMesage = e.toString();
        }

        return null;
    }

    private boolean stepOne() throws IOException {
        Connection.Response initial = Jsoup
                .connect(urlForStepOne)
                .data("LoginName", email)
                .data("Password", password)
                .method(Connection.Method.POST)
                .execute();

        loginCookies = initial.cookies();

        CheckPass checkPass = new CheckPass(initial);
        userMesage = checkPass.start();

        return userMesage.isEmpty();
    }

    private Document stepTwo() throws IOException {
        county = county.replace("ł", "l");
        urlForStepTwo = urlForStepTwo.replace("{locationID}", county);

        return Jsoup.connect(urlForStepTwo)
                .cookies(loginCookies)
                .get();
    }

    private Connection.Response stepThree(Document certificate) throws IOException {
        Elements wresultsInput = certificate.select("input[name=wresult]");
        String wresults = wresultsInput.attr("value");

        Elements waInput = certificate.select("input[name=wa]");
        String wa = waInput.attr("value");

        urlForStepThree = urlForStepThree.replace("{locationID}", county);

        return Jsoup.connect(urlForStepThree)
                .data("wa", wa)
                .data("wresult", wresults)
                .cookies(loginCookies)
                .followRedirects(true)
                .method(Connection.Method.POST)
                .execute();
    }

    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (!userMesage.isEmpty()){
            Toast.makeText(activity, userMesage , Toast.LENGTH_LONG).show();
        }
    }
}
