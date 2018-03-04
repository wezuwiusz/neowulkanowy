package io.github.wulkanowy.utils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.data.db.dao.entities.Grade;

public final class EntitiesCompare {

    private EntitiesCompare() {
        throw new IllegalStateException("Utility class");
    }

    public static List<Grade> compareGradeList(List<Grade> newList, List<Grade> oldList) {

        List<Grade> addedOrUpdatedGradeList = new ArrayList<>(CollectionUtils
                .removeAll(newList, oldList));
        List<Grade> updatedList = new ArrayList<>(CollectionUtils
                .removeAll(newList, addedOrUpdatedGradeList));
        List<Grade> lastList = new ArrayList<>();

        for (Grade grade : addedOrUpdatedGradeList) {
            if (!oldList.isEmpty()) {
                grade.setRead(false);
            }
            grade.setIsNew(true);
            updatedList.add(grade);
        }

        for (Grade updateGrade : updatedList) {
            for (Grade oldGrade : oldList) {
                if (updateGrade.equals(oldGrade)) {
                    updateGrade.setRead(oldGrade.getRead());
                }
            }
            lastList.add(updateGrade);
        }
        return lastList;
    }
}
