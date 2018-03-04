package io.github.wulkanowy.data.sync.grades;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.data.db.dao.entities.Account;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.dao.entities.Grade;
import io.github.wulkanowy.data.db.dao.entities.SubjectDao;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.data.sync.SyncContract;
import io.github.wulkanowy.utils.DataObjectConverter;
import io.github.wulkanowy.utils.EntitiesCompare;
import io.github.wulkanowy.utils.LogUtils;

@Singleton
public class GradeSync implements SyncContract {

    private final DaoSession daoSession;

    private final Vulcan vulcan;

    private final SharedPrefContract sharedPref;

    @Inject
    GradeSync(DaoSession daoSession, SharedPrefContract sharedPref, Vulcan vulcan) {
        this.daoSession = daoSession;
        this.sharedPref = sharedPref;
        this.vulcan = vulcan;
    }

    @Override
    public void sync() throws IOException, NotLoggedInErrorException, ParseException {

        long userId = sharedPref.getCurrentUserId();

        Account account = daoSession.getAccountDao().load(userId);
        account.resetGradeList();
        account.resetSubjectList();

        List<Grade> gradesFromNet = DataObjectConverter
                .gradesToGradeEntities(vulcan.getGradesList().getAll());
        List<Grade> gradesFromDb = account.getGradeList();

        List<Grade> updatedGrades = EntitiesCompare.compareGradeList(gradesFromNet, gradesFromDb);

        daoSession.getGradeDao().deleteInTx(gradesFromDb);

        List<Grade> lastList = new ArrayList<>();

        for (Grade grade : updatedGrades) {
            grade.setUserId(userId);
            grade.setSubjectId(daoSession.getSubjectDao().queryBuilder()
                    .where(SubjectDao.Properties.Name.eq(grade.getSubject()),
                            SubjectDao.Properties.UserId.eq(userId))
                    .build()
                    .uniqueOrThrow().getId());
            lastList.add(grade);
        }

        daoSession.getGradeDao().insertInTx(lastList);

        LogUtils.debug("Synchronization grades (amount = " + lastList.size() + ")");
    }
}
