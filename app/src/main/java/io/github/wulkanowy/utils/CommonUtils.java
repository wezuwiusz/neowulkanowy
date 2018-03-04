package io.github.wulkanowy.utils;

import android.content.Context;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

import io.github.wulkanowy.R;

public final class CommonUtils {

    private CommonUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void openInternalBrowserViewer(Context context, String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(context.getResources().getColor(R.color.colorPrimary));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }

    public static int colorHexToColorName(String hexColor) {
        switch (hexColor) {
            case "000000":
                return R.string.color_black_text;

            case "F04C4C":
                return R.string.color_red_text;

            case "20A4F7":
                return R.string.color_blue_text;

            case "6ECD07":
                return R.string.color_green_text;

            default:
                return R.string.noColor_text;
        }
    }
}
