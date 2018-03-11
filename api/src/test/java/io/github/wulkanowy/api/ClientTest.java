package io.github.wulkanowy.api;

import org.hamcrest.CoreMatchers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

import io.github.wulkanowy.api.login.Login;

public class ClientTest {

    private String getFixtureAsString(String fixtureFileName) {
        return FixtureHelper.getAsString(getClass().getResourceAsStream(fixtureFileName));
    }

    @Test
    public void setFullEndpointInfoTest() throws Exception {
        Client client = new Client("http://fakelog.net\\\\admin", "pass", "Default");

        Assert.assertEquals("fakelog.net", client.getHost());
        Assert.assertEquals("Default", client.getSymbol());
    }

    @Test
    public void checkForNoErrorsTest() throws Exception {
        Client client = new Client("", "", "");

        Document doc = Jsoup.parse(getFixtureAsString("login/Logowanie-success.html"));

        Assert.assertEquals(doc, client.checkForErrors(doc));
    }

    @Test(expected = VulcanOfflineException.class)
    public void checkForErrorsOffline() throws Exception {
        Client client = new Client("", "", "");

        Document doc = Jsoup.parse(getFixtureAsString("login/PrzerwaTechniczna.html"));

        client.checkForErrors(doc);
    }

    @Test(expected = NotLoggedInErrorException.class)
    public void checkForErrors() throws Exception {
        Client client = new Client("", "", "");

        Document doc = Jsoup.parse(getFixtureAsString("login/Logowanie-notLoggedIn.html"));

        client.checkForErrors(doc);
    }

    @Test
    public void getClientTest() throws Exception {
        Client client = new Client("", "", "");

        Assert.assertThat(client.getLogin(), CoreMatchers.instanceOf(Login.class));
    }

    @Test
    public void getClientTwiceTest() throws Exception {
        Client client = new Client("", "", "");

        Assert.assertEquals(client.getLogin(), client.getLogin());
    }

    @Test
    public void getFilledUrlTest() throws Exception {
        Client client = new Client("http://fakelog.cf\\\\admin", "", "symbol123");

        Assert.assertEquals("http://uonetplus.fakelog.cf/symbol123/LoginEndpoint.aspx",
                client.getFilledUrl("{schema}://uonetplus.{host}/{symbol}/LoginEndpoint.aspx"));
    }

    @Test
    public void getSymbolTest() throws Exception {
        Client client = new Client("", "", "symbol4321");

        Assert.assertEquals("symbol4321", client.getSymbol());
    }
}
