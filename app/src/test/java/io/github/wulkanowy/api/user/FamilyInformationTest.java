package io.github.wulkanowy.api.user;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class FamilyInformationTest extends UserTest {

    private FamilyInformation familyInformation;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        familyInformation = new FamilyInformation(snp);
    }

    @Test
    public void getFamilyMembers() throws Exception {
        List<FamilyMember> familyMemberList = familyInformation.getFamilyMembers();

        Assert.assertEquals(2, familyMemberList.size());

        FamilyMember member0 = familyMemberList.get(0);
        Assert.assertEquals("Marianna Pająk", member0.getName());
        Assert.assertEquals("matka", member0.getKinship());
        Assert.assertEquals("ul. Sportowa 16, 00-123 Warszawa", member0.getAddress());
        Assert.assertEquals("555111222", member0.getTelephones());
        Assert.assertEquals("wulkanowy@example.null", member0.getEmail());

        FamilyMember member1 = familyMemberList.get(1);
        Assert.assertEquals("Dawid Świątek", member1.getName());
        Assert.assertEquals("ojciec", member1.getKinship());
        Assert.assertEquals("ul. Sportowa 18, 00-123 Warszawa", member1.getAddress());
        Assert.assertEquals("555222111", member1.getTelephones());
        Assert.assertEquals("wulkanowy@example.null", member1.getEmail());
    }
}
