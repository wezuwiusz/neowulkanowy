package io.github.wulkanowy.data.sync.attendance;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.VulcanException;
import io.github.wulkanowy.api.generic.Lesson;
import io.github.wulkanowy.data.db.dao.entities.AttendanceLesson;
import io.github.wulkanowy.data.db.dao.entities.AttendanceLessonDao;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.dao.entities.Day;
import io.github.wulkanowy.data.db.dao.entities.DayDao;
import io.github.wulkanowy.data.db.dao.entities.Week;
import io.github.wulkanowy.data.db.dao.entities.WeekDao;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.utils.DataObjectConverter;
import io.github.wulkanowy.utils.LogUtils;
import io.github.wulkanowy.utils.TimeUtils;

@Singleton
public class AttendanceSync implements AttendanceSyncContract {

    private final DaoSession daoSession;

    private final SharedPrefContract sharedPref;

    private final Vulcan vulcan;

    private long userId;

    @Inject
    AttendanceSync(DaoSession daoSession, SharedPrefContract sharedPref, Vulcan vulcan) {
        this.daoSession = daoSession;
        this.sharedPref = sharedPref;
        this.vulcan = vulcan;
    }

    @Override
    public void syncAttendance() throws IOException, ParseException, VulcanException {
        syncAttendance(null);
    }

    @Override
    public void syncAttendance(String date) throws IOException, ParseException, VulcanException {
        this.userId = sharedPref.getCurrentUserId();

        io.github.wulkanowy.api.generic.Week<io.github.wulkanowy.api.generic.Day> weekApi = getWeekFromApi(getNormalizedDate(date));
        Week weekDb = getWeekFromDb(weekApi.getStartDayDate());

        long weekId = updateWeekInDb(weekDb, weekApi);

        List<AttendanceLesson> lessonList = updateDays(weekApi.getDays(), weekId);

        daoSession.getAttendanceLessonDao().saveInTx(lessonList);

        LogUtils.debug("Synchronization attendance lessons (amount = " + lessonList.size() + ")");
    }

    private String getNormalizedDate(String date) throws ParseException {
        return null != date ? String.valueOf(TimeUtils.getNetTicks(date)) : "";
    }

    private io.github.wulkanowy.api.generic.Week<io.github.wulkanowy.api.generic.Day> getWeekFromApi(String date)
            throws IOException, ParseException, VulcanException {
        return vulcan.getAttendanceTable().getWeekTable(date);
    }

    private Week getWeekFromDb(String date) {
        return daoSession.getWeekDao()
                .queryBuilder()
                .where(WeekDao.Properties.UserId.eq(userId), WeekDao.Properties.StartDayDate.eq(date))
                .unique();
    }

    private Long updateWeekInDb(Week dbWeekEntity, io.github.wulkanowy.api.generic.Week fromApi) {
        if (dbWeekEntity != null) {
            dbWeekEntity.setIsAttendanceSynced(true);
            dbWeekEntity.update();

            return dbWeekEntity.getId();
        }

        Week apiWeekEntity = DataObjectConverter.weekToWeekEntity(fromApi).setUserId(userId);
        apiWeekEntity.setIsAttendanceSynced(true);

        return daoSession.getWeekDao().insert(apiWeekEntity);
    }

    private List<AttendanceLesson> updateDays(List<io.github.wulkanowy.api.generic.Day> dayListFromApi, long weekId) {
        List<AttendanceLesson> updatedLessonList = new ArrayList<>();

        for (io.github.wulkanowy.api.generic.Day dayFromApi : dayListFromApi) {

            Day dbDayEntity = getDayFromDb(dayFromApi.getDate());

            Day apiDayEntity = DataObjectConverter.dayToDayEntity(dayFromApi);

            long dayId = updateDay(dbDayEntity, apiDayEntity, weekId);

            updateLessons(dayFromApi.getLessons(), updatedLessonList, dayId);
        }

        return updatedLessonList;
    }

    private Day getDayFromDb(String date) {
        return daoSession.getDayDao()
                .queryBuilder()
                .where(DayDao.Properties.UserId.eq(userId), DayDao.Properties.Date.eq(date))
                .unique();
    }

    private long updateDay(Day dbDayEntity, Day apiDayEntity, long weekId) {
        if (null != dbDayEntity) {
            return dbDayEntity.getId();
        }

        apiDayEntity.setUserId(userId);
        apiDayEntity.setWeekId(weekId);

        return daoSession.getDayDao().insert(apiDayEntity);
    }

    private void updateLessons(List<Lesson> lessons, List<AttendanceLesson> updatedLessons, long dayId) {
        List<AttendanceLesson> lessonsFromApiEntities = DataObjectConverter
                .lessonsToAttendanceLessonsEntities(lessons);

        for (AttendanceLesson apiLessonEntity : lessonsFromApiEntities) {
            AttendanceLesson lessonFromDb = getLessonFromDb(apiLessonEntity, dayId);

            apiLessonEntity.setDayId(dayId);

            if (lessonFromDb != null) {
                apiLessonEntity.setId(lessonFromDb.getId());
            }

            if (!"".equals(apiLessonEntity.getSubject())) {
                updatedLessons.add(apiLessonEntity);
            }
        }
    }

    private AttendanceLesson getLessonFromDb(AttendanceLesson apiEntity, long dayId) {
        return daoSession.getAttendanceLessonDao().queryBuilder()
                .where(AttendanceLessonDao.Properties.DayId.eq(dayId),
                        AttendanceLessonDao.Properties.Date.eq(apiEntity.getDate()),
                        AttendanceLessonDao.Properties.Number.eq(apiEntity.getNumber()))
                .unique();
    }
}
