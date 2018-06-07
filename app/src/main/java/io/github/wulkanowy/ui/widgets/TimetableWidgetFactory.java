package io.github.wulkanowy.ui.widgets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.R;
import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.data.db.dao.entities.TimetableLesson;
import io.github.wulkanowy.data.db.dao.entities.Week;
import io.github.wulkanowy.utils.TimeUtils;

public class TimetableWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context context;

    private List<TimetableLesson> lessonList = new ArrayList<>();

    private final RepositoryContract repository;

    public TimetableWidgetFactory(Context context, RepositoryContract repository) {
        this.context = context;
        this.repository = repository;
    }


    @Override
    public void onCreate() {
        // do nothing
    }

    @Override
    public void onDataSetChanged() {
        lessonList = new ArrayList<>();

        if (repository.getSharedRepo().isUserLoggedIn()) {

            Week week = repository.getDbRepo().getWeek(TimeUtils.getDateOfCurrentMonday(true));
            int valueOfDay = TimeUtils.getTodayOrNextDayValue(repository.getSharedRepo().getTimetableWidgetState());

            if (valueOfDay != 5 && valueOfDay != 6 && week != null) {
                week.resetDayList();
                lessonList = week.getDayList().get(valueOfDay).getTimetableLessons();
            }
        }
    }

    @Override
    public void onDestroy() {
        // do nothing
    }

    @Override
    public int getCount() {
        return lessonList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION) {
            return null;
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.timetable_widget_item);
        views.setTextViewText(R.id.timetable_widget_item_subject, getSubjectName(position));
        views.setTextViewText(R.id.timetable_widget_item_time, getTimeText(position));
        views.setTextViewText(R.id.timetable_widget_item_room, getRoomText(position));

        if (!getDescriptionText(position).isEmpty()) {
            views.setViewVisibility(R.id.timetable_widget_item_description, View.VISIBLE);
            views.setTextViewText(R.id.timetable_widget_item_description, getDescriptionText(position));
        } else {
            views.setViewVisibility(R.id.timetable_widget_item_description, View.GONE);
        }

        if (lessonList.get(position).getMovedOrCanceled()) {
            views.setInt(R.id.timetable_widget_item_subject, "setPaintFlags",
                    Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        } else {
            views.setInt(R.id.timetable_widget_item_subject, "setPaintFlags",
                    Paint.ANTI_ALIAS_FLAG);
        }

        views.setOnClickFillInIntent(R.id.timetable_widget_item_container, new Intent());
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private String getSubjectName(int position) {
        return lessonList.get(position).getSubject();
    }

    private String getRoomText(int position) {
        TimetableLesson lesson = lessonList.get(position);
        if (!lesson.getRoom().isEmpty()) {
            return context.getString(R.string.timetable_dialog_room) + " " + lesson.getRoom();
        }
        return lesson.getRoom();
    }

    private String getTimeText(int position) {
        TimetableLesson lesson = lessonList.get(position);
        return lesson.getStartTime() + " - " + lesson.getEndTime();
    }

    private String getDescriptionText(int position) {
        return StringUtils.capitalize(lessonList.get(position).getDescription());
    }

}
