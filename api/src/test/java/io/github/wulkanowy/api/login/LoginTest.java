package io.github.wulkanowy.api.login;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import io.github.wulkanowy.api.Client;
import io.github.wulkanowy.api.FixtureHelper;

public class LoginTest {

    private Document getFixtureAsDocument(String fixtureFileName) {
        return Jsoup.parse(getFixtureAsString(fixtureFileName));
    }

    private String getFixtureAsString(String fixtureFileName) {
        return FixtureHelper.getAsString(getClass().getResourceAsStream(fixtureFileName));
    }

    private Client getClient(String fixtureFileName) throws Exception {
        Document doc = getFixtureAsDocument(fixtureFileName);

        Client client = Mockito.mock(Client.class);
        Mockito.when(client.postPageByUrl(Mockito.anyString(), Mockito.any(String[][].class))).thenReturn(doc);
        Mockito.when(client.getPageByUrl(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(doc);

        return client;
    }

    @Test
    public void loginTest() throws Exception {
        Client client = getClient("Logowanie-success.html");
        Mockito.when(client.getPageByUrl(Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(getFixtureAsDocument("Logowanie-error.html"));
        Mockito.when(client.postPageByUrl(Mockito.eq(Login.LOGIN_PAGE_URL), Mockito.any(String[][].class)))
                .thenReturn(getFixtureAsDocument("Logowanie-certyfikat.html"));
        Mockito.doCallRealMethod().when(client).setSymbol(Mockito.anyString());
        Mockito.when(client.getSymbol()).thenCallRealMethod();
        Mockito.when(client.getHost()).thenReturn("fakelog.cf");
        Login login = new Login(client);
        login.login("a@a", "pswd", "d123");

        Assert.assertEquals("d123", client.getSymbol());
    }

    @Test(expected = BadCredentialsException.class)
    public void sendWrongCredentialsTest() throws Exception {
        Client client = getClient("Logowanie-error.html");
        Mockito.when(client.getPageByUrl(Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(getFixtureAsDocument("Logowanie-error.html")); // -error.html because it html with form used by
        Login login = new Login(client);

        login.sendCredentials("a@a", "pswd");
    }

    @Test
    public void sendCredentialsCertificateTest() throws Exception {
        Client client = getClient("Logowanie-certyfikat.html");
        Mockito.when(client.getPageByUrl(Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(getFixtureAsDocument("Logowanie-error.html")); // -error.html because it html with form used by
        Login login = new Login(client);

        Assert.assertEquals(
                getFixtureAsString("cert-stock.xml").replaceAll("\\s+", ""),
                login.sendCredentials("a@a", "passwd")
                        .select("input[name=wresult]")
                        .attr("value")
                        .replaceAll("\\s+", "")
        );
    }

    @Test
    public void sendCertificateNotDefaultSymbolSuccessTest() throws Exception {
        Client client = getClient("Logowanie-success.html");
        Mockito.doCallRealMethod().when(client).setSymbol(Mockito.anyString());
        Mockito.when(client.getSymbol()).thenCallRealMethod();
        Mockito.when(client.getHost()).thenReturn("fakelog.cf");
        Login login = new Login(client);

        login.sendCertificate(getFixtureAsDocument("Logowanie-certyfikat.html"), "wulkanowyschool321");

        Assert.assertEquals("wulkanowyschool321", client.getSymbol());
    }

    @Test
    public void sendCertificateDefaultSymbolSuccessTest() throws Exception {
        Client client = getClient("Logowanie-success.html");
        Mockito.doCallRealMethod().when(client).setSymbol(Mockito.anyString());
        Mockito.when(client.getSymbol()).thenCallRealMethod();
        Mockito.when(client.getHost()).thenReturn("fakelog.cf");
        Login login = new Login(client);

        login.sendCertificate(getFixtureAsDocument("Logowanie-certyfikat.html"), "Default");

        Assert.assertEquals("demo12345", client.getSymbol());
    }

    @Test(expected = AccountPermissionException.class)
    public void sendCertificateAccountPermissionTest() throws Exception {
        Client client = getClient("Logowanie-brak-dostepu.html");

        Login login = new Login(client);

        login.sendCertificate(getFixtureAsDocument("Logowanie-certyfikat.html"), "demo123");
    }

    @Test(expected = LoginErrorException.class)
    public void sendCertificateLoginErrorTest() throws Exception {
        Login login = new Login(getClient("Logowanie-certyfikat.html")); // change to other document

        login.sendCertificate(getFixtureAsDocument("Logowanie-certyfikat.html"), "demo123");
    }

    @Test
    public void findSymbolInCertificateTest() throws Exception {
        Login login = new Login(getClient("Logowanie-certyfikat.html"));

        String certificate = getFixtureAsString("cert-stock.xml");
        List<String> symbols = login.getSymbolsFromCertificate(certificate);

        Assert.assertEquals("demo12345", login.getLastSymbol(symbols));
    }

    @Test(expected = AccountPermissionException.class)
    public void findSymbolInCertificateWithoutSecondInstanceTest() throws Exception {
        Login login = new Login(getClient("Logowanie-certyfikat.html"));

        List<String> symbols = login.getSymbolsFromCertificate(getFixtureAsString("cert-no-symbols.xml"));

        login.getLastSymbol(symbols);
    }
}
