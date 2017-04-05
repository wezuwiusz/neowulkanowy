package leszcz_team.wulkanowy.activity.main;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

public class Login extends AsyncTask<Void, Void, Void> {

    String email;
    String password;
    String county;
    Activity activity;
    String userMesage;
    String wresults;
    String wa;
    Document doc4;
    String htmlDefault = "https://cufs.vulcan.net.pl/Default/Account/LogOn";
    String htmlStage2 = "https://cufs.vulcan.net.pl/{locationID}/FS/LS?wa=wsignin1.0&wtrealm=https://uonetplus.vulcan.net.pl/{locationID}/LoginEndpoint.aspx&wctx=https://uonetplus.vulcan.net.pl/{locationID}/LoginEndpoint.aspx";
    String htmlStage3 = "https://uonetplus.vulcan.net.pl/{locationID}/LoginEndpoint.aspx";


    public Login(String emailT, String passwordT, String countyT, Activity mainAC){

        email = emailT;
        password = passwordT;

        county = "powiat" + countyT.substring(7);
        activity = mainAC;
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            Connection.Response initial = Jsoup
                    .connect(htmlDefault)
                    .data("LoginName", email)
                    .data("Password", password)
                    .method(Connection.Method.POST)
                    .execute();

            Map<String, String> loginCookies = initial.cookies();

            CheckPass checkPass = new CheckPass(initial);
            userMesage = checkPass.start();

            county = county.replace("ł", "l");
            htmlStage2 = htmlStage2.replace("{locationID}", county);

            Document doc = Jsoup.connect(htmlStage2)
                    .cookies(loginCookies)
                    .get();

            Elements wresultsInput = doc.select("input[name=wresult]");
            wresults = wresultsInput.attr("value");

            Elements waInput = doc.select("input[name=wa]");
            wa = waInput.attr("value");

            htmlStage3 = htmlStage3.replace("{locationID}", county);

            doc4 = Jsoup.connect(htmlStage3)
                    .data("wa", wa)
                    .data("wresults", wresults)
                    .post();


        }
        catch (IOException e){
            userMesage = e.toString();
        }

        return null;
    }

    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (!userMesage.isEmpty()){
            Toast.makeText(activity, userMesage , Toast.LENGTH_LONG).show();
        }
    }
}
