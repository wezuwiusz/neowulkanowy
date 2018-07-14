package io.github.wulkanowy.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

public class ClientTest {

    private String getFixtureAsString(String fixtureFileName) {
        return FixtureHelper.getAsString(getClass().getResourceAsStream(fixtureFileName));
    }

    @Test
    public void setFullEndpointInfoTest() {
        Client client = new Client("http://fakelog.cf\\\\admin", "pass", "Default", "123");

        Assert.assertEquals("fakelog.cf", client.getHost());
        Assert.assertEquals("Default", client.getSymbol());
    }

    @Test
    public void setFullEndpointInfoWithSymbolTest() {
        Client client = new Client("http://fakelog.cf/notdefault\\\\admin", "pass", "Default", "123");

        Assert.assertEquals("fakelog.cf", client.getHost());
        Assert.assertEquals("notdefault", client.getSymbol()); //
    }

    @Test
    public void checkForNoErrorsTest() throws Exception {
        Client client = new Client("", "", "", "123");

        Document doc = Jsoup.parse(getFixtureAsString("login/Logowanie-success.html"));

        Assert.assertEquals(doc, client.checkForErrors(doc, 200));
    }

    @Test(expected = VulcanOfflineException.class)
    public void checkForErrorsOffline() throws Exception {
        Client client = new Client("", "", "", "123");

        Document doc = Jsoup.parse(getFixtureAsString("login/PrzerwaTechniczna.html"));

        client.checkForErrors(doc, 200);
    }

    @Test(expected = NotLoggedInErrorException.class)
    public void checkForErrors() throws Exception {
        Client client = new Client("", "", "", "123");

        Document doc = Jsoup.parse(getFixtureAsString("login/Logowanie-notLoggedIn.html"));

        client.checkForErrors(doc, 200);
    }

    @Test
    public void getFilledUrlTest() throws Exception {
        Client client = new Client("http://fakelog.cf\\\\admin", "", "symbol123", "321");

        Assert.assertEquals("http://uonetplus-opiekun.fakelog.cf/symbol123/321/Oceny/Wszystkie",
                client.getFilledUrl("{schema}://uonetplus-opiekun.{host}/{symbol}/{ID}/Oceny/Wszystkie"));
    }

    @Test
    public void getSymbolTest() {
        Client client = new Client("", "", "symbol4321", "123");

        Assert.assertEquals("symbol4321", client.getSymbol());
    }

    @Test
    public void getSchoolIdTest() throws Exception {
        Client client = new Client("", "", "1", "123456");

        Assert.assertEquals("123456", client.getSchoolId());
    }
}
