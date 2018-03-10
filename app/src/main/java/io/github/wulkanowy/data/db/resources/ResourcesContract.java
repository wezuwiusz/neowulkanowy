package io.github.wulkanowy.data.db.resources;

import io.github.wulkanowy.data.db.dao.entities.AttendanceLesson;

public interface ResourcesContract {

    String[] getSymbolsKeysArray();

    String[] getSymbolsValuesArray();

    String getErrorLoginMessage(Exception e);

    String getAttendanceLessonDescription(AttendanceLesson lesson);
}
