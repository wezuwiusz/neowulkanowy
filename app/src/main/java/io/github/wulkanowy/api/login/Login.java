package io.github.wulkanowy.api.login;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.Vulcan;

public class Login extends Vulcan {

    private String loginPageUrl = "https://cufs.vulcan.net.pl/{symbol}/Account/LogOn";

    private String certificatePageUrl = "https://cufs.vulcan.net.pl/{symbol}"
                    + "/FS/LS?wa=wsignin1.0&wtrealm=https://uonetplus.vulcan.net.pl/{symbol}"
                    + "/LoginEndpoint.aspx&wctx=https://uonetplus.vulcan.net.pl/{symbol}"
                    + "/LoginEndpoint.aspx";

    private String loginEndpointPageUrl =
            "https://uonetplus.vulcan.net.pl/{symbol}/LoginEndpoint.aspx";

    public Login(Cookies cookies) {
        this.cookies = cookies;
    }

    public boolean login(String email, String password, String symbol)
            throws BadCredentialsException, LoginErrorException, AccountPermissionException {
        try {
            sendCredentials(email, password, symbol);
            String[] certificate = getCertificateData(symbol);
            sendCertificate(certificate[0], certificate[1], symbol);
        } catch (IOException e) {
            throw new LoginErrorException();
        }

        return true;
    }

    private void sendCredentials(String email, String password, String symbol)
            throws IOException, BadCredentialsException {
        loginPageUrl = loginPageUrl.replace("{symbol}", symbol);

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

    private String[] getCertificateData(String symbol) throws IOException {
        certificatePageUrl = certificatePageUrl.replace("{symbol}", symbol);

        Document certificatePage = getPageByUrl(certificatePageUrl);

        return new String[]{
                certificatePage.select("input[name=wa]").attr("value"),
                certificatePage.select("input[name=wresult]").attr("value")
        };
    }

    private void sendCertificate(String protocolVersion, String certificate, String symbol)
            throws IOException, LoginErrorException, AccountPermissionException {
        loginEndpointPageUrl = loginEndpointPageUrl.replace("{symbol}", symbol);

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

        if (!html.select("title").text().equals("Uonet+")) {
            throw new LoginErrorException();
        }
    }
}
