package io.github.wulkanowy.services.synchronisation;

import android.app.Activity;
import android.util.Log;

import io.github.wulkanowy.activity.WulkanowyApp;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.services.jobs.VulcanSync;

public class DataSynchronisation {

    private DaoSession daoSession;

    public DataSynchronisation(DaoSession daoSession) {
        this.daoSession = daoSession;
    }

    public DataSynchronisation(Activity activity) {
        daoSession = ((WulkanowyApp) activity.getApplication()).getDaoSession();
    }

    public void syncGrades(VulcanSynchronisation vulcanSynchronisation) {
        GradesSynchronisation gradesSynchronisation = new GradesSynchronisation();

        try {
            gradesSynchronisation.sync(vulcanSynchronisation, daoSession);
        } catch (Exception e) {
            Log.e(VulcanSync.DEBUG_TAG, "Synchronisation of grades failed", e);
        }
    }

    public void syncSubjectsAndGrades(VulcanSynchronisation vulcanSynchronisation) {
        SubjectsSynchronisation subjectsSynchronisation = new SubjectsSynchronisation();

        try {
            subjectsSynchronisation.sync(vulcanSynchronisation, daoSession);
            syncGrades(vulcanSynchronisation);
        } catch (Exception e) {
            Log.e(VulcanSync.DEBUG_TAG, "Synchronisation of subjects failed", e);
        }
    }
}
