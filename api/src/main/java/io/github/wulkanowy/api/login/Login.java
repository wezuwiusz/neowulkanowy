package io.github.wulkanowy.api.login;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;

import io.github.wulkanowy.api.Client;
import io.github.wulkanowy.api.VulcanException;

public class Login {

    static final String LOGIN_PAGE_URL = "{schema}://cufs.{host}/{symbol}/Account/LogOn" +
            "?ReturnUrl=%2F{symbol}%2FFS%2FLS%3Fwa%3Dwsignin1.0%26wtrealm%3D" +
            "{schema}%253a%252f%252fuonetplus.{host}%252f{symbol}%252fLoginEndpoint.aspx%26wctx%3D" +
            "{schema}%253a%252f%252fuonetplus.{host}%252f{symbol}%252fLoginEndpoint.aspx";

    private Client client;

    public Login(Client client) {
        this.client = client;
    }

    public String login(String email, String password, String symbol) throws VulcanException, IOException {
        Document certDoc = sendCredentials(email, password);

        return sendCertificate(certDoc, symbol);
    }

    Document sendCredentials(String email, String password) throws IOException, VulcanException {
        String[][] credentials = new String[][]{
                {"LoginName", email},
                {"Password", password}
        };

        String nextUrl = LOGIN_PAGE_URL;
        Document loginPage = client.getPageByUrl(nextUrl, false);

        Element formFirst = loginPage.select("#form1").first();
        if (null != formFirst) { // on adfs login
            Document formSecond = client.postPageByUrl(
                    formFirst.attr("abs:action"),
                    getFormStateParams(formFirst, "", "")
            );
            credentials = getFormStateParams(formSecond, email, password);
            nextUrl = formSecond.select("#form1").first().attr("abs:action");
        } else if (!"Logowanie".equals(loginPage.select("#h1Default").text())) {
            throw new VulcanException("Expected login page, got page with title: " + loginPage.title());
        }

        Document html = client.postPageByUrl(nextUrl, credentials);

        Element errorMessage = html.select(".ErrorMessage, #ErrorTextLabel").first();
        if (null != errorMessage) {
            throw new BadCredentialsException(errorMessage.text());
        }

        return html;
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

    String sendCertificate(Document doc, String defaultSymbol) throws IOException, VulcanException {
        String certificate = doc.select("input[name=wresult]").val();

        if ("".equals(certificate)) {
            throw new VulcanException("Expected certificate, got empty string. Page title: " + doc.title());
        }

        client.setSymbol(findSymbol(defaultSymbol, certificate));

        Document targetDoc = sendCertData(doc);
        String title = targetDoc.title();

        if ("Working...".equals(title)) { // on adfs login
            title = sendCertData(targetDoc).title();
        }

        if ("Logowanie".equals(title)) {
            throw new AccountPermissionException("No account access. Try another symbol");
        }

        if (!"Uonet+".equals(title)) {
            throw new LoginErrorException("Expected page title `UONET+`, got " + title);
        }

        return client.getSymbol();
    }

    private Document sendCertData(Document doc) throws IOException, VulcanException {
        String url = doc.select("form[name=hiddenform]").attr("action");

        if (!doc.title().equals("Working...")) {
            throw new VulcanException("Expected certificate page, got page with title: " + doc.title());
        }

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
