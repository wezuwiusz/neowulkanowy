package io.github.wulkanowy.api.user;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.github.wulkanowy.api.StudentAndParentTestCase;

public class FamilyInformationTest extends StudentAndParentTestCase {

    private FamilyInformation familyInformation;

    @Before
    public void setUp() throws Exception {
        familyInformation = new FamilyInformation(getSnp("UczenDanePodstawowe.html"));
    }

    @Test
    public void getFamilyMembers() throws Exception {
        Assert.assertEquals(2, familyInformation.getFamilyMembers().size());
    }

    @Test
    public void getNameTest() throws Exception {
        List<FamilyMember> list = familyInformation.getFamilyMembers();
        Assert.assertEquals("Marianna Pająk", list.get(0).getName());
        Assert.assertEquals("Dawid Świątek", list.get(1).getName());
    }

    @Test
    public void getKinshipTest() throws Exception {
        List<FamilyMember> list = familyInformation.getFamilyMembers();
        Assert.assertEquals("matka", list.get(0).getKinship());
        Assert.assertEquals("ojciec", list.get(1).getKinship());
    }

    @Test
    public void getAddressTest() throws Exception {
        List<FamilyMember> list = familyInformation.getFamilyMembers();
        Assert.assertEquals("ul. Sportowa 16, 00-123 Warszawa", list.get(0).getAddress());
        Assert.assertEquals("ul. Sportowa 18, 00-123 Warszawa", list.get(1).getAddress());
    }

    @Test
    public void getTelephonesTest() throws Exception {
        List<FamilyMember> list = familyInformation.getFamilyMembers();
        Assert.assertEquals("555111222", list.get(0).getTelephones());
        Assert.assertEquals("555222111", list.get(1).getTelephones());
    }

    @Test
    public void getEmailTest() throws Exception {
        List<FamilyMember> list = familyInformation.getFamilyMembers();
        Assert.assertEquals("wulkanowy@example.null", list.get(0).getEmail());
        Assert.assertEquals("wulkanowy@example.null", list.get(1).getEmail());
    }
}
