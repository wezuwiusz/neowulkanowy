package io.github.wulkanowy.data.sync.subjects;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.dao.entities.Subject;
import io.github.wulkanowy.data.db.dao.entities.SubjectDao;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.data.sync.SyncContract;
import io.github.wulkanowy.utils.DataObjectConverter;
import io.github.wulkanowy.utils.LogUtils;

@Singleton
public class SubjectSync implements SyncContract {

    private final SubjectDao subjectDao;

    private final Vulcan vulcan;

    private final SharedPrefContract sharedPref;

    @Inject
    SubjectSync(DaoSession daoSession, SharedPrefContract sharedPref, Vulcan vulcan) {
        this.subjectDao = daoSession.getSubjectDao();
        this.sharedPref = sharedPref;
        this.vulcan = vulcan;
    }

    @Override
    public void sync() throws NotLoggedInErrorException, IOException, ParseException {

        long userId = sharedPref.getCurrentUserId();

        List<Subject> subjectsFromNet = DataObjectConverter
                .subjectsToSubjectEntities(vulcan.getSubjectsList().getAll());

        subjectDao.deleteInTx(subjectDao.queryBuilder()
                .where(SubjectDao.Properties.UserId.eq(userId))
                .build()
                .list());

        List<Subject> lastList = new ArrayList<>();

        for (Subject subject : subjectsFromNet) {
            subject.setUserId(userId);
            lastList.add(subject);
        }

        subjectDao.insertInTx(lastList);

        LogUtils.debug("Synchronization subjects (amount = " + lastList.size() + ")");
    }
}
