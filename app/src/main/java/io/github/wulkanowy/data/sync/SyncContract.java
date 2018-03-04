package io.github.wulkanowy.data.sync;

import java.io.IOException;
import java.text.ParseException;

import io.github.wulkanowy.api.login.NotLoggedInErrorException;

public interface SyncContract {

    void sync() throws NotLoggedInErrorException, IOException, ParseException;
}
