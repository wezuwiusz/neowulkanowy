package io.github.wulkanowy.services.widgets;

import android.content.Intent;
import android.widget.RemoteViewsService;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.ui.widgets.TimetableWidgetFactory;

public class TimetableWidgetServices extends RemoteViewsService {

    @Inject
    RepositoryContract repository;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        AndroidInjection.inject(this);
        return new TimetableWidgetFactory(getApplicationContext(), repository);
    }
}
