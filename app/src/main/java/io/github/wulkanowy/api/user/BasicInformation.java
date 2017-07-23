package io.github.wulkanowy.api.user;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.login.LoginErrorException;

public class BasicInformation extends StudentAndParent {

    private String studentDataPageUrl = "https://uonetplus-opiekun.vulcan.net.pl/{locationID}/{ID}/Uczen.mvc/DanePodstawowe";

    private Document studentDataPageDocument;

    public BasicInformation(Cookies cookies, String locationID) throws IOException, LoginErrorException {
        super(cookies, locationID);

        studentDataPageDocument = getPage();
    }

    private Document getPage() throws IOException, LoginErrorException {
        studentDataPageUrl = studentDataPageUrl.replace("{locationID}", getLocationID());
        studentDataPageUrl = studentDataPageUrl.replace("{ID}", getID());

        return Jsoup.connect(studentDataPageUrl)
                .cookies(getJar())
                .get();
    }

    private String getRowDataChildValue(Element e, int index) {
        return e.select(".daneWiersz .wartosc").get(index - 1).text();
    }

    public PersonalData getPersonalData() {
        Element e = studentDataPageDocument.select(".mainContainer > article").get(0);

        return new PersonalData()
                .setNames(getRowDataChildValue(e, 1))
                .setDateAndBirthPlace(getRowDataChildValue(e, 2))
                .setPesel(getRowDataChildValue(e, 3))
                .setGender(getRowDataChildValue(e, 4))
                .setPolishCitizenship(getRowDataChildValue(e, 5))
                .setFamilyName(getRowDataChildValue(e, 6))
                .setParentsNames(getRowDataChildValue(e, 7));
    }

    public AddressData getAddresData() {
        Element e = studentDataPageDocument.select(".mainContainer > article").get(1);

        return new AddressData()
                .setAddress(getRowDataChildValue(e, 1))
                .setRegisteredAddress(getRowDataChildValue(e, 2))
                .setCorrespondenceAddress(getRowDataChildValue(e, 3));

    }

    public ContactDetails getContactDetails() {
        Element e = studentDataPageDocument.select(".mainContainer > article").get(2);

        return new ContactDetails()
                .setPhoneNumber(getRowDataChildValue(e, 1))
                .setCellPhoneNumber(getRowDataChildValue(e, 2))
                .setEmail(getRowDataChildValue(e, 3));
    }
}
