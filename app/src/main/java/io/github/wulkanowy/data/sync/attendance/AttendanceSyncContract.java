package io.github.wulkanowy.data.sync.attendance;

import java.io.IOException;
import java.text.ParseException;

import io.github.wulkanowy.api.login.NotLoggedInErrorException;

public interface AttendanceSyncContract {

    void syncAttendance(String date) throws NotLoggedInErrorException, IOException, ParseException;

    void syncAttendance() throws NotLoggedInErrorException, IOException, ParseException;
}
