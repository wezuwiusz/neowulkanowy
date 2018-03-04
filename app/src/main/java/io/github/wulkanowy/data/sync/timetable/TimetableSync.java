package io.github.wulkanowy.data.sync.timetable;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.dao.entities.Day;
import io.github.wulkanowy.data.db.dao.entities.DayDao;
import io.github.wulkanowy.data.db.dao.entities.Lesson;
import io.github.wulkanowy.data.db.dao.entities.LessonDao;
import io.github.wulkanowy.data.db.dao.entities.Week;
import io.github.wulkanowy.data.db.dao.entities.WeekDao;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.utils.DataObjectConverter;
import io.github.wulkanowy.utils.LogUtils;
import io.github.wulkanowy.utils.TimeUtils;

@Singleton
public class TimetableSync implements TimetableSyncContract {

    private final DaoSession daoSession;

    private final Vulcan vulcan;

    private final SharedPrefContract sharedPref;

    @Inject
    TimetableSync(DaoSession daoSession, SharedPrefContract sharedPref, Vulcan vulcan) {
        this.daoSession = daoSession;
        this.sharedPref = sharedPref;
        this.vulcan = vulcan;
    }

    @Override
    public void syncTimetable(String date) throws NotLoggedInErrorException, IOException, ParseException {
        long userId = sharedPref.getCurrentUserId();

        io.github.wulkanowy.api.timetable.Week weekFromNet = date == null ? vulcan.getTimetable().getWeekTable()
                : vulcan.getTimetable().getWeekTable(String.valueOf(TimeUtils.getNetTicks(date)));

        Week weekFromDb = daoSession.getWeekDao().queryBuilder()
                .where(WeekDao.Properties.UserId.eq(userId),
                        WeekDao.Properties.StartDayDate.eq(weekFromNet.getStartDayDate()))
                .unique();

        Long weekId;

        if (weekFromDb == null) {
            Week weekFromNetEntity = DataObjectConverter.weekToWeekEntity(weekFromNet).setUserId(userId);
            weekId = daoSession.getWeekDao().insert(weekFromNetEntity);
        } else {
            weekId = weekFromDb.getId();
        }

        List<io.github.wulkanowy.api.timetable.Day> dayListFromNet = weekFromNet.getDays();

        List<Lesson> updatedLessonList = new ArrayList<>();

        for (io.github.wulkanowy.api.timetable.Day dayFromNet : dayListFromNet) {
            Day dayFromNetEntity = DataObjectConverter.dayToDayEntity(dayFromNet);

            Day dayFromDb = daoSession.getDayDao().queryBuilder()
                    .where(DayDao.Properties.UserId.eq(userId),
                            DayDao.Properties.WeekId.eq(weekId),
                            DayDao.Properties.Date.eq(dayFromNetEntity.getDate()))
                    .unique();

            dayFromNetEntity.setUserId(userId);
            dayFromNetEntity.setWeekId(weekId);

            Long dayId;

            if (dayFromDb != null) {
                dayFromNetEntity.setId(dayFromDb.getId());
                daoSession.getDayDao().save(dayFromNetEntity);
                dayId = dayFromNetEntity.getId();
            } else {
                dayId = daoSession.getDayDao().insert(dayFromNetEntity);
            }

            List<Lesson> lessonListFromNetEntities = DataObjectConverter
                    .lessonsToLessonsEntities(dayFromNet.getLessons());

            for (Lesson lessonFromNetEntity : lessonListFromNetEntities) {
                Lesson lessonFromDb = daoSession.getLessonDao().queryBuilder()
                        .where(LessonDao.Properties.DayId.eq(dayId),
                                LessonDao.Properties.Date.eq(lessonFromNetEntity.getDate()),
                                LessonDao.Properties.StartTime.eq(lessonFromNetEntity.getStartTime()),
                                LessonDao.Properties.EndTime.eq(lessonFromNetEntity.getEndTime()))
                        .unique();

                if (lessonFromDb != null) {
                    lessonFromNetEntity.setId(lessonFromDb.getId());
                }

                lessonFromNetEntity.setDayId(dayFromNetEntity.getId());

                if (!"".equals(lessonFromNetEntity.getSubject())) {
                    updatedLessonList.add(lessonFromNetEntity);
                }
            }
        }
        daoSession.getLessonDao().saveInTx(updatedLessonList);

        LogUtils.debug("Synchronization lessons (amount = " + updatedLessonList.size() + ")");
    }

    @Override
    public void syncTimetable() throws NotLoggedInErrorException, IOException, ParseException {
        syncTimetable(null);
    }
}
