package io.github.wulkanowy.data.sync;

import java.io.IOException;
import java.text.ParseException;

import io.github.wulkanowy.api.VulcanException;

public interface SyncContract {

    void sync(long semesterId) throws VulcanException, IOException, ParseException;
}
