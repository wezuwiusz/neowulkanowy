package io.github.wulkanowy.api.login;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.Vulcan;

public class Login extends Vulcan {

    private String loginPageUrl = "https://cufs.vulcan.net.pl/{locationID}/Account/LogOn";

    private String certificatePageUrl =
            "https://cufs.vulcan.net.pl/"
                    + "{locationID}/FS/LS?wa=wsignin1.0&wtrealm=https://uonetplus.vulcan.net.pl/"
                    + "{locationID}/LoginEndpoint.aspx&wctx=https://uonetplus.vulcan.net.pl/"
                    + "{locationID}/LoginEndpoint.aspx";

    private String loginEndpointPageUrl =
            "https://uonetplus.vulcan.net.pl/{locationID}/LoginEndpoint.aspx";

    public Login(Cookies cookies) {
        this.cookies = cookies;
    }

    public boolean login(String email, String password, String county)
            throws BadCredentialsException, LoginErrorException, AccountPermissionException {
        try {
            sendCredentials(email, password, county);
            String[] certificate = getCertificateData(county);
            sendCertificate(certificate[0], certificate[1], county);
        } catch (IOException e) {
            throw new LoginErrorException();
        }

        return true;
    }

    private void sendCredentials(String email, String password, String county)
            throws IOException, BadCredentialsException {
        loginPageUrl = loginPageUrl.replace("{locationID}", county);

        Connection.Response response = Jsoup.connect(loginPageUrl)
                .data("LoginName", email)
                .data("Password", password)
                .method(Connection.Method.POST)
                .execute();

        setCookies(response.cookies());
        Document document = response.parse();

        if (null != document.select(".ErrorMessage").first()) {
            throw new BadCredentialsException();
        }
    }

    private String[] getCertificateData(String county) throws IOException {
        certificatePageUrl = certificatePageUrl.replace("{locationID}", county);

        Document certificatePage = Jsoup.connect(certificatePageUrl)
                .cookies(getCookies())
                .get();

        return new String[]{
                certificatePage.select("input[name=wa]").attr("value"),
                certificatePage.select("input[name=wresult]").attr("value")
        };
    }

    private void sendCertificate(String protocolVersion, String certificate, String county)
            throws IOException, LoginErrorException, AccountPermissionException {
        loginEndpointPageUrl = loginEndpointPageUrl.replace("{locationID}", county);

        Connection.Response response = Jsoup.connect(loginEndpointPageUrl)
                .data("wa", protocolVersion)
                .data("wresult", certificate)
                .cookies(getCookies())
                .followRedirects(true)
                .method(Connection.Method.POST)
                .execute();

        addCookies(response.cookies());
        Document html = response.parse();

        if (html.getElementsByTag("title").text().equals("Logowanie")) {
            throw new AccountPermissionException();
        }

        if (!html.select(".welcome").text().equals("Dzień dobry!")) {
            throw new LoginErrorException();
        }
    }
}
