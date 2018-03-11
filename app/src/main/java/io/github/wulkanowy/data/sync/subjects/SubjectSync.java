package io.github.wulkanowy.data.sync.subjects;

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
import io.github.wulkanowy.data.db.dao.entities.Subject;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.data.sync.SyncContract;
import io.github.wulkanowy.utils.DataObjectConverter;
import io.github.wulkanowy.utils.LogUtils;

@Singleton
public class SubjectSync implements SyncContract {

    private final DaoSession daoSession;

    private final Vulcan vulcan;

    private final SharedPrefContract sharedPref;

    private Long userId;

    @Inject
    SubjectSync(DaoSession daoSession, SharedPrefContract sharedPref, Vulcan vulcan) {
        this.daoSession = daoSession;
        this.sharedPref = sharedPref;
        this.vulcan = vulcan;
    }

    @Override
    public void sync() throws VulcanException, IOException, ParseException {

        userId = sharedPref.getCurrentUserId();

        List<Subject> lastList = getUpdatedList(getSubjectsFromNet());

        daoSession.getSubjectDao().deleteInTx(getSubjectsFromDb());
        daoSession.getSubjectDao().insertInTx(lastList);

        LogUtils.debug("Synchronization subjects (amount = " + lastList.size() + ")");
    }

    private List<Subject> getSubjectsFromNet() throws VulcanException, IOException {
        return DataObjectConverter.subjectsToSubjectEntities(vulcan.getSubjectsList().getAll());
    }

    private List<Subject> getSubjectsFromDb() {
        Account account = daoSession.getAccountDao().load(userId);
        account.resetSubjectList();
        return account.getSubjectList();
    }

    private List<Subject> getUpdatedList(List<Subject> subjectsFromNet) {
        List<Subject> updatedList = new ArrayList<>();

        for (Subject subject : subjectsFromNet) {
            subject.setUserId(userId);
            updatedList.add(subject);
        }
        return updatedList;
    }
}
