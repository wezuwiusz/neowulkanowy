package io.github.wulkanowy.database;


import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.grades.Grade;

public class DatabaseComparer {

    public static List<Grade> compareGradesLists(List<Grade> newList, List<Grade> oldList) {

        List<Grade> addedOrUpdatedGradesList = new ArrayList<>(CollectionUtils.removeAll(newList, oldList));
        List<Grade> updatedList = new ArrayList<>(CollectionUtils.removeAll(newList, addedOrUpdatedGradesList));

        for (Grade grade : addedOrUpdatedGradesList) {
            grade.setIsNew(true);
            updatedList.add(grade);
        }

        return updatedList;
    }
}
