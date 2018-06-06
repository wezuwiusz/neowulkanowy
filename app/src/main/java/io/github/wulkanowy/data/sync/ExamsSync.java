package io.github.wulkanowy.data.sync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.VulcanException;
import io.github.wulkanowy.api.exams.ExamDay;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.dao.entities.Day;
import io.github.wulkanowy.data.db.dao.entities.DayDao;
import io.github.wulkanowy.data.db.dao.entities.Exam;
import io.github.wulkanowy.data.db.dao.entities.ExamDao;
import io.github.wulkanowy.data.db.dao.entities.Week;
import io.github.wulkanowy.data.db.dao.entities.WeekDao;
import io.github.wulkanowy.utils.DataObjectConverter;
import timber.log.Timber;

public class ExamsSync {

    private final DaoSession daoSession;

    private final Vulcan vulcan;

    private long diaryId;

    @Inject
    ExamsSync(DaoSession daoSession, Vulcan vulcan) {
        this.daoSession = daoSession;
        this.vulcan = vulcan;
    }

    public void syncExams(long diaryId, String date) throws IOException, VulcanException {
        this.diaryId = diaryId;

        io.github.wulkanowy.api.generic.Week<ExamDay> weekApi = getWeekFromApi(date);
        Week weekDb = getWeekFromDb(weekApi.getStartDayDate());

        long weekId = updateWeekInDb(weekDb, weekApi);

        List<Exam> examList = getPreparedExams(weekApi.getDays(), weekId);

        daoSession.getExamDao().saveInTx(examList);

        Timber.d("Exams synchronization complete (%s)", examList.size());
    }

    private Week getWeekFromDb(String date) {
        return daoSession.getWeekDao().queryBuilder().where(
                WeekDao.Properties.DiaryId.eq(diaryId),
                WeekDao.Properties.StartDayDate.eq(date)
        ).unique();
    }

    private io.github.wulkanowy.api.generic.Week<ExamDay> getWeekFromApi(String date)
            throws VulcanException, IOException {
        return vulcan.getExamsList().getWeek(date, true);
    }

    private Long updateWeekInDb(Week weekDb, io.github.wulkanowy.api.generic.Week weekApi) {
        if (weekDb != null) {
            weekDb.setExamsSynced(true);
            weekDb.update();

            return weekDb.getId();
        }

        Week weekApiEntity = DataObjectConverter.weekToWeekEntity(weekApi).setDiaryId(diaryId);
        weekApiEntity.setExamsSynced(true);

        return daoSession.getWeekDao().insert(weekApiEntity);
    }

    private Day getDayFromDb(String date, long weekId) {
        return daoSession.getDayDao().queryBuilder().where(
                DayDao.Properties.WeekId.eq(weekId),
                DayDao.Properties.Date.eq(date)
        ).unique();
    }

    private List<Exam> getPreparedExams(List<ExamDay> dayListFromApi,
                                        long weekId) {
        List<Exam> preparedExamList = new ArrayList<>();

        for (ExamDay dayFromApi : dayListFromApi) {

            Day dayDb = getDayFromDb(dayFromApi.getDate(), weekId);

            Day dayApiEntity = DataObjectConverter.dayToDayEntity(dayFromApi);

            long dayId = updateDayInDb(dayDb, dayApiEntity, weekId);

            prepareExam(dayFromApi.getExamList(), preparedExamList, dayId);
        }
        return preparedExamList;
    }

    private long updateDayInDb(Day dayDb, Day dayApi, long weekId) {
        dayApi.setWeekId(weekId);

        if (null != dayDb) {
            return dayDb.getId();
        }
        return daoSession.getDayDao().insert(dayApi);
    }

    private void prepareExam(List<io.github.wulkanowy.api.exams.Exam> examList,
                             List<Exam> preparedExams, long dayId) {
        List<Exam> examsApiEntity = DataObjectConverter.examsToExamsEntity(examList);

        for (Exam examApi : examsApiEntity) {
            Exam examDb = getExamFromDb(examApi, dayId);

            examApi.setDayId(dayId);

            if (examDb != null) {
                examApi.setId(examDb.getId());
            }
            preparedExams.add(examApi);
        }
    }

    private Exam getExamFromDb(Exam examApi, long dayId) {
        return daoSession.getExamDao().queryBuilder()
                .where(ExamDao.Properties.DayId.eq(dayId),
                        ExamDao.Properties.EntryDate.eq(examApi.getEntryDate()),
                        ExamDao.Properties.SubjectAndGroup.eq(examApi.getSubjectAndGroup()),
                        ExamDao.Properties.Type.eq(examApi.getType()),
                        ExamDao.Properties.Teacher.eq(examApi.getTeacher()))
                .unique();
    }
}
