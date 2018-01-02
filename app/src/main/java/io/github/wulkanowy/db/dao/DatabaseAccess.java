package io.github.wulkanowy.db.dao;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import io.github.wulkanowy.db.dao.entities.DaoSession;
import io.github.wulkanowy.db.dao.entities.Grade;
import io.github.wulkanowy.db.dao.entities.GradeDao;

public class DatabaseAccess {

    public List<Grade> getNewGrades(DaoSession daoSession) {
        Query<Grade> gradeQuery = daoSession.getGradeDao().queryBuilder()
                .where(GradeDao.Properties.IsNew.eq(1))
                .build();

        return gradeQuery.list();
    }
}
