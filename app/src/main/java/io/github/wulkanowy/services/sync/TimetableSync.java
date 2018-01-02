package io.github.wulkanowy.services.sync;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.greendao.query.Query;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.api.timetable.Day;
import io.github.wulkanowy.api.timetable.Week;
import io.github.wulkanowy.db.dao.entities.DayDao;
import io.github.wulkanowy.db.dao.entities.Lesson;
import io.github.wulkanowy.db.dao.entities.LessonDao;
import io.github.wulkanowy.db.dao.entities.WeekDao;
import io.github.wulkanowy.services.jobs.VulcanJobHelper;
import io.github.wulkanowy.utils.DataObjectConverter;
import io.github.wulkanowy.utils.TimeUtils;

public class TimetableSync {

    public void sync(@NonNull LoginSession loginSession, @Nullable String dateOfMonday) throws NotLoggedInErrorException,
            IOException, ParseException {
        DayDao dayDao = loginSession.getDaoSession().getDayDao();
        LessonDao lessonDao = loginSession.getDaoSession().getLessonDao();
        WeekDao weekDao = loginSession.getDaoSession().getWeekDao();

        Long weekId;

        Week week = dateOfMonday == null ? loginSession.getVulcan().getTimetable().getWeekTable()
                : loginSession.getVulcan().getTimetable()
                .getWeekTable(String.valueOf(TimeUtils.getNetTicks(dateOfMonday, "yyyy-MM-dd")));

        Query<io.github.wulkanowy.db.dao.entities.Week> weekQuery = weekDao.queryBuilder()
                .where(WeekDao.Properties.UserId.eq(loginSession.getUserId()), WeekDao.Properties.StartDayDate.eq(week.getStartDayDate())).build();

        io.github.wulkanowy.db.dao.entities.Week week1 = weekQuery.unique();

        if (week1 != null) {
            weekId = week1.getId();
        } else {
            weekId = weekDao.insert(DataObjectConverter.weekToWeekEntitie(week).setUserId(loginSession.getUserId()));
        }

        List<Day> dayList = week.getDays();

        dayDao.saveInTx(getPreparedDaysList(dayList, loginSession.getUserId(), weekId, dayDao));

        Log.d(VulcanJobHelper.DEBUG_TAG, "Synchronization days (amount = " + dayList.size() + ")");

        List<Lesson> lessonList = new ArrayList<>();
        lessonList.addAll(getPreparedLessonsList(dayList, dayDao, lessonDao, loginSession.getUserId(), weekId));

        lessonDao.saveInTx(lessonList);

        Log.d(VulcanJobHelper.DEBUG_TAG, "Synchronization lessons (amount = " + lessonList.size() + ")");
    }

    private List<Lesson> getPreparedLessonsList(List<Day> dayList, DayDao dayDao, LessonDao lessonDao, long userId, long weekId) {
        List<Lesson> allLessonsList = new ArrayList<>();

        for (Day day : dayList) {

            Query<io.github.wulkanowy.db.dao.entities.Day> dayQuery = dayDao.queryBuilder()
                    .where(DayDao.Properties.Date.eq(day.getDate()),
                            DayDao.Properties.UserId.eq(userId),
                            DayDao.Properties.WeekId.eq(weekId))
                    .build();

            List<Lesson> lessonEntityList = DataObjectConverter.lessonsToLessonsEntities(day.getLessons());
            List<Lesson> updatedLessonEntityList = new ArrayList<>();

            for (Lesson lesson : lessonEntityList) {
                Lesson lesson1 = lessonDao.queryBuilder()
                        .where(LessonDao.Properties.DayId.eq(dayQuery.uniqueOrThrow().getId()),
                                LessonDao.Properties.Date.eq(lesson.getDate()),
                                LessonDao.Properties.StartTime.eq(lesson.getStartTime()),
                                LessonDao.Properties.EndTime.eq(lesson.getEndTime()))
                        .unique();

                if (lesson1 != null) {
                    lesson.setId(lesson1.getId());
                }

                lesson.setDayId(dayQuery.uniqueOrThrow().getId());
                if (!"".equals(lesson.getSubject())) {
                    updatedLessonEntityList.add(lesson);
                }
            }
            allLessonsList.addAll(updatedLessonEntityList);
        }
        return allLessonsList;
    }

    private List<io.github.wulkanowy.db.dao.entities.Day> getPreparedDaysList(List<Day> dayList, long userId, long weekId, DayDao dayDao) {
        List<io.github.wulkanowy.db.dao.entities.Day> updatedDayList = new ArrayList<>();
        List<io.github.wulkanowy.db.dao.entities.Day> dayEntityList = DataObjectConverter
                .daysToDaysEntities(dayList);
        for (io.github.wulkanowy.db.dao.entities.Day day : dayEntityList) {

            Query<io.github.wulkanowy.db.dao.entities.Day> dayQuery = dayDao.queryBuilder().where(DayDao.Properties.UserId.eq(userId), DayDao.Properties.WeekId.eq(weekId), DayDao.Properties.Date.eq(day.getDate())).build();

            io.github.wulkanowy.db.dao.entities.Day day1 = dayQuery.unique();

            if (day1 != null) {
                day.setId(day1.getId());
            }

            day.setUserId(userId);
            day.setWeekId(weekId);
            updatedDayList.add(day);
        }
        return updatedDayList;
    }
}
