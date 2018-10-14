package io.github.wulkanowy.utils.security

import android.support.test.InstrumentationRegistry
import android.support.test.filters.SdkSuppress
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import java.security.KeyStore
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


@SmallTest
@RunWith(AndroidJUnit4::class)
class ScramblerTest {

    @Test
    fun encryptDecryptTest() {
        assertEquals("TEST", decrypt(encrypt("TEST",
                InstrumentationRegistry.getTargetContext())))
    }

    @Test
    fun emptyTextEncryptTest() {
        assertFailsWith<ScramblerException> {
            decrypt("")
        }

        assertFailsWith<ScramblerException> {
            encrypt("", InstrumentationRegistry.getTargetContext())
        }
    }

    @Test
    @SdkSuppress(minSdkVersion = 18)
    fun emptyKeyStoreTest() {
        val text = encrypt("test", InstrumentationRegistry.getTargetContext())

        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        keyStore.deleteEntry("USER_PASSWORD")

        assertFailsWith<ScramblerException> {
            decrypt(text)
        }
    }
}
