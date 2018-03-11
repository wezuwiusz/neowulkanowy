package io.github.wulkanowy.data.sync.account;

import java.io.IOException;

import io.github.wulkanowy.api.VulcanException;
import io.github.wulkanowy.utils.security.CryptoException;

public interface AccountSyncContract {

    void registerUser(String email, String password, String symbol)
            throws VulcanException, IOException,
            CryptoException;

    void initLastUser() throws VulcanException, IOException,
            CryptoException;
}
