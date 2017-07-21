package io.github.wulkanowy.activity.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

import io.github.wulkanowy.R;
import io.github.wulkanowy.activity.dashboard.DashboardActivity;

public class Login extends AsyncTask<Void, Void, Void> {

    String email;
    String password;
    String county;

    int check;

    Map<String, String> loginCookies;

    Activity activity;
    String userMesage;

    String urlForStepOne;
    String urlForStepTwo;
    String urlForStepThree;

    ProgressDialog progress;

    public Login(String emailT, String passwordT, String countyT, Activity mainAC, int check) {

        activity = mainAC;
        progress = new ProgressDialog(activity);
        this.check = check;


        if (countyT.equals("Debug")) {
            urlForStepOne = activity.getString(R.string.urlStepOneDebug);
            urlForStepTwo = activity.getString(R.string.urlStepTwoDebug);
            urlForStepThree = activity.getString(R.string.urlStepThreeDebug);
            county = activity.getString(R.string.countyDebug);
            email = emailT;
            password = passwordT;
        } else {
            urlForStepOne = activity.getString(R.string.urlStepOneRelease);
            urlForStepTwo = activity.getString(R.string.urlStepTwoRelease);
            urlForStepThree = activity.getString(R.string.urlStepThreeRelease);
            county = countyT;
            email = emailT;
            password = passwordT;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progress.setTitle(activity.getText(R.string.login_title));
        progress.setMessage(activity.getText(R.string.please_wait));
        progress.setCancelable(false);
        progress.show();
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
                String cookiesPath = activity.getFilesDir().getPath() + "/cookies.txt";
                FileOutputStream out = new FileOutputStream(cookiesPath);
                ObjectOutputStream outputStream = new ObjectOutputStream(out);
                outputStream.writeObject(loginCookies);
                outputStream.flush();

                userMesage = activity.getString(R.string.login_accepted);
            } else {
                userMesage = activity.getString(R.string.login_denied);
            }
        } catch (IOException e) {
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

        Connection.Response res = Jsoup.connect(urlForStepThree)
                .data("wa", wa)
                .data("wresult", wresults)
                .cookies(loginCookies)
                .followRedirects(true)
                .method(Connection.Method.POST)
                .execute();

        loginCookies = res.cookies();

        return res;
    }

    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        progress.dismiss();
        if (!userMesage.isEmpty()) {
            Toast.makeText(activity, userMesage, Toast.LENGTH_LONG).show();
        }

        if (userMesage.equals(activity.getString(R.string.login_accepted))) {
            if (check == 0) {
                if (createAccount()) {
                    Intent intent = new Intent(activity, DashboardActivity.class);
                    activity.startActivity(intent);
                } else if (!createAccount()) {
                    Toast.makeText(activity, "Konto już istnieje", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, DashboardActivity.class);
                    activity.startActivity(intent);
                }
            } else if (check == 1) {
                Intent intent = new Intent(activity, DashboardActivity.class);
                activity.startActivity(intent);
            }
        }

    }

    public boolean createAccount() {

        SharedPreferences sharedPreferences = activity.getSharedPreferences("io.github.wulkanowy", Context.MODE_PRIVATE);
        if (!sharedPreferences.contains(email)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("wulkanowy",email);
            editor.putString(email,email);
            editor.putString("sandi" + email,password);
            editor.putString("county" + email,county);
            editor.commit();
            return true;
        }
        else{
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("wulkanowy",email);
            editor.commit();
            return false;
        }
    }
}
