package io.github.wulkanowy.data;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.inject.Singleton;

import io.github.wulkanowy.api.VulcanException;
import io.github.wulkanowy.data.db.dao.entities.Account;
import io.github.wulkanowy.data.db.dao.entities.Grade;
import io.github.wulkanowy.data.db.dao.entities.Week;
import io.github.wulkanowy.data.db.resources.ResourcesContract;
import io.github.wulkanowy.data.sync.account.AccountSyncContract;
import io.github.wulkanowy.data.sync.attendance.AttendanceSyncContract;
import io.github.wulkanowy.data.sync.timetable.TimetableSyncContract;

@Singleton
public interface RepositoryContract extends ResourcesContract, AccountSyncContract,
        AttendanceSyncContract, TimetableSyncContract {

    long getCurrentUserId();

    int getStartupTab();

    boolean isServicesEnable();

    boolean isNotifyEnable();

    int getServicesInterval();

    boolean isMobileDisable();

    void syncGrades() throws VulcanException, IOException, ParseException;

    void syncSubjects() throws VulcanException, IOException, ParseException;

    void syncAll() throws VulcanException, IOException, ParseException;

    Account getCurrentUser();

    Week getWeek(String date);

    List<Grade> getNewGrades();
}
