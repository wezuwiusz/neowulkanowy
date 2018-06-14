package io.github.wulkanowy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.customtabs.CustomTabsIntent;

import io.github.wulkanowy.R;

public final class CommonUtils {

    private CommonUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void openInternalBrowserViewer(Activity activity, String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(activity.getResources().getColor(R.color.colorPrimary));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(activity, Uri.parse(url));
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

    @ColorInt
    public static int getThemeAttrColor(Context context, @AttrRes int colorAttr) {
        final TypedArray array = context.obtainStyledAttributes(null, new int[]{colorAttr});
        try {
            return array.getColor(0, 0);
        } finally {
            array.recycle();
        }
    }
}
