package io.github.wulkanowy.utilities;


import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.dao.entities.Grade;
import io.github.wulkanowy.dao.entities.Subject;

public class ConversionVulcanObject {

    private ConversionVulcanObject() {
        throw new IllegalStateException("Utility class");
    }

    public static List<Subject> subjectsToSubjectEntities(List<io.github.wulkanowy.api.grades.Subject> subjectList) {

        List<Subject> subjectEntityList = new ArrayList<>();

        for (io.github.wulkanowy.api.grades.Subject subject : subjectList) {
            Subject subjectEntity = new Subject()
                    .setName(subject.getName())
                    .setPredictedRating(subject.getPredictedRating())
                    .setFinalRating(subject.getFinalRating());
            subjectEntityList.add(subjectEntity);
        }

        return subjectEntityList;
    }

    public static List<Grade> gradesToGradeEntities(List<io.github.wulkanowy.api.grades.Grade> gradeList) {

        List<Grade> gradeEntityList = new ArrayList<>();

        for (io.github.wulkanowy.api.grades.Grade grade : gradeList) {
            Grade gradeEntity = new Grade()
                    .setSubject(grade.getSubject())
                    .setValue(grade.getValue())
                    .setColor(grade.getColor())
                    .setSymbol(grade.getSymbol())
                    .setDescription(grade.getDescription())
                    .setWeight(grade.getWeight())
                    .setDate(grade.getDate())
                    .setTeacher(grade.getTeacher())
                    .setSemester(grade.getSemester());

            gradeEntityList.add(gradeEntity);
        }
        return gradeEntityList;
    }
}
