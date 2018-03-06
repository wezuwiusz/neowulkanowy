package io.github.wulkanowy.utils;

import android.util.Log;

public final class LogUtils {

    private LogUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void debug(String message) {
        Log.d(AppConstant.APP_NAME, message);
    }

    public static void error(String message, Throwable throwable) {
        Log.e(AppConstant.APP_NAME, message, throwable);
    }

    public static void error(String message) {
        Log.e(AppConstant.APP_NAME, message);
    }

    public static void info(String message) {
        Log.i(AppConstant.APP_NAME, message);
    }
}
