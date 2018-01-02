package io.github.wulkanowy.utils.security;

import android.content.Context;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@SdkSuppress(minSdkVersion = 18)
@RunWith(AndroidJUnit4.class)
public class ScramblerTest {

    private Context targetContext;

    private Scrambler scramblerLoad = new Scrambler();

    @Before
    public void setUp() throws CryptoException {
        targetContext = InstrumentationRegistry.getTargetContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            scramblerLoad.loadKeyStore();
        }
    }

    @Test(expected = CryptoException.class)
    @SdkSuppress(maxSdkVersion = 17)
    public void testNoSuchAlgorithm() throws CryptoException {
        scramblerLoad.loadKeyStore();
    }

    @Test
    public void decryptEncryptStringTest() throws CryptoException {
        scramblerLoad.generateNewKey("TEST", targetContext);
        Assert.assertEquals("pass",
                scramblerLoad.decryptString("TEST", scramblerLoad.encryptString("TEST", "pass")));
    }

    @Test(expected = CryptoException.class)
    public void decryptEmptyTest() throws CryptoException {
        scramblerLoad.decryptString("", "");
    }

    @Test(expected = CryptoException.class)
    public void encryptEmptyTest() throws CryptoException {
        scramblerLoad.encryptString("", "");
    }

    @Test(expected = CryptoException.class)
    public void generateNewKeyEmptyTest() throws CryptoException {
        scramblerLoad.generateNewKey("", targetContext);
    }
}
