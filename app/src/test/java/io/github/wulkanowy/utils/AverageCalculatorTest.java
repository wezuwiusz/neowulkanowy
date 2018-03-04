package io.github.wulkanowy.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.data.db.dao.entities.Grade;

public class AverageCalculatorTest {

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

        Assert.assertEquals(4.8f, AverageCalculator.calculate(gradeList), 0.0f);
        Assert.assertEquals(4.8f, AverageCalculator.calculate(gradeList1), 0.0f);
    }

    @Test
    public void errorAverageTest() {
        List<Grade> gradeList = new ArrayList<>();
        gradeList.add(new Grade().setValue("np.").setWeight("1,00"));

        Assert.assertEquals(-1f, AverageCalculator.calculate(gradeList), 0.0f);
    }
}
