package io.github.wulkanowy.ui.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.github.wulkanowy.R;
import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.services.widgets.TimetableWidgetServices;
import io.github.wulkanowy.ui.main.MainActivity;

import static io.github.wulkanowy.utils.TimeUtilsKt.getTodayOrNextDay;

public class TimetableWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_TIMETABLE_TOGGLE = "timetable_toggle";

    @Inject
    RepositoryContract repository;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        inject(context);

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.timetable_widget);

            setViews(views, context, appWidgetId);
            setToggleIntent(views, context);
            setTemplateIntent(views, context);
            updateWidget(views, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        inject(context);

        if (ACTION_TIMETABLE_TOGGLE.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context.getPackageName(),
                    TimetableWidgetProvider.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

            repository.getSharedRepo().setTimetableWidgetState(!repository.getSharedRepo().getTimetableWidgetState());
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        inject(context);
        repository.getSharedRepo().setTimetableWidgetState(false);
    }

    private void setToggleIntent(RemoteViews views, Context context) {
        Intent refreshIntent = new Intent(context, TimetableWidgetProvider.class);
        refreshIntent.setAction(ACTION_TIMETABLE_TOGGLE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1,
                refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.timetable_widget_toggle, pendingIntent);
    }

    private void setTemplateIntent(RemoteViews views, Context context) {
        Intent intent = MainActivity.getStartIntent(context);
        intent.putExtra(MainActivity.EXTRA_CARD_ID_KEY, 3);

        PendingIntent pendingIntent = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(intent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setPendingIntentTemplate(R.id.timetable_widget_list, pendingIntent);
    }

    private void setViews(RemoteViews views, Context context, int appWidgetId) {
        Intent intent = new Intent(context, TimetableWidgetServices.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        views.setRemoteAdapter(appWidgetId, R.id.timetable_widget_list, intent);
        views.setEmptyView(R.id.timetable_widget_list, R.id.timetable_widget_empty);

        boolean nextDay = repository.getSharedRepo().getTimetableWidgetState();

        String toggleText = context.getString(nextDay ? R.string.widget_timetable_tomorrow
                : R.string.widget_timetable_today);

        views.setTextViewText(R.id.timetable_widget_toggle, toggleText);
        views.setTextViewText(R.id.timetable_widget_date, getTodayOrNextDay(nextDay));
    }

    private void updateWidget(RemoteViews views, AppWidgetManager appWidgetManager, int appWidgetId) {
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.timetable_widget_list);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void inject(Context context) {
        if (repository == null) {
            AndroidInjection.inject(this, context);
        }
    }
}
