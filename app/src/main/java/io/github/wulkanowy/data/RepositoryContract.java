package io.github.wulkanowy.data;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.inject.Singleton;

import io.github.wulkanowy.api.VulcanException;
import io.github.wulkanowy.data.db.dao.entities.Account;
import io.github.wulkanowy.data.db.dao.entities.Grade;
import io.github.wulkanowy.data.db.dao.entities.Subject;
import io.github.wulkanowy.data.db.dao.entities.Week;
import io.github.wulkanowy.data.db.resources.ResourcesContract;
import io.github.wulkanowy.data.sync.account.AccountSyncContract;

@Singleton
public interface RepositoryContract extends ResourcesContract, AccountSyncContract {

    long getCurrentUserId();

    int getStartupTab();

    void setTimetableWidgetState(boolean nextDay);

    boolean getTimetableWidgetState();

    boolean isServicesEnable();

    boolean isNotifyEnable();

    boolean isShowGradesSummary();

    int getServicesInterval();

    boolean isMobileDisable();

    void syncGrades() throws VulcanException, IOException, ParseException;

    void syncSubjects() throws VulcanException, IOException, ParseException;

    void syncAttendance() throws ParseException, IOException, VulcanException;

    void syncAttendance(String date) throws ParseException, IOException, VulcanException;

    void syncTimetable() throws VulcanException, IOException, ParseException;

    void syncTimetable(String date) throws VulcanException, IOException, ParseException;

    void syncAll() throws VulcanException, IOException, ParseException;

    Account getCurrentUser();

    Week getWeek(String date);

    List<Subject> getSubjectList();

    List<Grade> getNewGrades();

    long getCurrentStudentId();

    long getCurrentSymbolId();

    long getCurrentDiaryId();

    long getCurrentSemesterId();
}
