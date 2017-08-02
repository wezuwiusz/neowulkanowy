package io.github.wulkanowy.api.user;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.login.LoginErrorException;

public class BasicInformation {

    private StudentAndParent snp = null;

    private Document studentDataPageDocument;

    public BasicInformation(User user, StudentAndParent snp)
            throws IOException, LoginErrorException {
        this.snp = snp;

        studentDataPageDocument = user.getPage();
    }

    public PersonalData getPersonalData() {
        Element e = studentDataPageDocument.select(".mainContainer > article").get(0);

        return new PersonalData()
                .setNames(snp.getRowDataChildValue(e, 1))
                .setDateAndBirthPlace(snp.getRowDataChildValue(e, 2))
                .setPesel(snp.getRowDataChildValue(e, 3))
                .setGender(snp.getRowDataChildValue(e, 4))
                .setPolishCitizenship(snp.getRowDataChildValue(e, 5))
                .setFamilyName(snp.getRowDataChildValue(e, 6))
                .setParentsNames(snp.getRowDataChildValue(e, 7));
    }

    public AddressData getAddresData() {
        Element e = studentDataPageDocument.select(".mainContainer > article").get(1);

        return new AddressData()
                .setAddress(snp.getRowDataChildValue(e, 1))
                .setRegisteredAddress(snp.getRowDataChildValue(e, 2))
                .setCorrespondenceAddress(snp.getRowDataChildValue(e, 3));

    }

    public ContactDetails getContactDetails() {
        Element e = studentDataPageDocument.select(".mainContainer > article").get(2);

        return new ContactDetails()
                .setPhoneNumber(snp.getRowDataChildValue(e, 1))
                .setCellPhoneNumber(snp.getRowDataChildValue(e, 2))
                .setEmail(snp.getRowDataChildValue(e, 3));
    }
}
