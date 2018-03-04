package io.github.wulkanowy.data.sync.timetable;

import java.io.IOException;
import java.text.ParseException;

import io.github.wulkanowy.api.login.NotLoggedInErrorException;

public interface TimetableSyncContract {

    void syncTimetable(String date) throws NotLoggedInErrorException, IOException, ParseException;

    void syncTimetable() throws NotLoggedInErrorException, IOException, ParseException;
}
