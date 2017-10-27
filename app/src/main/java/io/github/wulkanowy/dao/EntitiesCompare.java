package io.github.wulkanowy.dao;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.dao.entities.Grade;

public abstract class EntitiesCompare {

    public static List<Grade> compareGradeList(List<Grade> newList, List<Grade> oldList) {

        List<Grade> addedOrUpdatedGradeList = new ArrayList<>(CollectionUtils
                .removeAll(newList, oldList));
        List<Grade> updatedList = new ArrayList<>(CollectionUtils
                .removeAll(newList, addedOrUpdatedGradeList));
        List<Grade> lastList = new ArrayList<>();

        for (Grade grade : addedOrUpdatedGradeList) {
            grade.setIsNew(true);
            if (oldList.size() != 0) {
                grade.setRead(false);
            }
            updatedList.add(grade);
        }

        for (Grade grade : updatedList) {
            for (Grade grade1 : oldList) {
                if (grade.equals(grade1)) {
                    grade.setRead(grade1.getRead());
                }
            }
            lastList.add(grade);
        }
        return lastList;
    }
}
