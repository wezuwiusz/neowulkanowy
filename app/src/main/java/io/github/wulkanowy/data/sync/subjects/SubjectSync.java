package io.github.wulkanowy.data.sync.subjects;

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
import io.github.wulkanowy.data.sync.SyncContract;
import io.github.wulkanowy.utils.DataObjectConverter;
import io.github.wulkanowy.utils.LogUtils;

@Singleton
public class SubjectSync implements SyncContract {

    private final DaoSession daoSession;

    private final Vulcan vulcan;

    private long semesterId;

    @Inject
    SubjectSync(DaoSession daoSession, Vulcan vulcan) {
        this.daoSession = daoSession;
        this.vulcan = vulcan;
    }

    @Override
    public void sync(long semesterId) throws VulcanException, IOException {
        this.semesterId = semesterId;

        List<Subject> lastList = getUpdatedList(getSubjectsFromNet());

        daoSession.getSubjectDao().deleteInTx(getSubjectsFromDb());
        daoSession.getSubjectDao().insertInTx(lastList);

        LogUtils.debug("Synchronization subjects (amount = " + lastList.size() + ")");
    }

    private List<Subject> getSubjectsFromNet() throws VulcanException, IOException {
        return DataObjectConverter.subjectsToSubjectEntities(
                vulcan.getSubjectsList().getAll(String.valueOf(semesterId)), semesterId);
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
