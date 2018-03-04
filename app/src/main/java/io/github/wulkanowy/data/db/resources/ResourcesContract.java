package io.github.wulkanowy.data.db.resources;

public interface ResourcesContract {

    String[] getSymbolsKeysArray();

    String[] getSymbolsValuesArray();

    String getErrorLoginMessage(Exception e);
}
