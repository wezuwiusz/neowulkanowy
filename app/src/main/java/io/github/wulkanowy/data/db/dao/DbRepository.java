package io.github.wulkanowy.data.db.dao;

import java.util.List;

import javax.inject.Inject;

import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.dao.entities.DiaryDao;
import io.github.wulkanowy.data.db.dao.entities.Grade;
import io.github.wulkanowy.data.db.dao.entities.GradeDao;
import io.github.wulkanowy.data.db.dao.entities.SemesterDao;
import io.github.wulkanowy.data.db.dao.entities.StudentDao;
import io.github.wulkanowy.data.db.dao.entities.Subject;
import io.github.wulkanowy.data.db.dao.entities.SymbolDao;
import io.github.wulkanowy.data.db.dao.entities.Week;
import io.github.wulkanowy.data.db.dao.entities.WeekDao;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;

public class DbRepository implements DbContract {

    private final DaoSession daoSession;

    private final SharedPrefContract sharedPref;

    @Inject
    DbRepository(DaoSession daoSession, SharedPrefContract sharedPrefContract) {
        this.daoSession = daoSession;
        this.sharedPref = sharedPrefContract;
    }


    @Override
    public Week getWeek(String date) {
        return daoSession.getWeekDao().queryBuilder().where(
                WeekDao.Properties.StartDayDate.eq(date),
                WeekDao.Properties.DiaryId.eq(getCurrentDiaryId())
        ).unique();
    }

    public List<Subject> getSubjectList() {
        return daoSession.getSemesterDao().load(getCurrentSemesterId()).getSubjectList();
    }

    @Override
    public List<Grade> getNewGrades() {
        return daoSession.getGradeDao().queryBuilder().where(
                GradeDao.Properties.IsNew.eq(1),
                GradeDao.Properties.SemesterId.eq(getCurrentSemesterId())
        ).list();
    }

    @Override
    public long getCurrentSymbolId() {
        return daoSession.getSymbolDao().queryBuilder().where(
                SymbolDao.Properties.UserId.eq(sharedPref.getCurrentUserId())
        ).unique().getId();
    }

    @Override
    public long getCurrentStudentId() {
        return daoSession.getStudentDao().queryBuilder().where(
                StudentDao.Properties.SymbolId.eq(getCurrentSymbolId()),
                StudentDao.Properties.Current.eq(true)
        ).unique().getId();
    }

    @Override
    public long getCurrentDiaryId() {
        return daoSession.getDiaryDao().queryBuilder().where(
                DiaryDao.Properties.StudentId.eq(getCurrentStudentId()),
                DiaryDao.Properties.Current.eq(true)
        ).unique().getId();
    }

    @Override
    public long getCurrentSemesterId() {
        return daoSession.getSemesterDao().queryBuilder().where(
                SemesterDao.Properties.DiaryId.eq(getCurrentDiaryId()),
                SemesterDao.Properties.Current.eq(true)
        ).unique().getId();
    }
}
