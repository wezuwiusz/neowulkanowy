package io.github.wulkanowy.data.sync.login;

import java.io.IOException;

import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.api.login.VulcanOfflineException;
import io.github.wulkanowy.utils.security.CryptoException;

public interface LoginSyncContract {

    void loginUser(String email, String password, String symbol)
            throws NotLoggedInErrorException, AccountPermissionException, IOException,
            CryptoException, VulcanOfflineException, BadCredentialsException;

    void loginCurrentUser() throws NotLoggedInErrorException, AccountPermissionException, IOException,
            CryptoException, VulcanOfflineException, BadCredentialsException;
}
