package io.github.wulkanowy.api.school;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.github.wulkanowy.api.StudentAndParentTestCase;

public class SchoolInfoTest extends StudentAndParentTestCase {

    private SchoolInfo schoolInfo;

    @Before
    public void setUp() throws Exception {
        schoolInfo = new SchoolInfo(getSnp("Szkola.html"));
    }

    @Test
    public void getNameTest() throws Exception {
        Assert.assertEquals("Zespół Szkół nr 64", schoolInfo.getSchoolData().getName());
    }

    @Test
    public void getAddressTest() throws Exception {
        Assert.assertEquals("ul. Wiśniowa 128, 01-234 Rogalowo, Nibylandia",
                schoolInfo.getSchoolData().getAddress());
    }

    @Test
    public void getPhoneNumberTest() throws Exception {
        Assert.assertEquals("55 5555555", schoolInfo.getSchoolData().getPhoneNumber());
    }

    @Test
    public void getHeadmasterTest() throws Exception {
        Assert.assertEquals("Antoni Sobczyk", schoolInfo.getSchoolData().getHeadmaster());
    }

    @Test
    public void getPedagoguesTest() throws Exception {
        Assert.assertArrayEquals(new String[]{
                "Zofia Czerwińska [ZC]",
                "Aleksander Krzemiński [AK]",
                "Karolina Kowalska [KK]",
                "Bartek Dąbrowski [BD]"
        }, schoolInfo.getSchoolData().getPedagogues());
    }
}
