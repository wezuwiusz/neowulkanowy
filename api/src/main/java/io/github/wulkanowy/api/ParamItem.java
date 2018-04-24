package io.github.wulkanowy.api;

interface ParamItem {

    ParamItem setId(String id);

    ParamItem setName(String name);

    ParamItem setCurrent(boolean isCurrent);

    boolean isCurrent();
}
