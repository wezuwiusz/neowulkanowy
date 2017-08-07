package io.github.wulkanowy.api.user;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BasicInformationTest extends UserTest {

    private BasicInformation basicInformation;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        basicInformation = new BasicInformation(snp);
    }

    @Test
    public void getPersonalData() throws Exception {
        PersonalData data = basicInformation.getPersonalData();

        Assert.assertEquals("Maria", data.getFirstName());
        Assert.assertEquals("Kamińska", data.getSurname());
        Assert.assertEquals("Maria Kamińska", data.getFirstAndLastName());
        Assert.assertEquals("Maria Aneta Kamińska", data.getName());
        Assert.assertEquals("01.01.1900, Warszawa", data.getDateAndBirthPlace());
        Assert.assertEquals("12345678900", data.getPesel());
        Assert.assertEquals("Kobieta", data.getGender());
        Assert.assertTrue(data.isPolishCitizenship());
        Assert.assertEquals("Nowak", data.getFamilyName());
        Assert.assertEquals("Gabriela, Kamil", data.getParentsNames());
    }

    @Test
    public void getAddressData() throws Exception {
        AddressData data = basicInformation.getAddressData();

        Assert.assertEquals("ul. Sportowa 16, 00-123 Warszawa", data.getAddress());
        Assert.assertEquals("ul. Sportowa 17, 00-123 Warszawa", data.getRegisteredAddress());
        Assert.assertEquals("ul. Sportowa 18, 00-123 Warszawa", data.getCorrespondenceAddress());
    }

    @Test
    public void getContactDetails() throws Exception {
        ContactDetails data = basicInformation.getContactDetails();

        Assert.assertEquals("005554433", data.getPhoneNumber());
        Assert.assertEquals("555444333", data.getCellPhoneNumber());
        Assert.assertEquals("wulkanowy@example.null", data.getEmail());
    }
}
