package io.github.wulkanowy.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

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

    @Test
    public void getClientTwiceTest() throws Exception {
        Vulcan vulcan = new Vulcan();
        Assert.assertTrue(vulcan.getClient().equals(vulcan.getClient()));
    }

    @Test
    public void getLoginTwiceTest() throws Exception {
        Vulcan vulcan = new Vulcan();
        Assert.assertTrue(vulcan.getLogin().equals(vulcan.getLogin()));
    }

    @Test(expected = NotLoggedInErrorException.class)
    public void getStudentAndParentNotLoggedInTest() throws Exception {
        vulcan.getStudentAndParent();
    }

    @Test
    public void getStudentAndParentTwiceTest() throws Exception {
        Client client = Mockito.mock(Client.class);
        Map<String, String> cookies = new HashMap<>();
        cookies.put("test", "test");
        Mockito.when(client.getCookies()).thenReturn(cookies);

        SnP snp = Mockito.mock(StudentAndParent.class);
        Mockito.doNothing().when(snp).storeContextCookies();

        Vulcan vulcan = Mockito.mock(Vulcan.class);
        Mockito.when(vulcan.getClient()).thenReturn(client);
        Mockito.when(vulcan.getStudentAndParent()).thenCallRealMethod();
        Mockito.when(vulcan.createSnp(Mockito.any(Client.class), Mockito.any())).thenReturn(snp);

        vulcan.getStudentAndParent();
        vulcan.getStudentAndParent();
    }

    @Test
    public void createSnPTest() throws Exception {
        vulcan.login("wulkanowy@wulkanowy.io", "wulkanowy123", "wulkan");

        SnP snp1 = vulcan.createSnp(Mockito.mock(Client.class), null);
        Assert.assertEquals(null, snp1.getId());

        SnP snp2 = vulcan.createSnp(Mockito.mock(Client.class), "wulkan");
        Assert.assertEquals("wulkan", snp2.getId());

    }

    @Test(expected = NotLoggedInErrorException.class)
    public void getAttendanceExceptionText() throws Exception {
        vulcan.getAttendanceTable();
    }
}
