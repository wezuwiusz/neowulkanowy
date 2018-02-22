package io.github.wulkanowy.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import io.github.wulkanowy.api.login.Login;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;

public class VulcanTest {

    private Vulcan vulcan;

    @Before
    public void setUp() throws Exception {
        vulcan = new Vulcan();
        vulcan.setClient(Mockito.mock(Client.class));
        vulcan.setLogin(Mockito.mock(Login.class));
    }

    @Test
    public void setFullEndpointInfoTest() throws Exception {
        vulcan.login("http://fakelog.net\\\\admin", "pass", "Default", "123");

        Assert.assertEquals("http", vulcan.getProtocolSchema());
        Assert.assertEquals("fakelog.net", vulcan.getLogHost());
        Assert.assertEquals("admin", vulcan.getEmail());
    }

    @Test(expected = NotLoggedInErrorException.class)
    public void getStudentAndParentNotLoggedInTest() throws Exception {
        vulcan.getStudentAndParent();
    }

    @Test
    public void createSnPTest() throws Exception {
        vulcan.login("wulkanowy@wulkanowy.io", "wulkanowy123", "wulkan");

        SnP snp1 = vulcan.createSnp(Mockito.mock(Client.class), "testSymbol", null);
        Assert.assertEquals(snp1.getId(), null);

        SnP snp2 = vulcan.createSnp(Mockito.mock(Client.class), "testSymbol", "wulkan");
        Assert.assertEquals(snp2.getId(), "wulkan");

    }

    @Test(expected = NotLoggedInErrorException.class)
    public void getAttendanceExceptionText() throws Exception {
        vulcan.getAttendanceTable();
    }
}
