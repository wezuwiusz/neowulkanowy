package io.github.wulkanowy.utils.security;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class SafetyTest {

    @Test
    @SdkSuppress(minSdkVersion = 18)
    public void encryptDecryptTest() throws Exception {
        Context targetContext = InstrumentationRegistry.getTargetContext();

        Safety safety = new Safety();
        Assert.assertEquals("PASS", safety.decrypt("TEST", safety.encrypt("TEST", "PASS", targetContext)));
    }
}
