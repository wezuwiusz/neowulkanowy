package io.github.wulkanowy.api.user;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import io.github.wulkanowy.api.StudentAndParent;

public class BasicInformation {

    private Document studentDataPageDocument;

    private StudentAndParent snp;

    private static final String STUDENT_DATA_PAGE_URL = "Uczen.mvc/DanePodstawowe";

    private static final String CONTENT_QUERY = ".mainContainer > article";

    public BasicInformation(StudentAndParent snp) {
        this.snp = snp;
    }

    public Document getStudentDataPageDocument() throws IOException {
        if (null == studentDataPageDocument) {
            studentDataPageDocument = snp.getSnPPageDocument(STUDENT_DATA_PAGE_URL);
        }

        return studentDataPageDocument;
    }

    public PersonalData getPersonalData() throws IOException {
        Element e = getStudentDataPageDocument().select(CONTENT_QUERY).get(0);

        String name = snp.getRowDataChildValue(e, 1);
        String[] names = name.split(" ");

        return new PersonalData()
                .setName(name)
                .setFirstName(names[0])
                .setSurname(names[names.length - 1])
                .setFirstAndLastName(names[0] + " " + names[names.length - 1])
                .setDateAndBirthPlace(snp.getRowDataChildValue(e, 2))
                .setPesel(snp.getRowDataChildValue(e, 3))
                .setGender(snp.getRowDataChildValue(e, 4))
                .setPolishCitizenship("Tak".equals(snp.getRowDataChildValue(e, 5)))
                .setFamilyName(snp.getRowDataChildValue(e, 6))
                .setParentsNames(snp.getRowDataChildValue(e, 7));
    }

    public AddressData getAddressData() throws IOException {
        Element e = getStudentDataPageDocument().select(CONTENT_QUERY).get(1);

        return new AddressData()
                .setAddress(snp.getRowDataChildValue(e, 1))
                .setRegisteredAddress(snp.getRowDataChildValue(e, 2))
                .setCorrespondenceAddress(snp.getRowDataChildValue(e, 3));

    }

    public ContactDetails getContactDetails() throws IOException {
        Element e = getStudentDataPageDocument().select(CONTENT_QUERY).get(2);

        return new ContactDetails()
                .setPhoneNumber(snp.getRowDataChildValue(e, 1))
                .setCellPhoneNumber(snp.getRowDataChildValue(e, 2))
                .setEmail(snp.getRowDataChildValue(e, 3));
    }
}
