package io.github.wulkanowy.api.login;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;

import io.github.wulkanowy.api.Api;
import io.github.wulkanowy.api.Cookies;

public class Login extends Api {

    private String loginPageUrl = "https://cufs.vulcan.net.pl/{symbol}/Account/LogOn" +
            "?ReturnUrl=%2F{symbol}%2FFS%2FLS%3Fwa%3Dwsignin1.0%26wtrealm%3D" +
            "https%253a%252f%252fuonetplus.vulcan.net.pl%252f{symbol}%252fLoginEndpoint.aspx%26wctx%3D" +
            "https%253a%252f%252fuonetplus.vulcan.net.pl%252f{symbol}%252fLoginEndpoint.aspx";

    private String loginEndpointPageUrl =
            "https://uonetplus.vulcan.net.pl/{symbol}/LoginEndpoint.aspx";

    public Login(Cookies cookies) {
        this.cookies = cookies;
    }

    public String getLoginPageUrl() {
        return loginPageUrl;
    }

    public String getLoginEndpointPageUrl() {
        return loginEndpointPageUrl;
    }

    public String login(String email, String password, String symbol)
            throws BadCredentialsException, LoginErrorException, AccountPermissionException, IOException {
        String certificate = sendCredentials(email, password, symbol);

        return sendCertificate(certificate, symbol);
    }

    public String sendCredentials(String email, String password, String symbol)
            throws IOException, BadCredentialsException {
        loginPageUrl = getLoginPageUrl().replace("{symbol}", symbol);

        Document html = postPageByUrl(loginPageUrl, new String[][]{
                {"LoginName", email},
                {"Password", password}
        });

        if (null != html.select(".ErrorMessage").first()) {
            throw new BadCredentialsException();
        }

        return html.select("input[name=wresult]").attr("value");
    }

    public String sendCertificate(String certificate, String defaultSymbol)
            throws IOException, LoginErrorException, AccountPermissionException {
        String symbol = findSymbol(defaultSymbol, certificate);

        loginEndpointPageUrl = getLoginEndpointPageUrl()
                .replace("{symbol}", symbol);

        Document html = postPageByUrl(loginEndpointPageUrl, new String[][]{
                {"wa", "wsignin1.0"},
                {"wresult", certificate}
        });

        if (html.getElementsByTag("title").text().equals("Logowanie")) {
            throw new AccountPermissionException();
        }

        if (!html.select("title").text().equals("Uonet+")) {
            throw new LoginErrorException();
        }

        return symbol;
    }

    public String findSymbol(String symbol, String certificate) {
        if ("Default".equals(symbol)) {
            return findSymbolInCertificate(certificate);
        }

        return symbol;
    }

    public String findSymbolInCertificate(String certificate) {
        Elements els = Jsoup.parse(certificate.replaceAll(":", ""), "", Parser.xmlParser())
                .select("[AttributeName=\"UserInstance\"] samlAttributeValue");

        if (0 == els.size()) {
            return "";
        }

        return els.get(1).text();
    }
}
