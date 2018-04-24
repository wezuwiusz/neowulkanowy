package io.github.wulkanowy.services.widgets;

import android.content.Intent;
import android.widget.RemoteViewsService;

import io.github.wulkanowy.ui.widgets.TimetableWidgetFactory;

public class TimetableWidgetServices extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TimetableWidgetFactory(getApplicationContext());
    }
}
