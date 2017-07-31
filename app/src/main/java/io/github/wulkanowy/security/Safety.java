package io.github.wulkanowy.security;

import android.content.Context;
import android.os.Build;
import android.util.Base64;

import io.github.wulkanowy.utilities.RootUtilities;

public class Safety extends Scrambler {

    public Safety(Context context) {
        super(context);
    }

    public String encrypt(String email, String plainText) throws CryptoException, UnsupportedOperationException {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {

            loadKeyStore();
            generateNewKey(email);
            return encryptString(email, plainText);
        } else {

            if (!RootUtilities.isRooted()) {
                return new String(Base64.encode(plainText.getBytes(), Base64.DEFAULT));
            } else {
                throw new UnsupportedOperationException("Password store in this devices isn't safe because is rooted");
            }
        }
    }

    public String decrypt(String email, String encryptedText) throws CryptoException {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {

            loadKeyStore();
            return decryptString(email, encryptedText);
        } else {
            return new String(Base64.decode(encryptedText, Base64.DEFAULT));
        }

    }
}
