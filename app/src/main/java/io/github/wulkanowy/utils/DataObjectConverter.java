package io.github.wulkanowy.utils;


import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.data.db.dao.entities.AttendanceLesson;
import io.github.wulkanowy.data.db.dao.entities.Day;
import io.github.wulkanowy.data.db.dao.entities.Diary;
import io.github.wulkanowy.data.db.dao.entities.Exam;
import io.github.wulkanowy.data.db.dao.entities.Grade;
import io.github.wulkanowy.data.db.dao.entities.School;
import io.github.wulkanowy.data.db.dao.entities.Semester;
import io.github.wulkanowy.data.db.dao.entities.Student;
import io.github.wulkanowy.data.db.dao.entities.Subject;
import io.github.wulkanowy.data.db.dao.entities.TimetableLesson;
import io.github.wulkanowy.data.db.dao.entities.Week;

public final class DataObjectConverter {

    private DataObjectConverter() {
        throw new IllegalStateException("Utility class");
    }

    public static List<School> schoolsToSchoolsEntities(List<io.github.wulkanowy.api.generic.School> schools, Long symbolId) {
        List<School> studentList = new ArrayList<>();

        for (io.github.wulkanowy.api.generic.School school : schools) {
            studentList.add(new School()
                    .setName(school.getName())
                    .setCurrent(school.getCurrent())
                    .setRealId(school.getId())
                    .setSymbolId(symbolId)
            );
        }

        return studentList;
    }

    public static List<Student> studentsToStudentEntities(List<io.github.wulkanowy.api.generic.Student> students, Long schoolId) {
        List<Student> studentList = new ArrayList<>();

        for (io.github.wulkanowy.api.generic.Student student : students) {
            studentList.add(new Student()
                    .setName(student.getName())
                    .setCurrent(student.isCurrent())
                    .setRealId(student.getId())
                    .setSchoolId(schoolId)
            );
        }

        return studentList;
    }

    public static List<Diary> diariesToDiaryEntities(List<io.github.wulkanowy.api.generic.Diary> diaryList, Long studentId) {
        List<Diary> diaryEntityList = new ArrayList<>();

        for (io.github.wulkanowy.api.generic.Diary diary : diaryList) {
            diaryEntityList.add(new Diary()
                    .setStudentId(studentId)
                    .setValue(diary.getId())
                    .setName(diary.getName())
                    .setCurrent(diary.isCurrent()));
        }

        return diaryEntityList;
    }

    public static List<Semester> semestersToSemesterEntities(List<io.github.wulkanowy.api.generic.Semester> semesters, long diaryId) {
        List<Semester> semesterList = new ArrayList<>();

        for (io.github.wulkanowy.api.generic.Semester semester : semesters) {
            semesterList.add(new Semester()
                    .setDiaryId(diaryId)
                    .setName(semester.getName())
                    .setCurrent(semester.isCurrent())
                    .setValue(semester.getId())
            );
        }

        return semesterList;
    }

    public static List<Subject> subjectsToSubjectEntities(List<io.github.wulkanowy.api.grades.Subject> subjectList, long semesterId) {
        List<Subject> subjectEntityList = new ArrayList<>();

        for (io.github.wulkanowy.api.grades.Subject subject : subjectList) {
            Subject subjectEntity = new Subject()
                    .setSemesterId(semesterId)
                    .setName(subject.getName())
                    .setPredictedRating(subject.getPredictedRating())
                    .setFinalRating(subject.getFinalRating());
            subjectEntityList.add(subjectEntity);
        }

        return subjectEntityList;
    }

    public static List<Grade> gradesToGradeEntities(List<io.github.wulkanowy.api.grades.Grade> gradeList, long semesterId) {
        List<Grade> gradeEntityList = new ArrayList<>();

        for (io.github.wulkanowy.api.grades.Grade grade : gradeList) {
            gradeEntityList.add(new Grade()
                    .setSubject(grade.getSubject())
                    .setValue(grade.getValue())
                    .setColor(grade.getColor())
                    .setSymbol(grade.getSymbol())
                    .setDescription(grade.getDescription())
                    .setWeight(grade.getWeight())
                    .setDate(grade.getDate())
                    .setTeacher(grade.getTeacher())
                    .setSemesterId(semesterId));
        }

        return gradeEntityList;
    }

    public static Week weekToWeekEntity(io.github.wulkanowy.api.generic.Week week) {
        return new Week().setStartDayDate(week.getStartDayDate());
    }

    public static Day dayToDayEntity(io.github.wulkanowy.api.generic.Day day) {
        return new Day()
                .setDate(day.getDate())
                .setDayName(day.getDayName());
    }

    public static Day timetableDayToDayEntity(io.github.wulkanowy.api.timetable.TimetableDay day) {
        return new Day()
                .setDate(day.getDate())
                .setDayName(day.getDayName())
                .setFreeDay(day.isFreeDay())
                .setFreeDayName(day.getFreeDayName());
    }

    public static List<Day> daysToDaysEntities(List<io.github.wulkanowy.api.generic.Day> dayList) {
        List<Day> dayEntityList = new ArrayList<>();

        for (io.github.wulkanowy.api.generic.Day day : dayList) {
            dayEntityList.add(dayToDayEntity(day));
        }
        return dayEntityList;
    }

    public static List<TimetableLesson> lessonsToTimetableLessonsEntities(List<io.github.wulkanowy.api.generic.Lesson> lessonList) {
        List<TimetableLesson> lessonEntityList = new ArrayList<>();

        for (io.github.wulkanowy.api.generic.Lesson lesson : lessonList) {
            lessonEntityList.add(new TimetableLesson()
                    .setNumber(lesson.getNumber())
                    .setSubject(lesson.getSubject())
                    .setTeacher(lesson.getTeacher())
                    .setRoom(lesson.getRoom())
                    .setDescription(lesson.getDescription())
                    .setGroup(lesson.getGroupName())
                    .setStartTime(lesson.getStartTime())
                    .setEndTime(lesson.getEndTime())
                    .setDate(lesson.getDate())
                    .setEmpty(lesson.isEmpty())
                    .setDivisionIntoGroups(lesson.isDivisionIntoGroups())
                    .setPlanning(lesson.isPlanning())
                    .setRealized(lesson.isRealized())
                    .setMovedOrCanceled(lesson.isMovedOrCanceled())
                    .setNewMovedInOrChanged(lesson.isNewMovedInOrChanged()));
        }

        return lessonEntityList;
    }

    public static List<AttendanceLesson> lessonsToAttendanceLessonsEntities(List<io.github.wulkanowy.api.generic.Lesson> lessonList) {
        List<AttendanceLesson> lessonEntityList = new ArrayList<>();

        for (io.github.wulkanowy.api.generic.Lesson lesson : lessonList) {
            lessonEntityList.add(new AttendanceLesson()
                    .setNumber(lesson.getNumber())
                    .setSubject(lesson.getSubject())
                    .setDate(lesson.getDate())
                    .setPresence(lesson.isPresence())
                    .setAbsenceUnexcused(lesson.isAbsenceUnexcused())
                    .setAbsenceExcused(lesson.isAbsenceExcused())
                    .setUnexcusedLateness(lesson.isUnexcusedLateness())
                    .setAbsenceForSchoolReasons(lesson.isAbsenceForSchoolReasons())
                    .setExcusedLateness(lesson.isExcusedLateness())
                    .setExemption(lesson.isExemption()));
        }
        return lessonEntityList;
    }

    public static List<Exam> examsToExamsEntity(List<io.github.wulkanowy.api.exams.Exam> examList) {
        List<Exam> examEntityList = new ArrayList<>();

        for (io.github.wulkanowy.api.exams.Exam exam : examList) {
            examEntityList.add(new Exam()
                    .setDescription(exam.getDescription())
                    .setEntryDate(exam.getEntryDate())
                    .setSubjectAndGroup(exam.getSubjectAndGroup())
                    .setTeacher(exam.getTeacher())
                    .setType(exam.getType()));
        }
        return examEntityList;
    }
}
