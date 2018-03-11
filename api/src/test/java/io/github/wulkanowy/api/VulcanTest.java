package io.github.wulkanowy.api;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class VulcanTest {

    @Test(expected = NotLoggedInErrorException.class)
    public void getClientWithoutLoginTest() throws Exception {
        Vulcan vulcan = new Vulcan();

        vulcan.getClient();
    }

    @Test
    public void getClientTest() throws Exception {
        Vulcan vulcan = new Vulcan();
        vulcan.setCredentials("email", "password", "symbol", null);

        Assert.assertThat(vulcan.getClient(), CoreMatchers.instanceOf(Client.class));
    }

    @Test
    public void getClientTwiceTest() throws Exception {
        Vulcan vulcan = new Vulcan();
        vulcan.setCredentials("email", "password", "symbol", null);

        Assert.assertEquals(vulcan.getClient(), vulcan.getClient());
    }
}
