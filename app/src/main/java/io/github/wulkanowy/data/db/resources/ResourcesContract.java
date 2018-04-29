package io.github.wulkanowy.data.db.resources;

import javax.inject.Singleton;

import io.github.wulkanowy.data.db.dao.entities.AttendanceLesson;

@Singleton
public interface ResourcesContract {

    String[] getSymbolsKeysArray();

    String[] getSymbolsValuesArray();

    String getErrorLoginMessage(Exception e);

    String getAttendanceLessonDescription(AttendanceLesson lesson);
}
