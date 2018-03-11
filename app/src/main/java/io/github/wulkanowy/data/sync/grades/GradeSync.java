package io.github.wulkanowy.data.sync.grades;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.VulcanException;
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

    private Long userId;

    @Inject
    GradeSync(DaoSession daoSession, SharedPrefContract sharedPref, Vulcan vulcan) {
        this.daoSession = daoSession;
        this.sharedPref = sharedPref;
        this.vulcan = vulcan;
    }

    @Override
    public void sync() throws IOException, VulcanException, ParseException {

        userId = sharedPref.getCurrentUserId();

        Account account = daoSession.getAccountDao().load(userId);
        resetAccountRelations(account);

        List<Grade> lastList = getUpdatedList(getComparedList(account));

        daoSession.getGradeDao().deleteInTx(account.getGradeList());
        daoSession.getGradeDao().insertInTx(lastList);

        LogUtils.debug("Synchronization grades (amount = " + lastList.size() + ")");
    }

    private void resetAccountRelations(Account account) {
        account.resetSubjectList();
        account.resetGradeList();
    }

    private List<Grade> getUpdatedList(List<Grade> comparedList) {
        List<Grade> updatedList = new ArrayList<>();

        for (Grade grade : comparedList) {
            grade.setUserId(userId);
            grade.setSubjectId(getSubjectId(grade.getSubject()));
            updatedList.add(grade);
        }
        return updatedList;
    }

    private List<Grade> getComparedList(Account account) throws IOException, VulcanException,
            ParseException {
        List<Grade> gradesFromNet = DataObjectConverter
                .gradesToGradeEntities(vulcan.getGradesList().getAll());

        List<Grade> gradesFromDb = account.getGradeList();

        return EntitiesCompare.compareGradeList(gradesFromNet, gradesFromDb);
    }

    private Long getSubjectId(String subjectName) {
        return daoSession.getSubjectDao().queryBuilder()
                .where(SubjectDao.Properties.Name.eq(subjectName),
                        SubjectDao.Properties.UserId.eq(userId))
                .build()
                .uniqueOrThrow()
                .getId();
    }
}
