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
public class ScramblerTest {

    @Test
    @SdkSuppress(minSdkVersion = 18)
    public void encryptDecryptTest() throws Exception {
        Context targetContext = InstrumentationRegistry.getTargetContext();

        Assert.assertEquals("PASS", Scrambler.decrypt("TEST",
                Scrambler.encrypt("TEST", "PASS", targetContext)));
    }
}