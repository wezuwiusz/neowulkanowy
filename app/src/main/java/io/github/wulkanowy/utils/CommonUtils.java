package io.github.wulkanowy.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;

import io.github.wulkanowy.R;

public final class CommonUtils {

    private CommonUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static int colorHexToColorName(String hexColor) {
        switch (hexColor) {
            case "000000":
                return R.string.all_black;

            case "F04C4C":
                return R.string.all_red;

            case "20A4F7":
                return R.string.all_blue;

            case "6ECD07":
                return R.string.all_green;

            default:
                return R.string.all_empty_color;
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
