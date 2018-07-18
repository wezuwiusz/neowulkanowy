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
import android.util.Base64.DEFAULT
import org.apache.commons.lang3.StringUtils
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*
import javax.crypto.Cipher
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.Cipher.ENCRYPT_MODE
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.security.auth.x500.X500Principal
import kotlin.collections.ArrayList

object Scrambler {

    private const val KEY_ALIAS = "USER_PASSWORD"

    private const val ALGORITHM_RSA = "RSA"

    private const val KEYSTORE_NAME = "AndroidKeyStore"

    private const val KEY_TRANSFORMATION_ALGORITHM = "RSA/ECB/PKCS1Padding"

    private const val KEY_CIPHER_JELLY_PROVIDER = "AndroidOpenSSL"

    private const val KEY_CIPHER_M_PROVIDER = "AndroidKeyStoreBCWorkaround"

    private val KEY_CHARSET = Charset.forName("UTF-8")

    @JvmStatic
    fun encrypt(plainText: String, context: Context): String {
        if (StringUtils.isEmpty(plainText)) {
            throw ScramblerException("Text to be encrypted is empty")
        }

        if (SDK_INT < JELLY_BEAN_MR2) {
            return String(Base64.encode(plainText.toByteArray(KEY_CHARSET), DEFAULT), KEY_CHARSET)
        }

        try {
            if (!isKeyPairExist()) {
                generateKeyPair(context)
            }

            val cipher = getCipher()
            cipher.init(ENCRYPT_MODE, getPublicKey())

            val outputStream = ByteArrayOutputStream()
            val cipherOutputStream = CipherOutputStream(outputStream, cipher)
            cipherOutputStream.write(plainText.toByteArray(KEY_CHARSET))
            cipherOutputStream.close()

            return Base64.encodeToString(outputStream.toByteArray(), DEFAULT)
        } catch (e: Exception) {
            throw ScramblerException("An error occurred while encrypting text", e)
        }

    }

    @JvmStatic
    fun decrypt(cipherText: String): String {
        if (StringUtils.isEmpty(cipherText)) {
            throw ScramblerException("Text to be encrypted is empty")
        }

        if (SDK_INT < JELLY_BEAN_MR2) {
            return String(Base64.decode(cipherText.toByteArray(KEY_CHARSET), DEFAULT), KEY_CHARSET)
        }

        if (!isKeyPairExist()) {
            throw ScramblerException("KeyPair doesn't exist")
        }

        try {
            val cipher = getCipher()
            cipher.init(DECRYPT_MODE, getPrivateKey())

            val input = CipherInputStream(ByteArrayInputStream(Base64.decode(cipherText, DEFAULT)), cipher)
            val values = ArrayList<Byte>()

            var nextByte = 0
            while ({ nextByte = input.read(); nextByte }() != -1) {
                values.add(nextByte.toByte())
            }

            val bytes = ByteArray(values.size)
            for (i in bytes.indices) {
                bytes[i] = values[i]
            }
            return String(bytes, 0, bytes.size, KEY_CHARSET)
        } catch (e: Exception) {
            throw ScramblerException("An error occurred while decrypting text", e)
        }

    }

    private fun getKeyStoreInstance(): KeyStore {
        val keyStore = KeyStore.getInstance(KEYSTORE_NAME)
        keyStore.load(null)
        return keyStore
    }

    private fun getPublicKey(): PublicKey =
            (getKeyStoreInstance().getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry)
                    .certificate.publicKey


    private fun getPrivateKey(): PrivateKey =
            (getKeyStoreInstance().getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry).privateKey


    private fun getCipher(): Cipher {
        if (SDK_INT >= M) {
            return Cipher.getInstance(KEY_TRANSFORMATION_ALGORITHM, KEY_CIPHER_M_PROVIDER)
        }

        return Cipher.getInstance(KEY_TRANSFORMATION_ALGORITHM, KEY_CIPHER_JELLY_PROVIDER)
    }

    @TargetApi(JELLY_BEAN_MR2)
    private fun generateKeyPair(context: Context) {
        val spec = if (SDK_INT >= M) {
            KeyGenParameterSpec.Builder(KEY_ALIAS, PURPOSE_DECRYPT or PURPOSE_ENCRYPT)
                    .setDigests(DIGEST_SHA256, DIGEST_SHA512)
                    .setCertificateSubject(X500Principal("CN=Wulkanowy"))
                    .setEncryptionPaddings(ENCRYPTION_PADDING_RSA_PKCS1)
                    .setSignaturePaddings(SIGNATURE_PADDING_RSA_PKCS1)
                    .setCertificateSerialNumber(BigInteger.TEN)
                    .build()
        } else {
            val start = Calendar.getInstance()
            val end = Calendar.getInstance()
            end.add(Calendar.YEAR, 99)

            KeyPairGeneratorSpec.Builder(context)
                    .setAlias(KEY_ALIAS)
                    .setSubject(X500Principal("CN=Wulkanowy"))
                    .setSerialNumber(BigInteger.TEN)
                    .setStartDate(start.time)
                    .setEndDate(end.time)
                    .build()
        }

        val generator = KeyPairGenerator.getInstance(ALGORITHM_RSA, KEYSTORE_NAME)
        generator.initialize(spec)
        generator.generateKeyPair()

        Timber.i("A new KeyPair has been generated")
    }

    private fun isKeyPairExist(): Boolean = getKeyStoreInstance().getKey(KEY_ALIAS, null) != null
}
