package io.github.wulkanowy.utils.security

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.filters.SmallTest
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
                ApplicationProvider.getApplicationContext())))
    }

    @Test
    fun emptyTextEncryptTest() {
        assertFailsWith<ScramblerException> {
            decrypt("")
        }

        assertFailsWith<ScramblerException> {
            encrypt("", ApplicationProvider.getApplicationContext())
        }
    }

    @Test
    @SdkSuppress(minSdkVersion = 18)
    fun emptyKeyStoreTest() {
        val text = encrypt("test", ApplicationProvider.getApplicationContext())

        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        keyStore.deleteEntry("wulkanowy_password")

        assertFailsWith<ScramblerException> {
            decrypt(text)
        }
    }
}
