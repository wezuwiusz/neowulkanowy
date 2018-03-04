package io.github.wulkanowy.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.data.db.dao.entities.Grade;


public class EntitiesCompareTest {

    private List<Grade> newList = new ArrayList<>();

    private List<Grade> oldList = new ArrayList<>();

    private Grade grade1;

    private Grade grade2;

    @Before
    public void prepareObjects() {
        grade1 = new Grade()
                .setSubject("Matematyka")
                .setValue("6")
                .setColor("FFFFFF")
                .setSymbol("S")
                .setDescription("Lorem ipsum")
                .setWeight("10")
                .setDate("01.01.2017")
                .setTeacher("Andrzej")
                .setSemester("777");

        grade2 = new Grade()
                .setSubject("Religia")
                .setValue("6")
                .setColor("FFFFFF")
                .setSymbol("S")
                .setDescription("Wolna wola")
                .setWeight("10")
                .setDate("01.01.2017")
                .setTeacher("Andrzej")
                .setSemester("777");
    }

    @Test
    public void testCompareNewGradeEmptyOldList() {

        newList.add(grade1);

        List<Grade> updatedList = EntitiesCompare.compareGradeList(newList, oldList);

        Assert.assertTrue(updatedList.get(0).getRead());
        Assert.assertTrue(updatedList.get(0).getIsNew());

    }

    @Test
    public void testCompareNewGradePositive() {
        newList.add(grade1);
        newList.add(grade2);
        oldList.add(grade2);

        List<Grade> updatedList = EntitiesCompare.compareGradeList(newList, oldList);

        Assert.assertFalse(updatedList.get(0).getIsNew());
        Assert.assertTrue(updatedList.get(1).getIsNew());
    }

    @Test
    public void testCompareNewGradeNegative() {

        newList.add(grade1);
        newList.add(grade1);
        oldList.add(grade1);
        oldList.add(grade2);

        List<Grade> updatedList = EntitiesCompare.compareGradeList(newList, oldList);

        Assert.assertFalse(updatedList.get(0).getIsNew());
        Assert.assertFalse(updatedList.get(1).getIsNew());
    }

    @Test
    public void testCompareEmptyGradeList() {

        List<Grade> updatedList = EntitiesCompare.compareGradeList(newList, oldList);

        Assert.assertEquals(new ArrayList<>(), updatedList);
    }

    @Test
    public void testCompareReadGradeTest() {
        newList.add(grade1);
        newList.add(grade2);
        oldList.add(grade2.setRead(true));

        List<Grade> updatedList = EntitiesCompare.compareGradeList(newList, oldList);

        Assert.assertTrue(updatedList.get(0).getRead());
        Assert.assertFalse(updatedList.get(0).getIsNew());
        Assert.assertFalse(updatedList.get(1).getRead());
        Assert.assertTrue(updatedList.get(1).getIsNew());
    }
}
