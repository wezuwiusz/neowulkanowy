package io.github.wulkanowy.utils;


import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.data.db.dao.entities.AttendanceLesson;
import io.github.wulkanowy.data.db.dao.entities.Day;
import io.github.wulkanowy.data.db.dao.entities.Diary;
import io.github.wulkanowy.data.db.dao.entities.Grade;
import io.github.wulkanowy.data.db.dao.entities.Subject;
import io.github.wulkanowy.data.db.dao.entities.TimetableLesson;
import io.github.wulkanowy.data.db.dao.entities.Week;

public final class DataObjectConverter {

    private DataObjectConverter() {
        throw new IllegalStateException("Utility class");
    }

    public static List<Diary> diariesToDiaryEntities(List<io.github.wulkanowy.api.Diary> diaryList) {
        List<Diary> diaryEntityList = new ArrayList<>();

        for (io.github.wulkanowy.api.Diary diary : diaryList) {
            Diary diaryEntity = new Diary()
                    .setValue(diary.getId())
                    .setName(diary.getName())
                    .setStudentId(diary.getStudentId())
                    .setIsCurrent(diary.isCurrent());
            diaryEntityList.add(diaryEntity);
        }

        return diaryEntityList;
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

    public static Week weekToWeekEntity(io.github.wulkanowy.api.generic.Week week) {
        return new Week().setStartDayDate(week.getStartDayDate());
    }

    public static Day dayToDayEntity(io.github.wulkanowy.api.generic.Day day) {
        return new Day()
                .setDate(day.getDate())
                .setDayName(day.getDayName())
                .setIsFreeDay(day.isFreeDay())
                .setFreeDayName(day.getFreeDayName());
    }


    public static List<Day> daysToDaysEntities(List<io.github.wulkanowy.api.generic.Day> dayList) {
        List<Day> dayEntityList = new ArrayList<>();

        for (io.github.wulkanowy.api.generic.Day day : dayList) {
            dayEntityList.add(dayToDayEntity(day));
        }
        return dayEntityList;
    }

    public static TimetableLesson lessonToTimetableLessonEntity(io.github.wulkanowy.api.generic.Lesson lesson) {
        return new TimetableLesson()
                .setNumber(lesson.getNumber())
                .setSubject(lesson.getSubject())
                .setTeacher(lesson.getTeacher())
                .setRoom(lesson.getRoom())
                .setDescription(lesson.getDescription())
                .setGroupName(lesson.getGroupName())
                .setStartTime(lesson.getStartTime())
                .setEndTime(lesson.getEndTime())
                .setDate(lesson.getDate())
                .setEmpty(lesson.isEmpty())
                .setDivisionIntoGroups(lesson.isDivisionIntoGroups())
                .setPlanning(lesson.isPlanning())
                .setRealized(lesson.isRealized())
                .setMovedOrCanceled(lesson.isMovedOrCanceled())
                .setNewMovedInOrChanged(lesson.isNewMovedInOrChanged());
    }

    public static AttendanceLesson lessonToAttendanceLessonEntity(io.github.wulkanowy.api.generic.Lesson lesson) {
        return new AttendanceLesson()
                .setNumber(Integer.valueOf(lesson.getNumber()))
                .setSubject(lesson.getSubject())
                .setDate(lesson.getDate())
                .setIsPresence(lesson.isPresence())
                .setIsAbsenceUnexcused(lesson.isAbsenceUnexcused())
                .setIsAbsenceExcused(lesson.isAbsenceExcused())
                .setIsUnexcusedLateness(lesson.isUnexcusedLateness())
                .setIsAbsenceForSchoolReasons(lesson.isAbsenceForSchoolReasons())
                .setIsExcusedLateness(lesson.isExcusedLateness())
                .setIsExemption(lesson.isExemption());
    }

    public static List<TimetableLesson> lessonsToTimetableLessonsEntities(List<io.github.wulkanowy.api.generic.Lesson> lessonList) {
        List<TimetableLesson> lessonEntityList = new ArrayList<>();

        for (io.github.wulkanowy.api.generic.Lesson lesson : lessonList) {
            lessonEntityList.add(lessonToTimetableLessonEntity(lesson));
        }
        return lessonEntityList;
    }

    public static List<AttendanceLesson> lessonsToAttendanceLessonsEntities(List<io.github.wulkanowy.api.generic.Lesson> lessonList) {
        List<AttendanceLesson> lessonEntityList = new ArrayList<>();

        for (io.github.wulkanowy.api.generic.Lesson lesson : lessonList) {
            lessonEntityList.add(lessonToAttendanceLessonEntity(lesson));
        }
        return lessonEntityList;
    }
}
