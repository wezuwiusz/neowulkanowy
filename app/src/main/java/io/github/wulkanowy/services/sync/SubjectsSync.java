package io.github.wulkanowy.services.sync;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.grades.SubjectsList;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.db.dao.entities.Subject;
import io.github.wulkanowy.db.dao.entities.SubjectDao;
import io.github.wulkanowy.services.jobs.VulcanJobHelper;
import io.github.wulkanowy.utils.DataObjectConverter;

public class SubjectsSync {

    public void sync(LoginSession loginSession) throws IOException,
            NotLoggedInErrorException {

        SubjectsList subjectsList = loginSession.getVulcan().getSubjectsList();
        SubjectDao subjectDao = loginSession.getDaoSession().getSubjectDao();

        List<Subject> subjectEntitiesList = DataObjectConverter.subjectsToSubjectEntities(subjectsList.getAll());
        List<Subject> preparedList = new ArrayList<>();

        for (Subject subject : subjectEntitiesList) {
            subject.setUserId(loginSession.getUserId());
            preparedList.add(subject);
        }

        SubjectDao.dropTable(subjectDao.getDatabase(), true);
        SubjectDao.createTable(subjectDao.getDatabase(), false);
        subjectDao.insertInTx(preparedList);


        Log.d(VulcanJobHelper.DEBUG_TAG, "Synchronization subjects (amount = " + String.valueOf(subjectEntitiesList.size() + ")"));
    }
}
