package io.github.wulkanowy.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.R;
import io.github.wulkanowy.data.db.dao.entities.Grade;
import io.github.wulkanowy.data.db.dao.entities.Subject;

import static org.junit.Assert.assertEquals;

public class GradeUtilsTest {

    @Test
    public void weightedAverageTest() {
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

        assertEquals(4.8f, GradeUtils.calculateWeightedAverage(gradeList), 0.0f);
        assertEquals(4.8f, GradeUtils.calculateWeightedAverage(gradeList1), 0.0f);
    }

    @Test
    public void subjectsAverageTest() {
        List<Subject> subjectList = new ArrayList<>();
        subjectList.add(new Subject().setPredictedRating("2").setFinalRating("3"));
        subjectList.add(new Subject().setPredictedRating("niedostateczny").setFinalRating("dopuszczający"));
        subjectList.add(new Subject().setPredictedRating("dostateczny").setFinalRating("dobry"));
        subjectList.add(new Subject().setPredictedRating("bardzo dobry").setFinalRating("celujący"));
        subjectList.add(new Subject().setPredictedRating("2/3").setFinalRating("-4"));

        assertEquals(3.8f, GradeUtils.calculateSubjectsAverage(subjectList, false), 0.0f);
        assertEquals(2.75f, GradeUtils.calculateSubjectsAverage(subjectList, true), 0.0f);
    }

    @Test
    public void abnormalAverageTest() {
        List<Grade> gradeList = new ArrayList<>();
        gradeList.add(new Grade().setValue("np.").setWeight("1,00"));

        List<Subject> subjectList = new ArrayList<>();
        subjectList.add(new Subject().setFinalRating("nieklasyfikowany"));

        assertEquals(-1f, GradeUtils.calculateWeightedAverage(gradeList), 0.0f);
        assertEquals(-1f, GradeUtils.calculateSubjectsAverage(subjectList, false), 0.0f);
    }

    @Test
    public void getValueColorTest() {
        assertEquals(R.color.grade_six, GradeUtils.getValueColor("-6"));
        assertEquals(R.color.grade_five, GradeUtils.getValueColor("--5"));
        assertEquals(R.color.grade_four, GradeUtils.getValueColor("=4"));
        assertEquals(R.color.grade_three, GradeUtils.getValueColor("3-"));
        assertEquals(R.color.grade_two, GradeUtils.getValueColor("2--"));
        assertEquals(R.color.grade_two, GradeUtils.getValueColor("2="));
        assertEquals(R.color.grade_one, GradeUtils.getValueColor("1+"));
        assertEquals(R.color.grade_one, GradeUtils.getValueColor("+1"));
        assertEquals(R.color.grade_default, GradeUtils.getValueColor("6 (.XI)"));
        assertEquals(R.color.grade_default, GradeUtils.getValueColor("Np"));
        assertEquals(R.color.grade_default, GradeUtils.getValueColor("7"));
        assertEquals(R.color.grade_default, GradeUtils.getValueColor(""));
    }

    @Test
    public void getShortGradeValueTest() {
        assertEquals("6", GradeUtils.getShortGradeValue("celujący"));
        assertEquals("1", GradeUtils.getShortGradeValue("niedostateczny"));
        assertEquals("wzorowe", GradeUtils.getShortGradeValue("wzorowe"));
    }
}
