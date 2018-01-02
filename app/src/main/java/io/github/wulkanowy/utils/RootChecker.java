package io.github.wulkanowy.utils;

import android.os.Build;

import java.io.File;

public final class RootChecker {

    private RootChecker() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isRooted() {
        return checkOne() || checkTwo() || checkThree();
    }

    private static boolean checkOne() {
        return Build.TAGS != null && Build.TAGS.contains("test-keys");
    }

    private static boolean checkTwo() {
        return new File("/system/app/Superuser.apk").exists();
    }

    private static boolean checkThree() {
        String[] commands = {"/system/xbin/which su", "/system/bin/which su", "which su"};
        for (String command : commands) {
            try {
                Runtime.getRuntime().exec(command);
                return true;
            } catch (Exception e) {
                // ignore
            }
        }
        return false;
    }

}
