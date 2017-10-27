package io.github.wulkanowy.services.synchronisation;

import android.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.grades.SubjectsList;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.dao.entities.Subject;
import io.github.wulkanowy.dao.entities.SubjectDao;
import io.github.wulkanowy.services.LoginSession;
import io.github.wulkanowy.services.jobs.VulcanJobHelper;
import io.github.wulkanowy.utilities.ConversionVulcanObject;

public class SubjectsSynchronisation {

    public void sync(LoginSession loginSession) throws IOException,
            ParseException, NotLoggedInErrorException {

        SubjectsList subjectsList = loginSession.getVulcan().getSubjectsList();
        SubjectDao subjectDao = loginSession.getDaoSession().getSubjectDao();

        List<Subject> subjectEntitiesList = ConversionVulcanObject.subjectsToSubjectEntities(subjectsList.getAll());
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
