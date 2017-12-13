package io.github.wulkanowy.api.login;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Pattern;

import io.github.wulkanowy.api.Api;
import io.github.wulkanowy.api.Cookies;

public class Login extends Api {

    private static final String loginPageUrl = "{schema}://cufs.{host}/{symbol}/Account/LogOn" +
            "?ReturnUrl=%2F{symbol}%2FFS%2FLS%3Fwa%3Dwsignin1.0%26wtrealm%3D" +
            "{schema}%253a%252f%252fuonetplus.{host}%252f{symbol}%252fLoginEndpoint.aspx%26wctx%3D" +
            "{schema}%253a%252f%252fuonetplus.{host}%252f{symbol}%252fLoginEndpoint.aspx";

    private static final String loginEndpointPageUrl =
            "{schema}://uonetplus.{host}/{symbol}/LoginEndpoint.aspx";

    private String protocolSchema = "https";

    private String logHost = "vulcan.net.pl";

    private String symbol = "Default";

    public Login(Cookies cookies) {
        this.cookies = cookies;
    }

    public void setProtocolSchema(String schema) {
        this.protocolSchema = schema;
    }

    public void setLogHost(String hostname) {
        this.logHost = hostname;
    }

    public String getLoginPageUrl() {
        return loginPageUrl
                .replace("{schema}", protocolSchema)
                .replaceFirst(Pattern.quote("{host}"), logHost)
                .replace("{host}", logHost.replace(":", "%253A"))
                .replace("{symbol}", symbol);
    }

    public String getLoginEndpointPageUrl() {
        return loginEndpointPageUrl
                .replace("{schema}", protocolSchema)
                .replace("{host}", logHost)
                .replace("{symbol}", symbol);
    }

    public String login(String email, String password, String symbol)
            throws BadCredentialsException, LoginErrorException,
            AccountPermissionException, IOException, VulcanOfflineException {
        String certificate = sendCredentials(email, password, symbol);

        return sendCertificate(certificate, symbol);
    }

    public String sendCredentials(String email, String password, String symbol)
            throws IOException, BadCredentialsException {
        this.symbol = symbol;

        Document html = postPageByUrl(getLoginPageUrl(), new String[][]{
                {"LoginName", email},
                {"Password", password}
        });

        if (null != html.select(".ErrorMessage").first()) {
            throw new BadCredentialsException();
        }

        return html.select("input[name=wresult]").attr("value");
    }

    public String sendCertificate(String certificate, String defaultSymbol)
            throws IOException, LoginErrorException, AccountPermissionException, VulcanOfflineException {
        this.symbol = findSymbol(defaultSymbol, certificate);

        Document html = postPageByUrl(getLoginEndpointPageUrl(), new String[][]{
                {"wa", "wsignin1.0"},
                {"wresult", certificate}
        });

        if (html.getElementsByTag("title").text().equals("Logowanie")) {
            throw new AccountPermissionException();
        }

        if (html.getElementsByTag("title").text().equals("Przerwa techniczna")) {
            throw new VulcanOfflineException();
        }

        if (!html.select("title").text().equals("Uonet+")) {
            throw new LoginErrorException();
        }

        return this.symbol;
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

        if (els.isEmpty()) {
            return "";
        }

        return els.get(1).text();
    }
}
