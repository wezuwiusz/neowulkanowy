package io.github.wulkanowy.utils.security;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

import timber.log.Timber;

public final class Scrambler {

    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";

    private static KeyStore keyStore;

    private Scrambler() {
        throw new IllegalStateException("Utility class");
    }

    public static String encrypt(String email, String plainText, Context context)
            throws CryptoException {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            loadKeyStore();
            generateNewKey(email, context);
            return encryptString(email, plainText);
        }
        return new String(Base64.encode(plainText.getBytes(), Base64.DEFAULT));
    }

    public static String decrypt(String email, String encryptedText) throws CryptoException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            loadKeyStore();
            return decryptString(email, encryptedText);
        }
        return new String(Base64.decode(encryptedText, Base64.DEFAULT));
    }

    private static void loadKeyStore() throws CryptoException {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
        } catch (Exception e) {
            throw new CryptoException(e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    @TargetApi(18)
    private static void generateNewKey(String alias, Context context) throws CryptoException {

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        AlgorithmParameterSpec spec;

        end.add(Calendar.YEAR, 10);
        if (!"".equals(alias)) {
            try {
                if (!keyStore.containsAlias(alias)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        spec = new KeyGenParameterSpec.Builder(alias,
                                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                                .setDigests(KeyProperties.DIGEST_SHA256)
                                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                                .setCertificateNotBefore(start.getTime())
                                .setCertificateNotAfter(end.getTime())
                                .build();

                    } else {
                        spec = new KeyPairGeneratorSpec.Builder(context)
                                .setAlias(alias)
                                .setSubject(new X500Principal("CN=" + alias))
                                .setSerialNumber(BigInteger.TEN)
                                .setStartDate(start.getTime())
                                .setEndDate(end.getTime())
                                .build();
                    }

                    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA",
                            ANDROID_KEYSTORE);
                    keyPairGenerator.initialize(spec);
                    keyPairGenerator.generateKeyPair();
                }
            } catch (Exception e) {
                throw new CryptoException(e.getMessage());
            }
        } else {
            throw new CryptoException("GenerateNewKey - String is empty");
        }

        Timber.d("Key pair are create");

    }

    private static String encryptString(String alias, String text) throws CryptoException {

        if (!alias.isEmpty() && !text.isEmpty()) {
            try {
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
                RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

                Cipher input = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                input.init(Cipher.ENCRYPT_MODE, publicKey);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                CipherOutputStream cipherOutputStream = new CipherOutputStream(
                        outputStream, input);
                cipherOutputStream.write(text.getBytes("UTF-8"));
                cipherOutputStream.close();

                byte[] values = outputStream.toByteArray();

                return Base64.encodeToString(values, Base64.DEFAULT);

            } catch (Exception e) {
                throw new CryptoException(e.getMessage());
            }
        } else {
            throw new CryptoException("EncryptString - String is empty");
        }
    }

    private static String decryptString(String alias, String text) throws CryptoException {

        if (!alias.isEmpty() && !text.isEmpty()) {
            try {
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);

                Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());

                CipherInputStream cipherInputStream = new CipherInputStream(
                        new ByteArrayInputStream(Base64.decode(text, Base64.DEFAULT)), output);

                ArrayList<Byte> values = new ArrayList<>();

                int nextByte;

                while ((nextByte = cipherInputStream.read()) != -1) {
                    values.add((byte) nextByte);
                }

                Byte[] bytes = values.toArray(new Byte[values.size()]);

                return new String(ArrayUtils.toPrimitive(bytes), 0, bytes.length, "UTF-8");
            } catch (Exception e) {
                throw new CryptoException(e.getMessage());
            }
        } else {
            throw new CryptoException("EncryptString - String is empty");
        }
    }
}
