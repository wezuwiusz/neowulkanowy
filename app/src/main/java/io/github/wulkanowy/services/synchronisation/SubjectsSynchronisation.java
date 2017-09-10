package io.github.wulkanowy.services.synchronisation;

import android.content.Context;

import java.io.IOException;
import java.text.ParseException;

import io.github.wulkanowy.api.grades.SubjectsList;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.database.subjects.SubjectsDatabase;

public class SubjectsSynchronisation {

    public void sync(VulcanSynchronisation vulcanSynchronisation, Context context) throws IOException, ParseException, LoginErrorException {

        SubjectsList subjectsList = new SubjectsList(vulcanSynchronisation.getStudentAndParent());

        SubjectsDatabase subjectsDatabase = new SubjectsDatabase(context);
        subjectsDatabase.open();
        subjectsDatabase.put(subjectsList.getAll());
        subjectsDatabase.close();
    }
}
