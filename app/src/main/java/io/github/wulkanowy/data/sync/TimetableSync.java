package io.github.wulkanowy.data.sync;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.VulcanException;
import io.github.wulkanowy.api.generic.Lesson;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.dao.entities.Day;
import io.github.wulkanowy.data.db.dao.entities.DayDao;
import io.github.wulkanowy.data.db.dao.entities.TimetableLesson;
import io.github.wulkanowy.data.db.dao.entities.TimetableLessonDao;
import io.github.wulkanowy.data.db.dao.entities.Week;
import io.github.wulkanowy.data.db.dao.entities.WeekDao;
import io.github.wulkanowy.utils.DataObjectConverter;
import timber.log.Timber;

@Singleton
public class TimetableSync {

    private final DaoSession daoSession;

    private final Vulcan vulcan;

    private long diaryId;

    @Inject
    TimetableSync(DaoSession daoSession, Vulcan vulcan) {
        this.daoSession = daoSession;
        this.vulcan = vulcan;
    }

    public void syncTimetable(long diaryId, String date) throws IOException, VulcanException {
        this.diaryId = diaryId;

        io.github.wulkanowy.api.generic.Week<io.github.wulkanowy.api.timetable.TimetableDay> weekApi = getWeekFromApi(date);
        Week weekDb = getWeekFromDb(weekApi.getStartDayDate());

        long weekId = updateWeekInDb(weekDb, weekApi);

        List<TimetableLesson> lessonList = updateDays(weekApi.getDays(), weekId);

        daoSession.getTimetableLessonDao().saveInTx(lessonList);

        Timber.d("Timetable synchronization complete (%s)", lessonList.size());
    }

    private io.github.wulkanowy.api.generic.Week<io.github.wulkanowy.api.timetable.TimetableDay> getWeekFromApi(String date)
            throws IOException, VulcanException {
        return vulcan.getTimetable().getWeekTable(date);
    }

    private Week getWeekFromDb(String date) {
        return daoSession.getWeekDao().queryBuilder().where(
                WeekDao.Properties.DiaryId.eq(diaryId),
                WeekDao.Properties.StartDayDate.eq(date)
        ).unique();
    }

    private Long updateWeekInDb(Week dbEntity, io.github.wulkanowy.api.generic.Week fromApi) {
        if (dbEntity != null) {
            dbEntity.setTimetableSynced(true);
            dbEntity.update();

            return dbEntity.getId();
        }

        Week apiEntity = DataObjectConverter.weekToWeekEntity(fromApi).setDiaryId(diaryId);
        apiEntity.setTimetableSynced(true);

        return daoSession.getWeekDao().insert(apiEntity);
    }

    private List<TimetableLesson> updateDays(List<io.github.wulkanowy.api.timetable.TimetableDay> dayListFromApi, long weekId) {
        List<TimetableLesson> updatedLessonList = new ArrayList<>();

        for (io.github.wulkanowy.api.timetable.TimetableDay dayFromApi : dayListFromApi) {

            Day dbDayEntity = getDayFromDb(dayFromApi.getDate(), weekId);

            Day apiDayEntity = DataObjectConverter.timetableDayToDayEntity(dayFromApi);

            long dayId = updateDay(dbDayEntity, apiDayEntity, weekId);

            updateLessons(dayFromApi.getLessons(), updatedLessonList, dayId);
        }

        return updatedLessonList;
    }

    private Day getDayFromDb(String date, long weekId) {
        return daoSession.getDayDao().queryBuilder().where(
                DayDao.Properties.WeekId.eq(weekId),
                DayDao.Properties.Date.eq(date)
        ).unique();
    }

    private long updateDay(Day dayFromDb, Day apiDayEntity, long weekId) {
        apiDayEntity.setWeekId(weekId);

        if (null != dayFromDb) {
            apiDayEntity.setId(dayFromDb.getId());

            daoSession.getDayDao().save(apiDayEntity);
            dayFromDb.refresh();

            return dayFromDb.getId();
        }

        return daoSession.getDayDao().insert(apiDayEntity);
    }

    private void updateLessons(List<Lesson> lessons, List<TimetableLesson> updatedLessons, long dayId) {
        List<TimetableLesson> lessonsFromApiEntities = DataObjectConverter
                .lessonsToTimetableLessonsEntities(lessons);

        List<TimetableLesson> lessonsFromDbEntities = getLessonsFromDb(dayId);

        if (!lessonsFromDbEntities.isEmpty()) {
            List<TimetableLesson> lessonToRemove = new ArrayList<>(CollectionUtils.removeAll(lessonsFromDbEntities, lessonsFromApiEntities));

            for (TimetableLesson timetableLesson : lessonToRemove) {
                daoSession.getTimetableLessonDao().delete(timetableLesson);
            }
        }

        for (TimetableLesson apiLessonEntity : lessonsFromApiEntities) {
            TimetableLesson lessonFromDb = getLessonFromDb(apiLessonEntity, dayId);

            apiLessonEntity.setDayId(dayId);

            if (lessonFromDb != null) {
                apiLessonEntity.setId(lessonFromDb.getId());
            }

            if (!"".equals(apiLessonEntity.getSubject())) {
                updatedLessons.add(apiLessonEntity);
            }
        }
    }

    private TimetableLesson getLessonFromDb(TimetableLesson apiEntity, long dayId) {
        return daoSession.getTimetableLessonDao().queryBuilder()
                .where(TimetableLessonDao.Properties.DayId.eq(dayId),
                        TimetableLessonDao.Properties.Date.eq(apiEntity.getDate()),
                        TimetableLessonDao.Properties.StartTime.eq(apiEntity.getStartTime()),
                        TimetableLessonDao.Properties.EndTime.eq(apiEntity.getEndTime()))
                .unique();
    }

    private List<TimetableLesson> getLessonsFromDb(long dayId) {
        return daoSession.getDayDao().load(dayId).getTimetableLessons();
    }
}
