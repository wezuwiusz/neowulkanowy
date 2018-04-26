package io.github.wulkanowy.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.R;
import io.github.wulkanowy.data.db.dao.entities.Grade;

public class GradeUtilsTest {

    @Test
    public void averageTest() {
        List<Grade> gradeList = new ArrayList<>();
        gradeList.add(new Grade().setValue("np.").setWeight("1,00"));
        gradeList.add(new Grade().setValue("-5").setWeight("10,00"));
        gradeList.add(new Grade().setValue("--5").setWeight("10,00"));
        gradeList.add(new Grade().setValue("=5").setWeight("10,00"));
        gradeList.add(new Grade().setValue("+5").setWeight("10,00"));
        gradeList.add(new Grade().setValue("5").setWeight("10,00"));

        List<Grade> gradeList1 = new ArrayList<>();
        gradeList1.add(new Grade().setValue("np.").setWeight("1,00"));
        gradeList1.add(new Grade().setValue("5-").setWeight("10,00"));
        gradeList1.add(new Grade().setValue("5--").setWeight("10,00"));
        gradeList1.add(new Grade().setValue("5=").setWeight("10,00"));
        gradeList1.add(new Grade().setValue("5+").setWeight("10,00"));
        gradeList1.add(new Grade().setValue("5").setWeight("10,00"));

        Assert.assertEquals(4.8f, GradeUtils.calculate(gradeList), 0.0f);
        Assert.assertEquals(4.8f, GradeUtils.calculate(gradeList1), 0.0f);
    }

    @Test
    public void errorAverageTest() {
        List<Grade> gradeList = new ArrayList<>();
        gradeList.add(new Grade().setValue("np.").setWeight("1,00"));

        Assert.assertEquals(-1f, GradeUtils.calculate(gradeList), 0.0f);
    }

    @Test
    public void getValueColor() {
        Assert.assertEquals(R.color.six_grade, GradeUtils.getValueColor("-6"));
        Assert.assertEquals(R.color.five_grade, GradeUtils.getValueColor("--5"));
        Assert.assertEquals(R.color.four_grade, GradeUtils.getValueColor("=4"));
        Assert.assertEquals(R.color.three_grade, GradeUtils.getValueColor("3-"));
        Assert.assertEquals(R.color.two_grade, GradeUtils.getValueColor("2--"));
        Assert.assertEquals(R.color.two_grade, GradeUtils.getValueColor("2="));
        Assert.assertEquals(R.color.one_grade, GradeUtils.getValueColor("1+"));
        Assert.assertEquals(R.color.one_grade, GradeUtils.getValueColor("+1"));
        Assert.assertEquals(R.color.default_grade, GradeUtils.getValueColor("6 (.XI)"));
        Assert.assertEquals(R.color.default_grade, GradeUtils.getValueColor("Np"));
        Assert.assertEquals(R.color.default_grade, GradeUtils.getValueColor("7"));
        Assert.assertEquals(R.color.default_grade, GradeUtils.getValueColor(""));
    }
}
