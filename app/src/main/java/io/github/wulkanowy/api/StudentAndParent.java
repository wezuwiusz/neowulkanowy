package io.github.wulkanowy.api;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.wulkanowy.api.login.LoginErrorException;

public class StudentAndParent extends Vulcan {

    private String startPageUrl = "https://uonetplus.vulcan.net.pl/{locationID}/Start.mvc/Index";

    private String locationID;

    private String uonetPlusOpiekunUrl;

    public StudentAndParent(Cookies cookies, String locID) throws IOException {
        this.cookies = cookies;
        this.locationID = locID;
    }

    public StudentAndParent setUp() throws IOException {
        startPageUrl = startPageUrl.replace("{locationID}", locationID);

        // get link to uonetplus-opiekun.vulcan.net.pl module
        Document startPage = Jsoup.connect(startPageUrl)
                .followRedirects(true)
                .cookies(getCookies())
                .get();
        Element studentTileLink = startPage.select(".panel.linkownia.pracownik.klient > a").first();
        uonetPlusOpiekunUrl = studentTileLink.attr("href");

        //get context module cookie
        Connection.Response res = Jsoup.connect(uonetPlusOpiekunUrl)
                .followRedirects(true)
                .cookies(getCookies())
                .execute();

        cookies.addItems(res.cookies());

        return this;
    }

    public String getLocationID() {
        return locationID;
    }

    public String getID() throws LoginErrorException {
        Pattern pattern = Pattern.compile("([0-9]{6})");
        Matcher matcher = pattern.matcher(uonetPlusOpiekunUrl);

        // Finds all the matches until found by moving the `matcher` forward
        if (!matcher.find()) {
            throw new LoginErrorException();
        }

        String match = matcher.group(1);

        return match;
    }

    public String getRowDataChildValue(Element e, int index) {
        return e.select(".daneWiersz .wartosc").get(index - 1).text();
    }
}
