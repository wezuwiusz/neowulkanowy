package io.github.wulkanowy.dao.entities;

import org.junit.Assert;
import org.junit.Test;

import io.github.wulkanowy.R;

public class GradeTest {

    @Test
    public void getValueColorTest() {
        Assert.assertEquals(R.color.six_grade, new Grade().setValue("-6").getValueColor());
        Assert.assertEquals(R.color.five_grade, new Grade().setValue("--5").getValueColor());
        Assert.assertEquals(R.color.four_grade, new Grade().setValue("=4").getValueColor());
        Assert.assertEquals(R.color.three_grade, new Grade().setValue("3-").getValueColor());
        Assert.assertEquals(R.color.two_grade, new Grade().setValue("2--").getValueColor());
        Assert.assertEquals(R.color.two_grade, new Grade().setValue("2=").getValueColor());
        Assert.assertEquals(R.color.one_grade, new Grade().setValue("1+").getValueColor());
        Assert.assertEquals(R.color.one_grade, new Grade().setValue("+1").getValueColor());
        Assert.assertEquals(R.color.default_grade, new Grade().setValue("Np").getValueColor());
        Assert.assertEquals(R.color.default_grade, new Grade().setValue("").getValueColor());
    }

    @Test
    public void equalsTest() {
        Assert.assertTrue(new Grade().setSubject("Religia").setValue("5")
                .equals(new Grade().setSubject("Religia").setValue("5")));

        Assert.assertFalse(new Grade().setSubject("Religia").setValue("4")
                .equals(new Grade().setSubject("Religia").setValue("5")));

        Assert.assertEquals(new Grade().setSubject("Religia").setValue("5").hashCode(),
                new Grade().setSubject("Religia").setValue("5").hashCode());
    }
}
