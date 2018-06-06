package io.github.wulkanowy.utils;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import timber.log.Timber;

public final class LoggerUtils {

    public static class CrashlyticsTree extends Timber.Tree {

        @Override
        protected void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable t) {
            Crashlytics.setInt("priority", priority);
            Crashlytics.setString("tag", tag);

            if (t == null) {
                Crashlytics.log(message);
            } else {
                Crashlytics.setString("message", message);
                Crashlytics.logException(t);
            }
        }
    }

    public static class DebugLogTree extends Timber.DebugTree {

        @Override
        protected void log(int priority, String tag, @NonNull String message, Throwable t) {
            if ("HUAWEI".equals(Build.MANUFACTURER) || "samsung".equals(Build.MANUFACTURER)) {
                if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
                    priority = Log.ERROR;
                }
            }
            super.log(priority, AppConstant.APP_NAME, message, t);
        }

        @Override
        protected String createStackElementTag(@NonNull StackTraceElement element) {
            return super.createStackElementTag(element) + " - " + element.getLineNumber();
        }
    }
}
