package io.github.wulkanowy.data.sync.grades;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.VulcanException;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.dao.entities.Grade;
import io.github.wulkanowy.data.db.dao.entities.Semester;
import io.github.wulkanowy.data.db.dao.entities.SubjectDao;
import io.github.wulkanowy.data.sync.SyncContract;
import io.github.wulkanowy.utils.DataObjectConverter;
import io.github.wulkanowy.utils.EntitiesCompare;
import io.github.wulkanowy.utils.LogUtils;

@Singleton
public class GradeSync implements SyncContract {

    private final DaoSession daoSession;

    private final Vulcan vulcan;

    private long semesterId;

    @Inject
    GradeSync(DaoSession daoSession, Vulcan vulcan) {
        this.daoSession = daoSession;
        this.vulcan = vulcan;
    }

    @Override
    public void sync(long semesterId) throws IOException, VulcanException, ParseException {
        this.semesterId = semesterId;

        Semester semester = daoSession.getSemesterDao().load(semesterId);
        resetSemesterRelations(semester);

        List<Grade> lastList = getUpdatedList(getComparedList(semester));

        daoSession.getGradeDao().deleteInTx(semester.getGradeList());
        daoSession.getGradeDao().insertInTx(lastList);

        LogUtils.debug("Synchronization grades (amount = " + lastList.size() + ")");
    }

    private void resetSemesterRelations(Semester semester) {
        semester.resetSubjectList();
        semester.resetGradeList();
    }

    private List<Grade> getUpdatedList(List<Grade> comparedList) {
        List<Grade> updatedList = new ArrayList<>();

        for (Grade grade : comparedList) {
            grade.setSemesterId(semesterId);
            grade.setSubjectId(getSubjectId(grade.getSubject()));
            updatedList.add(grade);
        }

        return updatedList;
    }

    private List<Grade> getComparedList(Semester semester) throws IOException, VulcanException, ParseException {
        List<Grade> gradesFromNet = DataObjectConverter.gradesToGradeEntities(
                vulcan.getGradesList().getAll(semester.getValue()), semesterId);

        List<Grade> gradesFromDb = semester.getGradeList();

        return EntitiesCompare.compareGradeList(gradesFromNet, gradesFromDb);
    }

    private Long getSubjectId(String subjectName) {
        return daoSession.getSubjectDao().queryBuilder().where(
                SubjectDao.Properties.Name.eq(subjectName),
                SubjectDao.Properties.SemesterId.eq(semesterId)
        ).build().uniqueOrThrow().getId();
    }
}
