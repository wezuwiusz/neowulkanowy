package io.github.wulkanowy.services.synchronisation;

import android.content.Context;
import android.util.Log;

import io.github.wulkanowy.services.jobs.VulcanSync;

public class DataSynchronisation {

    private Context context;

    public DataSynchronisation(Context context) {
        this.context = context;
    }

    public void syncGrades(VulcanSynchronisation vulcanSynchronisation) {
        GradesSynchronisation gradesSynchronisation = new GradesSynchronisation();

        try {
            gradesSynchronisation.sync(vulcanSynchronisation, context);
        } catch (Exception e) {
            Log.e(VulcanSync.DEBUG_TAG, "Synchronisation of grades failed", e);
        }
    }

    public void syncSubjects(VulcanSynchronisation vulcanSynchronisation) {
        SubjectsSynchronisation subjectsSynchronisation = new SubjectsSynchronisation();

        try {
            subjectsSynchronisation.sync(vulcanSynchronisation, context);
        } catch (Exception e) {
            Log.e(VulcanSync.DEBUG_TAG, "Synchronisation of subjects failed", e);
        }
    }

    public void syncGradesAndSubjects(VulcanSynchronisation vulcanSynchronisation) {
        syncSubjects(vulcanSynchronisation);
        syncGrades(vulcanSynchronisation);
    }
}
