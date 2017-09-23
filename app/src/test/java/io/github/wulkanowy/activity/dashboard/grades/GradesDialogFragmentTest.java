package io.github.wulkanowy.activity.dashboard.grades;


import org.junit.Assert;
import org.junit.Test;

import io.github.wulkanowy.R;

import static io.github.wulkanowy.activity.dashboard.grades.GradesDialogFragment.colorHexToColorName;

public class GradesDialogFragmentTest {

    @Test
    public void colorHexToColorNameTest() {
        Assert.assertEquals(R.string.color_black_text, colorHexToColorName("000000"));
        Assert.assertEquals(R.string.color_red_text, colorHexToColorName("F04C4C"));
        Assert.assertEquals(R.string.color_blue_text, colorHexToColorName("20A4F7"));
        Assert.assertEquals(R.string.color_green_text, colorHexToColorName("6ECD07"));
        Assert.assertEquals(R.string.noColor_text, colorHexToColorName(""));

    }
}
