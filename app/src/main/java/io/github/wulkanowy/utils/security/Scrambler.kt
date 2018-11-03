@file:Suppress("DEPRECATION")

package io.github.wulkanowy.utils.security

import android.annotation.TargetApi
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.JELLY_BEAN_MR2
import android.os.Build.VERSION_CODES.M
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.*
import android.util.Base64
import android.util.Base64.*
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.util.*
import java.util.Calendar.YEAR
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.security.auth.x500.X500Principal
import kotlin.collections.ArrayList

private const val KEY_ALIAS = "USER_PASSWORD"

private const val ALGORITHM_RSA = "RSA"

private const val KEYSTORE_NAME = "AndroidKeyStore"

private const val KEY_TRANSFORMATION_ALGORITHM = "RSA/ECB/PKCS1Padding"

private const val KEY_CIPHER_JELLY_PROVIDER = "AndroidOpenSSL"

private const val KEY_CIPHER_M_PROVIDER = "AndroidKeyStoreBCWorkaround"

private val KEY_CHARSET = Charset.forName("UTF-8")

private val isKeyPairExists: Boolean
    get() = keyStore.getKey(KEY_ALIAS, null) != null

private val cipher: Cipher
    get() {
        return if (SDK_INT >= M) Cipher.getInstance(KEY_TRANSFORMATION_ALGORITHM, KEY_CIPHER_M_PROVIDER)
        else Cipher.getInstance(KEY_TRANSFORMATION_ALGORITHM, KEY_CIPHER_JELLY_PROVIDER)
    }

private val keyStore: KeyStore
    get() = KeyStore.getInstance(KEYSTORE_NAME).apply { load(null) }

fun encrypt(plainText: String, context: Context): String {
    if (plainText.isEmpty()) throw ScramblerException("Text to be encrypted is empty")

    if (SDK_INT < JELLY_BEAN_MR2) {
        return String(Base64.encode(plainText.toByteArray(KEY_CHARSET), DEFAULT), KEY_CHARSET)
    }

    return try {
        if (!isKeyPairExists) generateKeyPair(context)
        cipher.let {
            it.init(ENCRYPT_MODE, keyStore.getCertificate(KEY_ALIAS).publicKey)

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

    if (SDK_INT < JELLY_BEAN_MR2 || cipherText.length < 250) {
        return String(decode(cipherText.toByteArray(KEY_CHARSET), DEFAULT), KEY_CHARSET)
    }

    if (!isKeyPairExists) throw ScramblerException("KeyPair doesn't exist")

    return try {
        cipher.let {
            it.init(DECRYPT_MODE, (keyStore.getKey(KEY_ALIAS, null) as PrivateKey))

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
                .setCertificateSubject(X500Principal("CN=Wulkanowy"))
                .setEncryptionPaddings(ENCRYPTION_PADDING_RSA_PKCS1)
                .setSignaturePaddings(SIGNATURE_PADDING_RSA_PKCS1)
                .setCertificateSerialNumber(BigInteger.TEN)
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
        KeyPairGenerator.getInstance(ALGORITHM_RSA, KEYSTORE_NAME).apply {
            initialize(it)
            genKeyPair()
        }
    }
    Timber.i("A new KeyPair has been generated")
}
