package io.github.wulkanowy.ui.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem;

public interface AsyncResponse<T extends AbstractExpandableHeaderItem> {
    void onQuarryProcessFinish(@NonNull List<T> resultItemList);

    void onRefreshProcessFinish(@Nullable List<T> resultItemList, int stringErrorId);
}
