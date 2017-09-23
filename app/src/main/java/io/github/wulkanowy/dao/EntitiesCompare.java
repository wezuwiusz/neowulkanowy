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

        for (Grade grade : addedOrUpdatedGradeList) {
            grade.setIsNew(true);
            updatedList.add(grade);
        }

        return updatedList;
    }
}
