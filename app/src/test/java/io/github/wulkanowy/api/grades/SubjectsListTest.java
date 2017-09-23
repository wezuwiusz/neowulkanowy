package io.github.wulkanowy.api.grades;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.github.wulkanowy.api.StudentAndParentTestCase;

public class SubjectsListTest extends StudentAndParentTestCase {

    private SubjectsList std;

    private SubjectsList average;

    @Before
    public void setUp() throws Exception {
        std = new SubjectsList(getSnp("OcenyWszystkie-subjects.html"));
        average = new SubjectsList(getSnp("OcenyWszystkie-subjects-average.html"));
    }

    @Test
    public void getAllTest() throws Exception {
        Assert.assertEquals(5, std.getAll().size());
        Assert.assertEquals(5, average.getAll().size());
    }

    @Test
    public void getNameTest() throws Exception {
        List<Subject> stdList = std.getAll();

        Assert.assertEquals("Zachowanie", stdList.get(0).getName());
        Assert.assertEquals("Praktyka zawodowa", stdList.get(1).getName());
        Assert.assertEquals("Metodologia programowania", stdList.get(2).getName());
        Assert.assertEquals("Podstawy przedsiębiorczości", stdList.get(3).getName());
        Assert.assertEquals("Wychowanie do życia w rodzinie", stdList.get(4).getName());

        List<Subject> averageList = average.getAll();
        Assert.assertEquals("Zachowanie", averageList.get(0).getName());
        Assert.assertEquals("Język polski", averageList.get(1).getName());
        Assert.assertEquals("Wychowanie fizyczne", averageList.get(2).getName());
        Assert.assertEquals("Język angielski", averageList.get(3).getName());
        Assert.assertEquals("Wiedza o społeczeństwie", averageList.get(4).getName());
    }

    @Test
    public void getPredictedRatingTest() throws Exception {
        List<Subject> stdList = std.getAll();

        Assert.assertEquals("bardzo dobre", stdList.get(0).getPredictedRating());
        Assert.assertEquals("-", stdList.get(1).getPredictedRating());
        Assert.assertEquals("bardzo dobry", stdList.get(2).getPredictedRating());
        Assert.assertEquals("3/4", stdList.get(3).getPredictedRating());
        Assert.assertEquals("-", stdList.get(4).getPredictedRating());

        List<Subject> averageList = average.getAll();
        Assert.assertEquals("bardzo dobre", averageList.get(0).getPredictedRating());
        Assert.assertEquals("-", averageList.get(1).getPredictedRating());
        Assert.assertEquals("bardzo dobry", averageList.get(2).getPredictedRating());
        Assert.assertEquals("4/5", averageList.get(3).getPredictedRating());
        Assert.assertEquals("-", averageList.get(4).getPredictedRating());
    }

    @Test
    public void getFinalRatingTest() throws Exception {
        List<Subject> stdList = std.getAll();

        Assert.assertEquals("bardzo dobre", stdList.get(0).getFinalRating());
        Assert.assertEquals("celujący", stdList.get(1).getFinalRating());
        Assert.assertEquals("celujący", stdList.get(2).getFinalRating());
        Assert.assertEquals("dostateczny", stdList.get(3).getFinalRating());
        Assert.assertEquals("-", stdList.get(4).getFinalRating());

        List<Subject> averageList = average.getAll();
        Assert.assertEquals("bardzo dobre", averageList.get(0).getFinalRating());
        Assert.assertEquals("dobry", averageList.get(1).getFinalRating());
        Assert.assertEquals("celujący", averageList.get(2).getFinalRating());
        Assert.assertEquals("bardzo dobry", averageList.get(3).getFinalRating());
        Assert.assertEquals("-", averageList.get(4).getFinalRating());
    }
}
