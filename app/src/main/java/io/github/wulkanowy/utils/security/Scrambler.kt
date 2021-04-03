@file:Suppress("DEPRECATION")

package io.github.wulkanowy.utils.security

import android.annotation.TargetApi
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.JELLY_BEAN_MR2
import android.os.Build.VERSION_CODES.M
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.DIGEST_SHA256
import android.security.keystore.KeyProperties.DIGEST_SHA512
import android.security.keystore.KeyProperties.ENCRYPTION_PADDING_RSA_OAEP
import android.security.keystore.KeyProperties.PURPOSE_DECRYPT
import android.security.keystore.KeyProperties.PURPOSE_ENCRYPT
import android.util.Base64.DEFAULT
import android.util.Base64.decode
import android.util.Base64.encode
import android.util.Base64.encodeToString
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.spec.MGF1ParameterSpec.SHA1
import java.util.Calendar
import java.util.Calendar.YEAR
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource.PSpecified
import javax.security.auth.x500.X500Principal

private const val KEYSTORE_NAME = "AndroidKeyStore"

private const val KEY_ALIAS = "wulkanowy_password"

private val KEY_CHARSET = Charset.forName("UTF-8")

private val isKeyPairExists: Boolean
    get() = keyStore.getKey(KEY_ALIAS, null) != null

private val keyStore: KeyStore
    get() = KeyStore.getInstance(KEYSTORE_NAME).apply { load(null) }

private val cipher: Cipher
    get() {
        return if (SDK_INT >= M) Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "AndroidKeyStoreBCWorkaround")
        else Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL")
    }

fun encrypt(plainText: String, context: Context): String {
    if (plainText.isEmpty()) throw ScramblerException("Text to be encrypted is empty")

    return try {
        if (!isKeyPairExists) generateKeyPair(context)

        cipher.let {
            if (SDK_INT >= M) {
                OAEPParameterSpec("SHA-256", "MGF1", SHA1, PSpecified.DEFAULT).let { spec ->
                    it.init(ENCRYPT_MODE, keyStore.getCertificate(KEY_ALIAS).publicKey, spec)
                }
            } else it.init(ENCRYPT_MODE, keyStore.getCertificate(KEY_ALIAS).publicKey)

            ByteArrayOutputStream().let { output ->
                CipherOutputStream(output, it).apply {
                    write(plainText.toByteArray(KEY_CHARSET))
                    close()
                }
                encodeToString(output.toByteArray(), DEFAULT)
            }
        }
    } catch (exception: Exception) {
        Timber.e(exception, "An error occurred while encrypting text")
        String(encode(plainText.toByteArray(KEY_CHARSET), DEFAULT), KEY_CHARSET)
    }
}

fun decrypt(cipherText: String): String {
    if (cipherText.isEmpty()) throw ScramblerException("Text to be encrypted is empty")

    return try {
        if (!isKeyPairExists) throw ScramblerException("KeyPair doesn't exist")

        cipher.let {
            if (SDK_INT >= M) {
                OAEPParameterSpec("SHA-256", "MGF1", SHA1, PSpecified.DEFAULT).let { spec ->
                    it.init(DECRYPT_MODE, keyStore.getKey(KEY_ALIAS, null), spec)
                }
            } else it.init(DECRYPT_MODE, keyStore.getKey(KEY_ALIAS, null))

            CipherInputStream(ByteArrayInputStream(decode(cipherText, DEFAULT)), it).let { input ->
                val values = ArrayList<Byte>()
                var nextByte = 0
                while ({ nextByte = input.read(); nextByte }() != -1) {
                    values.add(nextByte.toByte())
                }
                val bytes = ByteArray(values.size)
                for (i in bytes.indices) {
                    bytes[i] = values[i]
                }
                String(bytes, 0, bytes.size, KEY_CHARSET)
            }
        }
    } catch (e: Exception) {
        throw ScramblerException("An error occurred while decrypting text", e)
    }
}

@TargetApi(JELLY_BEAN_MR2)
private fun generateKeyPair(context: Context) {
    (if (SDK_INT >= M) {
        KeyGenParameterSpec.Builder(KEY_ALIAS, PURPOSE_DECRYPT or PURPOSE_ENCRYPT)
            .setDigests(DIGEST_SHA256, DIGEST_SHA512)
            .setEncryptionPaddings(ENCRYPTION_PADDING_RSA_OAEP)
            .setCertificateSerialNumber(BigInteger.TEN)
            .setCertificateSubject(X500Principal("CN=Wulkanowy"))
            .build()
    } else {
        KeyPairGeneratorSpec.Builder(context)
            .setAlias(KEY_ALIAS)
            .setSubject(X500Principal("CN=Wulkanowy"))
            .setSerialNumber(BigInteger.TEN)
            .setStartDate(Calendar.getInstance().time)
            .setEndDate(Calendar.getInstance().apply { add(YEAR, 99) }.time)
            .build()
    }).let {
        KeyPairGenerator.getInstance("RSA", KEYSTORE_NAME).apply {
            initialize(it)
            genKeyPair()
        }
    }
    Timber.i("A new KeyPair has been generated")
}
