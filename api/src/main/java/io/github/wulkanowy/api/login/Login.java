package io.github.wulkanowy.api.login;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;

import io.github.wulkanowy.api.Client;
import io.github.wulkanowy.api.VulcanException;

public class Login {

    private static final String LOGIN_PAGE_URL = "{schema}://cufs.{host}/{symbol}/Account/LogOn" +
            "?ReturnUrl=%2F{symbol}%2FFS%2FLS%3Fwa%3Dwsignin1.0%26wtrealm%3D" +
            "{schema}%253a%252f%252fuonetplus.{host}%252f{symbol}%252fLoginEndpoint.aspx%26wctx%3D" +
            "{schema}%253a%252f%252fuonetplus.{host}%252f{symbol}%252fLoginEndpoint.aspx";

    private static final String LOGIN_ENDPOINT_PAGE_URL =
            "{schema}://uonetplus.{host}/{symbol}/LoginEndpoint.aspx";

    private Client client;

    private String symbol;

    public Login(Client client) {
        this.client = client;
    }

    public String login(String email, String password, String symbol) throws VulcanException, IOException {
        String certificate = sendCredentials(email, password, symbol);

        return sendCertificate(certificate, symbol);
    }

    String sendCredentials(String email, String password, String symbol) throws IOException, VulcanException {
        this.symbol = symbol;

        Document html = client.postPageByUrl(LOGIN_PAGE_URL, new String[][]{
                {"LoginName", email},
                {"Password", password}
        });

        if (null != html.select(".ErrorMessage").first()) {
            throw new BadCredentialsException();
        }

        return html.select("input[name=wresult]").attr("value");
    }

    String sendCertificate(String certificate, String defaultSymbol) throws IOException, VulcanException {
        this.symbol = findSymbol(defaultSymbol, certificate);
        client.setSymbol(this.symbol);

        String title = client.postPageByUrl(LOGIN_ENDPOINT_PAGE_URL, new String[][]{
                {"wa", "wsignin1.0"},
                {"wresult", certificate}
        }).select("title").text();

        if ("Logowanie".equals(title)) {
            throw new AccountPermissionException();
        }

        if (!"Uonet+".equals(title)) {
            throw new LoginErrorException();
        }

        return this.symbol;
    }

    private String findSymbol(String symbol, String certificate) {
        if ("Default".equals(symbol)) {
            return findSymbolInCertificate(certificate);
        }

        return symbol;
    }

    String findSymbolInCertificate(String certificate) {
        Elements els = Jsoup
                .parse(certificate.replaceAll(":", ""), "", Parser.xmlParser())
                .select("[AttributeName=\"UserInstance\"] samlAttributeValue");

        if (els.isEmpty()) {
            return "";
        }

        return els.get(1).text();
    }
}
