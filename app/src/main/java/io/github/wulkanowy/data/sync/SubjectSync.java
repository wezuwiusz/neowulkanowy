package io.github.wulkanowy.data.sync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.VulcanException;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.dao.entities.Semester;
import io.github.wulkanowy.data.db.dao.entities.Subject;
import io.github.wulkanowy.utils.DataObjectConverter;
import timber.log.Timber;

@Singleton
public class SubjectSync {

    private final DaoSession daoSession;

    private final Vulcan vulcan;

    private long semesterId;

    @Inject
    SubjectSync(DaoSession daoSession, Vulcan vulcan) {
        this.daoSession = daoSession;
        this.vulcan = vulcan;
    }

    public void sync(long semesterId) throws VulcanException, IOException {
        this.semesterId = semesterId;

        Semester semester = daoSession.getSemesterDao().load(semesterId);

        List<Subject> lastList = getUpdatedList(getSubjectsFromNet(semester));

        daoSession.getSubjectDao().deleteInTx(getSubjectsFromDb());
        daoSession.getSubjectDao().insertInTx(lastList);

        Timber.d("Subjects synchronization complete (%s)", lastList.size());
    }

    private List<Subject> getSubjectsFromNet(Semester semester) throws VulcanException, IOException {
        return DataObjectConverter.subjectsToSubjectEntities(
                vulcan.getSubjectsList().getAll(semester.getValue()), semesterId);
    }

    private List<Subject> getSubjectsFromDb() {
        Semester semester = daoSession.getSemesterDao().load(semesterId);
        semester.resetSubjectList();
        return semester.getSubjectList();
    }

    private List<Subject> getUpdatedList(List<Subject> subjectsFromNet) {
        List<Subject> updatedList = new ArrayList<>();

        for (Subject subject : subjectsFromNet) {
            subject.setSemesterId(semesterId);
            updatedList.add(subject);
        }
        return updatedList;
    }
}
