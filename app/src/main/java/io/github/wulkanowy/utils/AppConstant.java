package io.github.wulkanowy.utils;

public final class AppConstant {

    public static final String APP_NAME = "Wulkanowy";

    public static final String DATABASE_NAME = "wulkanowy_db";

    public static final String SHARED_PREFERENCES_NAME = "user_data";


    public static final String VULCAN_CREATE_ACCOUNT_URL =
            "https://cufs.vulcan.net.pl/Default/AccountManage/CreateAccount";

    public static final String VULCAN_FORGOT_PASS_URL =
            "https://cufs.vulcan.net.pl/Default/AccountManage/UnlockAccount";

    public static final String DEFAULT_SYMBOL = "Default";

    public static final String DATE_PATTERN = "yyyy-MM-dd";

    public static final String REPO_URL = "https://github.com/wulkanowy/wulkanowy";

    private AppConstant() {
        throw new IllegalStateException("Utility class");
    }
}
