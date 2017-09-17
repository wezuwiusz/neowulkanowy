package io.github.wulkanowy.services.synchronisation;

import android.util.Log;

import org.greenrobot.greendao.query.Query;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.grades.GradesList;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.dao.EntitiesCompare;
import io.github.wulkanowy.dao.entities.Account;
import io.github.wulkanowy.dao.entities.AccountDao;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.dao.entities.Grade;
import io.github.wulkanowy.dao.entities.GradeDao;
import io.github.wulkanowy.dao.entities.Subject;
import io.github.wulkanowy.dao.entities.SubjectDao;
import io.github.wulkanowy.services.jobs.VulcanSync;
import io.github.wulkanowy.utilities.ConversionVulcanObject;

public class GradesSynchronisation {

    public void sync(VulcanSynchronisation vulcanSynchronisation, DaoSession daoSession) throws IOException,
            ParseException, LoginErrorException {

        GradesList gradesList = new GradesList(vulcanSynchronisation.getStudentAndParent());

        GradeDao gradeDao = daoSession.getGradeDao();
        AccountDao accountDao = daoSession.getAccountDao();
        SubjectDao subjectDao = daoSession.getSubjectDao();

        Account account = accountDao.load(vulcanSynchronisation.getUserId());

        List<Grade> gradesFromDb = account.getGradeList();
        List<Grade> gradeEntitiesList = ConversionVulcanObject.gradesToGradeEntities(gradesList.getAll());
        List<Grade> updatedList = EntitiesCompare.compareGradeList(gradeEntitiesList, gradesFromDb);
        List<Grade> lastList = new ArrayList<>();

        GradeDao.dropTable(gradeDao.getDatabase(), true);
        GradeDao.createTable(gradeDao.getDatabase(), false);

        for (Grade grade : updatedList) {

            Query<Subject> subjectQuery = subjectDao.queryBuilder()
                    .where(SubjectDao.Properties.Name.eq(grade.getSubject()))
                    .build();

            grade.setUserId(vulcanSynchronisation.getUserId());
            grade.setSubjectId((subjectQuery.uniqueOrThrow()).getId());

            lastList.add(grade);
        }

        gradeDao.insertInTx(lastList);

        Log.d(VulcanSync.DEBUG_TAG, "Synchronization grades (amount = " + String.valueOf(lastList.size() + ")"));
    }
}
