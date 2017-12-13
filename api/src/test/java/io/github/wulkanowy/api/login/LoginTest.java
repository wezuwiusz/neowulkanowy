package io.github.wulkanowy.api.login;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.FixtureHelper;

public class LoginTest {

    public String getFixtureAsString(String fixtureFileName) {
        return FixtureHelper.getAsString(getClass().getResourceAsStream(fixtureFileName));
    }

    public Login getSetUpLogin(String fixtureFileName) throws Exception {
        Document tablePageDocument = Jsoup.parse(getFixtureAsString(fixtureFileName));

        Login login = Mockito.mock(Login.class);
        Mockito.when(login.postPageByUrl(Mockito.anyString(), Mockito.any(String[][].class))
        ).thenReturn(tablePageDocument);
        Mockito.when(login.getLoginPageUrl()).thenReturn("");
        Mockito.when(login.getLoginEndpointPageUrl()).thenReturn("asdf");
        return login;
    }

    @Test
    public void loginTest() throws Exception {
        Login login = getSetUpLogin("Logowanie-success.html");
        Mockito.when(login.login(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenCallRealMethod();
        Mockito.when(login.sendCredentials(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn("<xml>");
        Mockito.when(login.sendCertificate(Mockito.anyString(), Mockito.anyString())).thenReturn("d123");
        Assert.assertEquals("d123", login.login("a@a", "pswd", "d123"));
    }

    @Test
    public void loginDefaultTest() throws Exception {
        Login login = getSetUpLogin("Logowanie-success.html");
        Mockito.when(login.getLoginEndpointPageUrl()).thenReturn("asdf");
        Mockito.when(login.login(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenCallRealMethod();
        Mockito.when(login.sendCredentials(Mockito.anyString(), Mockito.anyString(), Mockito.eq("Default")))
                .thenReturn(getFixtureAsString("cert.xml"));
        Mockito.when(login.findSymbol(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
        Mockito.when(login.sendCertificate(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
        Mockito.when(login.findSymbolInCertificate(Mockito.anyString())).thenCallRealMethod();
        Assert.assertEquals("demo12345", login.login("a@a", "pswd", "Default"));
    }

    @Test(expected = BadCredentialsException.class)
    public void sendWrongCredentialsTest() throws Exception {
        Login login = getSetUpLogin("Logowanie-error.html");
        Mockito.when(login.sendCredentials(
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();

        login.sendCredentials("a@a", "pswd", "d123");
    }

    @Test
    public void sendCredentialsCertificateTest() throws Exception {
        Login login = getSetUpLogin("Logowanie-certyfikat.html");
        Mockito.when(login.sendCredentials(
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
        Mockito.when(login.getLoginPageUrl()).thenReturn("http://a.a");

        Assert.assertEquals(
                getFixtureAsString("cert.xml").replaceAll("\\s+",""),
                login.sendCredentials("a@a", "passwd", "d123").replaceAll("\\s+","")
        );
    }

    @Test
    public void sendCertificateNotDefaultSymbolSuccessTest() throws Exception {
        Login login = getSetUpLogin("Logowanie-success.html");
        Mockito.when(login.findSymbol(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
        Mockito.when(login.sendCertificate(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
        Assert.assertEquals("wulkanowyschool321", login.sendCertificate("", "wulkanowyschool321"));
    }

    @Test
    public void sendCertificateDefaultSymbolSuccessTest() throws Exception {
        Login login = getSetUpLogin("Logowanie-success.html");
        Mockito.when(login.findSymbol(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
        Mockito.when(login.findSymbolInCertificate(Mockito.anyString())).thenCallRealMethod();
        Mockito.when(login.sendCertificate(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
        Assert.assertEquals("demo12345",
                login.sendCertificate(getFixtureAsString("cert.xml"), "Default"));
    }

    @Test(expected = AccountPermissionException.class)
    public void sendCertificateAccountPermissionTest() throws Exception {
        Login login = getSetUpLogin("Logowanie-brak-dostepu.html");
        Mockito.when(login.findSymbol(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
        Mockito.when(login.sendCertificate(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
        login.sendCertificate(getFixtureAsString("cert.xml"), "demo123");
    }

    @Test(expected = LoginErrorException.class)
    public void sendCertificateLoginErrorTest() throws Exception {
        Login login = getSetUpLogin("Logowanie-certyfikat.html"); // change to other document
        Mockito.when(login.findSymbol(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
        Mockito.when(login.sendCertificate(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
        login.sendCertificate(getFixtureAsString("cert.xml"), "demo123");
    }

    @Test(expected = VulcanOfflineException.class)
    public void sendCertificateVulcanOfflineTest() throws Exception {
        Login login = getSetUpLogin("PrzerwaTechniczna.html");
        Mockito.when(login.findSymbol(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
        Mockito.when(login.sendCertificate(Mockito.anyString(), Mockito.anyString())).thenCallRealMethod();
        login.sendCertificate(getFixtureAsString("cert.xml"), "demo123");
    }

    @Test
    public void findSymbolInCertificateTest() throws Exception {
        Login login = new Login(new Cookies());

        String certificate = getFixtureAsString("cert.xml");

        Assert.assertEquals("demo12345", login.findSymbolInCertificate(certificate));
    }

    @Test
    public void findSymbolInInvalidCertificateTest() throws Exception {
        Login login = new Login(new Cookies());

        Assert.assertEquals("", login.findSymbolInCertificate("<xml></xml>")); // change to real cert with empty symbols
    }
}
