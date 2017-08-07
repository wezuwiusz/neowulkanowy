package io.github.wulkanowy.api.school;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SchoolInfoTest extends SchoolTest {

    private SchoolInfo schoolInfo;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        schoolInfo = new SchoolInfo(snp);
    }

    @Test
    public void getSchoolDataTest() throws Exception {
        SchoolData schoolData = schoolInfo.getSchoolData();

        Assert.assertEquals("Zespół Szkół nr 64", schoolData.getName());
        Assert.assertEquals("ul. Wiśniowa 128, 01-234 Rogalowo, Nibylandia",
                schoolData.getAddress());
        Assert.assertEquals("55 5555555", schoolData.getPhoneNumber());
        Assert.assertEquals("Antoni Sobczyk", schoolData.getHeadmaster());
        Assert.assertArrayEquals(new String[]{
                "Zofia Czerwińska [ZC]",
                "Aleksander Krzemiński [AK]",
                "Karolina Kowalska [KK]",
                "Bartek Dąbrowski [BD]"
        }, schoolData.getPedagogues());
    }
}
