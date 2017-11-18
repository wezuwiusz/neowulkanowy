package io.github.wulkanowy.api.user;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.github.wulkanowy.api.StudentAndParentTestCase;

public class BasicInformationTest extends StudentAndParentTestCase {

    private BasicInformation basicInformation;

    @Before
    public void setUp() throws Exception {
        basicInformation = new BasicInformation(getSnp("UczenDanePodstawowe.html"));
    }

    @Test
    public void getPersonalFirstNameTest() throws Exception {
        Assert.assertEquals("Maria", basicInformation.getPersonalData().getFirstName());
    }

    @Test
    public void getPersonalSurnameTest() throws Exception {
        Assert.assertEquals("Kamińska", basicInformation.getPersonalData().getSurname());
    }

    @Test
    public void getPersonalFirstAndLastNameTest() throws Exception {
        Assert.assertEquals("Maria Kamińska",
                basicInformation.getPersonalData().getFirstAndLastName());
    }

    @Test
    public void getPersonalNameTest() throws Exception {
        Assert.assertEquals("Maria Aneta Kamińska", basicInformation.getPersonalData().getName());
    }

    @Test
    public void getPersonalDateAndBirthPlaceTest() throws Exception {
        Assert.assertEquals("01.01.1900, Warszawa",
                basicInformation.getPersonalData().getDateAndBirthPlace());
    }

    @Test
    public void getPersonalPeselTest() throws Exception {
        Assert.assertEquals("12345678900", basicInformation.getPersonalData().getPesel());
    }

    @Test
    public void getPersonalGenderTest() throws Exception {
        Assert.assertEquals("Kobieta", basicInformation.getPersonalData().getGender());
    }

    @Test
    public void isPersonalPolishCitizenshipTest() throws Exception {
        Assert.assertTrue(basicInformation.getPersonalData().isPolishCitizenship());
    }

    @Test
    public void getPersonalFamilyNameTest() throws Exception {
        Assert.assertEquals("Nowak", basicInformation.getPersonalData().getFamilyName());
    }

    @Test
    public void getPersonalParentsNames() throws Exception {
        Assert.assertEquals("Gabriela, Kamil",
                basicInformation.getPersonalData().getParentsNames());
    }

    @Test
    public void getBasicAddressTest() throws Exception {
        Assert.assertEquals("ul. Sportowa 16, 00-123 Warszawa",
                basicInformation.getAddressData().getAddress());
    }

    @Test
    public void getBasicRegisteredAddressTest() throws Exception {
        Assert.assertEquals("ul. Sportowa 17, 00-123 Warszawa",
                basicInformation.getAddressData().getRegisteredAddress());
    }

    @Test
    public void getBasicCorrespondenceAddressTest() throws Exception {
        Assert.assertEquals("ul. Sportowa 18, 00-123 Warszawa",
                basicInformation.getAddressData().getCorrespondenceAddress());
    }

    @Test
    public void getContactPhoneNumberTest() throws Exception {
        Assert.assertEquals("005554433",
                basicInformation.getContactDetails().getPhoneNumber());
    }

    @Test
    public void getContactCellPhoneNumberTest() throws Exception {
        Assert.assertEquals("555444333",
                basicInformation.getContactDetails().getCellPhoneNumber());
    }

    @Test
    public void getContactEmailTest() throws Exception {
        Assert.assertEquals("wulkanowy@example.null",
                basicInformation.getContactDetails().getEmail());
    }
}
