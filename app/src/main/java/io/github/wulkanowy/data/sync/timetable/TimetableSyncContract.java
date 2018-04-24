package io.github.wulkanowy.data.sync.timetable;

import java.io.IOException;
import java.text.ParseException;

import io.github.wulkanowy.api.VulcanException;

public interface TimetableSyncContract {

    void syncTimetable(long diaryId, String date) throws VulcanException, IOException, ParseException;

    void syncTimetable(long diaryId) throws VulcanException, IOException, ParseException;
}
