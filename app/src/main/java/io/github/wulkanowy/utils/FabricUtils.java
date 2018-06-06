package io.github.wulkanowy.utils;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.LoginEvent;
import com.crashlytics.android.answers.SignUpEvent;

public final class FabricUtils {

    private FabricUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void logLogin(String method, boolean result) {
        Answers.getInstance().logLogin(new LoginEvent()
                .putMethod(method)
                .putSuccess(result)
        );
    }

    public static void logRegister(boolean result, String symbol, String message) {
        Answers.getInstance().logSignUp(new SignUpEvent()
                .putMethod("Login activity")
                .putSuccess(result)
                .putCustomAttribute("symbol", symbol)
                .putCustomAttribute("message", message)
        );
    }

    public static void logRefresh(String name, boolean result, String date) {
        Answers.getInstance().logCustom(
                new CustomEvent(name + " refresh")
                        .putCustomAttribute("Success", result ? "true" : "false")
                        .putCustomAttribute("Date", date)
        );
    }
}
