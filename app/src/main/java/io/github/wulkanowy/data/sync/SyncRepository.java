package io.github.wulkanowy.data.sync;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.VulcanException;
import io.github.wulkanowy.data.db.dao.DbContract;
import io.github.wulkanowy.utils.security.CryptoException;

@Singleton
public class SyncRepository implements SyncContract {

    private final GradeSync gradeSync;

    private final SubjectSync subjectSync;

    private final AttendanceSync attendanceSync;

    private final TimetableSync timetableSync;

    private final AccountSync accountSync;

    private final ExamsSync examsSync;

    private final DbContract database;

    @Inject
    SyncRepository(GradeSync gradeSync, SubjectSync subjectSync, AttendanceSync attendanceSync,
                   TimetableSync timetableSync, AccountSync accountSync, ExamsSync examsSync,
                   DbContract database) {
        this.gradeSync = gradeSync;
        this.subjectSync = subjectSync;
        this.attendanceSync = attendanceSync;
        this.timetableSync = timetableSync;
        this.accountSync = accountSync;
        this.examsSync = examsSync;
        this.database = database;
    }

    @Override
    public void registerUser(String email, String password, String symbol) throws VulcanException,
            IOException, CryptoException {
        accountSync.registerUser(email, password, symbol);
    }

    @Override
    public void initLastUser() throws CryptoException {
        accountSync.initLastUser();
    }

    @Override
    public void syncGrades(int semesterName) throws VulcanException, IOException {
        gradeSync.sync(semesterName);
    }

    @Override
    public void syncGrades() throws VulcanException, IOException {
        gradeSync.sync(database.getCurrentSemesterId());
    }

    @Override
    public void syncSubjects(int semesterName) throws VulcanException, IOException {
        subjectSync.sync(semesterName);
    }

    @Override
    public void syncSubjects() throws VulcanException, IOException {
        subjectSync.sync(database.getCurrentSemesterId());
    }

    @Override
    public void syncAttendance() throws IOException, VulcanException {
        attendanceSync.syncAttendance(database.getCurrentDiaryId(), null);
    }

    @Override
    public void syncAttendance(long diaryId, String date) throws IOException, VulcanException {
        if (diaryId != 0) {
            attendanceSync.syncAttendance(diaryId, date);
        } else {
            attendanceSync.syncAttendance(database.getCurrentDiaryId(), date);
        }
    }

    @Override
    public void syncTimetable() throws VulcanException, IOException {
        timetableSync.syncTimetable(database.getCurrentDiaryId(), null);
    }

    @Override
    public void syncTimetable(long diaryId, String date) throws VulcanException, IOException {
        if (diaryId != 0) {
            timetableSync.syncTimetable(diaryId, date);
        } else {
            timetableSync.syncTimetable(database.getCurrentDiaryId(), date);
        }
    }

    @Override
    public void syncExams() throws VulcanException, IOException {
        examsSync.syncExams(database.getCurrentDiaryId(), null);
    }

    @Override
    public void syncExams(long diaryId, String date) throws VulcanException, IOException {
        if (diaryId != 0) {
            examsSync.syncExams(diaryId, date);
        } else {
            examsSync.syncExams(database.getCurrentDiaryId(), date);
        }
    }

    @Override
    public void syncAll() throws VulcanException, IOException {
        syncSubjects();
        syncGrades();
        syncAttendance();
        syncTimetable();
        syncExams();
    }
}
