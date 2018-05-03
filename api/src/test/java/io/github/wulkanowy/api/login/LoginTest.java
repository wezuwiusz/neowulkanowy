package io.github.wulkanowy.api.login;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

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
        Login login = new Login(client);

        Assert.assertEquals("d123", login.login("a@a", "pswd", "d123"));
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
                getFixtureAsString("cert-stock.xml").replaceAll("\\s+",""),
                login.sendCredentials("a@a", "passwd").select("input[name=wresult]").attr("value").replaceAll("\\s+","")
        );
    }

    @Test
    public void sendCertificateNotDefaultSymbolSuccessTest() throws Exception {
        Login login = new Login(getClient("Logowanie-success.html"));

        Assert.assertEquals("wulkanowyschool321",
                login.sendCertificate(new Document(""), "wulkanowyschool321"));
    }

    @Test
    public void sendCertificateDefaultSymbolSuccessTest() throws Exception {
        Login login = new Login(getClient("Logowanie-success.html"));

        Assert.assertEquals("demo12345",
                login.sendCertificate(getFixtureAsDocument("Logowanie-certyfikat.html"), "Default"));
    }

    @Test(expected = AccountPermissionException.class)
    public void sendCertificateAccountPermissionTest() throws Exception {
        Login login = new Login(getClient("Logowanie-brak-dostepu.html"));

        login.sendCertificate(getFixtureAsDocument("cert-stock.xml"), "demo123");
    }

    @Test(expected = LoginErrorException.class)
    public void sendCertificateLoginErrorTest() throws Exception {
        Login login = new Login(getClient("Logowanie-certyfikat.html")); // change to other document

        login.sendCertificate(getFixtureAsDocument("cert-stock.xml"), "demo123");
    }

    @Test
    public void findSymbolInCertificateTest() throws Exception {
        Login login = new Login(getClient("Logowanie-certyfikat.html"));

        String certificate = getFixtureAsString("cert-stock.xml");

        Assert.assertEquals("demo12345", login.findSymbolInCertificate(certificate));
    }

    @Test
    public void findSymbolInInvalidCertificateTest() throws Exception {
        Login login = new Login(getClient("Logowanie-certyfikat.html"));

        Assert.assertEquals("", login.findSymbolInCertificate("<xml></xml>")); // change to real cert with empty symbols
    }
}
