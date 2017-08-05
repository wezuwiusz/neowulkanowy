package io.github.wulkanowy.database.subjects;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.grades.Subject;
import io.github.wulkanowy.database.DatabaseAdapter;
import io.github.wulkanowy.database.DatabaseHelper;

public class SubjectsDatabase extends DatabaseAdapter {

    private static String idText = "id";
    private static String name = "name";
    private static String predictedRating1 = "predictedRating1";
    private static String finalRating1 = "finalRating1";
    private static String predictedRating2 = "predictedRating2";
    private static String finalRating2 = "finalRating2";
    private static String subjects = "subjects";

    public SubjectsDatabase(Context context) {
        super(context);
    }

    public long put(Subject subject) throws SQLException {

        ContentValues newSubject = new ContentValues();
        newSubject.put(name, subject.getName());
        newSubject.put(predictedRating1, subject.getPredictedRating());
        newSubject.put(finalRating1, subject.getFinalRating());

        if (!database.isReadOnly()) {
            long newId = database.insertOrThrow(subjects, null, newSubject);
            Log.d(DatabaseHelper.DEBUG_TAG, "Put subject " + newId + " into database");
            return newId;
        }

        Log.e(DatabaseHelper.DEBUG_TAG, "Attempt to write on read-only database");
        throw new SQLException("Attempt to write on read-only database");
    }

    public List<Long> put(List<Subject> subjectList) throws SQLException {

        List<Long> newIdList = new ArrayList<>();

        for (Subject subject : subjectList) {
            if (!checkExist(subjects, name, subject.getName())) {
                newIdList.add(put(subject));
            }
        }
        return newIdList;
    }

    public long update(Subject subject) throws SQLException {

        ContentValues updateSubject = new ContentValues();
        updateSubject.put(name, subject.getName());
        updateSubject.put(predictedRating1, subject.getPredictedRating());
        updateSubject.put(finalRating1, subject.getFinalRating());
        String args[] = {subject.getId() + ""};

        if (!database.isReadOnly()) {
            long updateId = database.update(subjects, updateSubject, "id=?", args);
            Log.d(DatabaseHelper.DEBUG_TAG, "Update subject " + updateId + " into database");
            return updateId;
        }

        Log.e(DatabaseHelper.DEBUG_TAG, "Attempt to write on read-only database");
        throw new SQLException("Attempt to write on read-only database");
    }

    public Subject getSubject(long id) throws SQLException {

        Subject subject = new Subject();

        String[] columns = {idText, name, predictedRating1, finalRating1, predictedRating2, finalRating2};
        String args[] = {id + ""};

        try {
            Cursor cursor = database.query(subjects, columns, "id=?", args, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                subject.setId(cursor.getInt(0));
                subject.setName(cursor.getString(1));
                subject.setPredictedRating(cursor.getString(2));
                subject.setFinalRating(cursor.getString(3));
                cursor.close();
            }
        } catch (SQLException e) {

            Log.e(DatabaseHelper.DEBUG_TAG, e.getMessage());
            throw e;
        } catch (CursorIndexOutOfBoundsException e) {

            Log.e(DatabaseHelper.DEBUG_TAG, e.getMessage());
            throw new SQLException(e.getMessage());
        }

        Log.d(DatabaseHelper.DEBUG_TAG, "Extract subject " + id + " from database");

        return subject;
    }

    public List<Subject> getAllSubjectsNames() {

        List<Subject> subjectsList = new ArrayList<>();

        String exec = "SELECT " + name + " FROM " + subjects;

        Cursor cursor = database.rawQuery(exec, null);

        while (cursor.moveToNext()) {
            Subject subject = new Subject();
            subject.setName(cursor.getString(0));
            subjectsList.add(subject);
        }
        cursor.close();
        return subjectsList;
    }

    public static long getSubjectId(String nameSubject) throws SQLException {

        String whereExec = "SELECT " + idText + " FROM " + subjects + " WHERE " + name + " =?";

        Cursor cursor = database.rawQuery(whereExec, new String[]{nameSubject});
        cursor.moveToFirst();
        int idSubject = cursor.getInt(0);
        cursor.close();
        return idSubject;
    }
}
