package io.github.wulkanowy.services.synchronisation;

import android.content.Context;

import java.io.IOException;
import java.text.ParseException;

import io.github.wulkanowy.api.grades.GradesList;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.database.grades.GradesDatabase;

public class GradesSynchronisation {

    public void sync(VulcanSynchronisation vulcanSynchronisation, Context context) throws IOException, ParseException, LoginErrorException {

        GradesList gradesList = new GradesList(vulcanSynchronisation.getStudentAndParent());

        GradesDatabase gradesDatabase = new GradesDatabase(context);
        gradesDatabase.open();
        gradesDatabase.put(gradesList.getAll());
        gradesDatabase.close();
    }
}
