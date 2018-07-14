package io.github.wulkanowy.api.login;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import io.github.wulkanowy.api.Client;
import io.github.wulkanowy.api.NotLoggedInErrorException;
import io.github.wulkanowy.api.VulcanException;

public class Login {

    protected static final String LOGIN_PAGE_URL = "{schema}://cufs.{host}/{symbol}/Account/LogOn";

    private static final String LOGIN_PAGE_URL_QUERY = "?ReturnUrl=%2F{symbol}%2FFS%2FLS%3Fwa%3Dwsignin1.0%26wtrealm%3D" +
            "{schema}%253a%252f%252fuonetplus.{host}%252f{symbol}%252fLoginEndpoint.aspx%26wctx%3D" +
            "{schema}%253a%252f%252fuonetplus.{host}%252f{symbol}%252fLoginEndpoint.aspx";

    private Client client;

    private static final Logger logger = LoggerFactory.getLogger(Login.class);

    public Login(Client client) {
        this.client = client;
    }

    public void login(String email, String password, String symbol) throws VulcanException, IOException {
        Document certDoc = sendCredentials(email, password);

        if ("Błąd".equals(certDoc.title())) {
            client.clearCookies();
            throw new NotLoggedInErrorException(certDoc.body().text());
        }

        sendCertificate(certDoc, symbol);
    }

    Document sendCredentials(String email, String password) throws IOException, VulcanException {
        String[][] credentials = new String[][]{
                {"LoginName", email},
                {"Password", password}
        };

        Document nextDoc = sendCredentialsData(credentials, LOGIN_PAGE_URL + LOGIN_PAGE_URL_QUERY.replace(":", "%253A"));

        Element errorMessage = nextDoc.selectFirst(".ErrorMessage, #ErrorTextLabel");
        if (null != errorMessage) {
            throw new BadCredentialsException(errorMessage.text());
        }

        return nextDoc;
    }

    private Document sendCredentialsData(String[][] credentials, String nextUrl) throws IOException, VulcanException {
        Element formFirst = client.getPageByUrl(nextUrl, false).selectFirst("#form1");

        if (null != formFirst) { // only on adfs login
            Document formSecond = client.postPageByUrl(
                    formFirst.attr("abs:action"),
                    getFormStateParams(formFirst, "", "")
            );
            credentials = getFormStateParams(formSecond, credentials[0][1], credentials[1][1]);
            nextUrl = formSecond.selectFirst("#form1").attr("abs:action");
        }

        return client.postPageByUrl(nextUrl, credentials);
    }

    private String[][] getFormStateParams(Element form, String email, String password) {
        return new String[][]{
                {"__VIEWSTATE", form.select("#__VIEWSTATE").val()},
                {"__VIEWSTATEGENERATOR", form.select("#__VIEWSTATEGENERATOR").val()},
                {"__EVENTVALIDATION", form.select("#__EVENTVALIDATION").val()},
                {"__db", form.select("input[name=__db]").val()},
                {"PassiveSignInButton.x", "0"},
                {"PassiveSignInButton.y", "0"},
                {"SubmitButton.x", "0"},
                {"SubmitButton.y", "0"},
                {"UsernameTextBox", email},
                {"PasswordTextBox", password},
        };
    }

    void sendCertificate(Document doc, String defaultSymbol) throws IOException, VulcanException {
        client.setSymbol(findSymbol(defaultSymbol, doc.select("input[name=wresult]").val()));

        Document targetDoc = sendCertData(doc);
        String title = targetDoc.title();

        if ("Working...".equals(title)) { // on adfs login
            logger.info("ADFS login");
            title = sendCertData(targetDoc).title();
        }

        if ("Logowanie".equals(title)) {
            throw new AccountPermissionException("No account access. Try another symbol");
        }

        if (!"Uonet+".equals(title)) {
            logger.debug("Login failed. Body: {}", targetDoc.body());
            throw new LoginErrorException("Expected page title `UONET+`, got " + title);
        }

        client.setSchools(new StartPage(client).getSchools(targetDoc));
    }

    private Document sendCertData(Document doc) throws IOException, VulcanException {
        String url = doc.select("form[name=hiddenform]").attr("action");

        return client.postPageByUrl(url.replaceFirst("Default", "{symbol}"), new String[][]{
                {"wa", "wsignin1.0"},
                {"wresult", doc.select("input[name=wresult]").val()},
                {"wctx", doc.select("input[name=wctx]").val()}
        });
    }

    private String findSymbol(String symbol, String certificate) throws AccountPermissionException {
        if ("Default".equals(symbol)) {
            return findSymbolInCertificate(certificate);
        }

        return symbol;
    }

    String findSymbolInCertificate(String certificate) throws AccountPermissionException {
        Elements instances = Jsoup
                .parse(certificate.replaceAll(":", ""), "", Parser.xmlParser())
                .select("[AttributeName=\"UserInstance\"] samlAttributeValue");

        if (instances.isEmpty()) { // on adfs login
            return "";
        }

        if (instances.size() < 2) { // 1st index is always `Default`
            throw new AccountPermissionException("First login detected, specify symbol");
        }

        return instances.get(1).text();
    }
}
