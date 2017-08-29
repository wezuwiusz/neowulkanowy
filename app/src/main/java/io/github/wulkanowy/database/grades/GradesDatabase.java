package io.github.wulkanowy.database.grades;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.activity.dashboard.grades.GradeItem;
import io.github.wulkanowy.api.grades.Grade;
import io.github.wulkanowy.database.DatabaseAdapter;
import io.github.wulkanowy.database.DatabaseComparer;
import io.github.wulkanowy.database.DatabaseHelper;
import io.github.wulkanowy.database.subjects.SubjectsDatabase;

public class GradesDatabase extends DatabaseAdapter {

    private String idText = "id";
    private String userIdText = "userID";
    private String subjectIdText = "subjectID";
    private String subject = "subject";
    private String value = "value";
    private String color = "color";
    private String symbol = "symbol";
    private String description = "description";
    private String weight = "weight";
    private String date = "date";
    private String teacher = "teacher";
    private String isNew = "isNew";
    private String semester = "semester";
    private String grades = "grades";

    public GradesDatabase(Context context) {
        super(context);
    }

    public long put(Grade grade) throws SQLException {

        ContentValues newGrade = new ContentValues();
        newGrade.put(userIdText, context.getSharedPreferences("LoginData", context.MODE_PRIVATE).getLong("isLogin", 0));
        newGrade.put(subjectIdText, SubjectsDatabase.getSubjectId(grade.getSubject()));
        newGrade.put(subject, grade.getSubject());
        newGrade.put(value, grade.getValue());
        newGrade.put(color, grade.getColor());
        newGrade.put(symbol, grade.getSymbol());
        newGrade.put(description, grade.getDescription());
        newGrade.put(weight, grade.getWeight());
        newGrade.put(date, grade.getDate());
        newGrade.put(teacher, grade.getTeacher());
        newGrade.put(semester, grade.getSemester());
        newGrade.put(isNew, grade.isNew() ? 1 : 0);

        if (!database.isReadOnly()) {
            long newId = database.insertOrThrow(grades, null, newGrade);
            Log.d(DatabaseHelper.DEBUG_TAG, "Put grade " + newId + " into database");
            return newId;
        }

        Log.e(DatabaseHelper.DEBUG_TAG, "Attempt to write on read-only database");
        throw new SQLException("Attempt to write on read-only database");
    }

    public List<Long> put(List<Grade> gradeList) throws SQLException {

        List<Long> newIdList = new ArrayList<>();
        List<Grade> preparedList;

        if (checkExist(grades, null, null)) {
            preparedList = DatabaseComparer.compareGradesLists(gradeList, getAllUserGrades());
            deleteAndCreate(grades);
        } else {
            preparedList = gradeList;
        }

        for (Grade grade : preparedList) {

            newIdList.add(put(grade));
        }
        return newIdList;
    }

    public long update(Grade grade) throws SQLException {

        ContentValues updateGrade = new ContentValues();
        updateGrade.put(userIdText, grade.getUserID());
        updateGrade.put(subjectIdText, grade.getSubjectID());
        updateGrade.put(subject, grade.getSubject());
        updateGrade.put(value, grade.getValue());
        updateGrade.put(color, grade.getColor());
        updateGrade.put(symbol, grade.getSymbol());
        updateGrade.put(description, grade.getDescription());
        updateGrade.put(weight, grade.getWeight());
        updateGrade.put(date, grade.getDate());
        updateGrade.put(teacher, grade.getTeacher());
        updateGrade.put(semester, grade.getSemester());
        updateGrade.put(isNew, grade.isNew() ? 1 : 0);
        String args[] = {grade.getId() + ""};

        if (!database.isReadOnly()) {
            long updateId = database.update(grades, updateGrade, "id=?", args);
            Log.d(DatabaseHelper.DEBUG_TAG, "Update grade " + updateId + " into database");
            return updateId;
        }

        Log.e(DatabaseHelper.DEBUG_TAG, "Attempt to write on read-only database");
        throw new SQLException("Attempt to write on read-only database");

    }

    public Grade getGrade(long id) throws SQLException {

        Grade grade = new Grade();

        String[] columns = {idText, userIdText, subjectIdText, value, color, description, weight, date, teacher};
        String args[] = {id + ""};

        try {
            Cursor cursor = database.query(grades, columns, "id=?", args, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                grade.setId(cursor.getInt(0));
                grade.setUserID(cursor.getInt(1));
                grade.setSubjectID(cursor.getInt(2));
                grade.setSubject(cursor.getString(3));
                grade.setValue(cursor.getString(4));
                grade.setColor(cursor.getString(5));
                grade.setSymbol(cursor.getString(6));
                grade.setDescription(cursor.getString(7));
                grade.setWeight(cursor.getString(8));
                grade.setDate(cursor.getString(9));
                grade.setTeacher(cursor.getString(10));
                grade.setSemester(cursor.getString(11));
                grade.setIsNew(cursor.getInt(12) != 0);
                cursor.close();
            }
        } catch (SQLException e) {

            Log.e(DatabaseHelper.DEBUG_TAG, e.getMessage());
            throw e;
        } catch (CursorIndexOutOfBoundsException e) {

            Log.e(DatabaseHelper.DEBUG_TAG, e.getMessage());
            throw new SQLException(e.getMessage());
        }

        Log.d(DatabaseHelper.DEBUG_TAG, "Extract grade " + id + " from database");

        return grade;
    }

    public List<GradeItem> getSubjectGrades(long userId, long subjectId) throws SQLException {

        String whereExec = "SELECT * FROM " + grades + " WHERE " + userIdText + "=? AND " + subjectIdText + "=?";

        List<GradeItem> gradesList = new ArrayList<>();

        Cursor cursor = database.rawQuery(whereExec, new String[]{String.valueOf(userId), String.valueOf(subjectId)});

        while (cursor.moveToNext()) {
            GradeItem grade = new GradeItem();
            grade.setId(cursor.getInt(0));
            grade.setUserID(cursor.getInt(1));
            grade.setSubjectID(cursor.getInt(2));
            grade.setSubject(cursor.getString(3));
            grade.setValue(cursor.getString(4));
            grade.setColor(cursor.getString(5));
            grade.setSymbol(cursor.getString(6));
            grade.setDescription(cursor.getString(7));
            grade.setWeight(cursor.getString(8));
            grade.setDate(cursor.getString(9));
            grade.setTeacher(cursor.getString(10));
            grade.setSemester(cursor.getString(11));
            grade.setIsNew(cursor.getInt(12) != 0);
            gradesList.add(grade);
        }

        cursor.close();
        return gradesList;
    }

    public List<Grade> getAllUserGrades() {

        List<Grade> gradesList = new ArrayList<>();

        String exec = "SELECT * FROM " + grades + " WHERE " + userIdText + "=?";

        Cursor cursor = database.rawQuery(exec, new String[]{String.valueOf(context.getSharedPreferences("LoginData", context.MODE_PRIVATE).getLong("isLogin", 0))});

        while (cursor.moveToNext()) {
            Grade grade = new Grade();
            grade.setSubject(cursor.getString(3));
            grade.setValue(cursor.getString(4));
            grade.setColor(cursor.getString(5));
            grade.setSymbol(cursor.getString(6));
            grade.setDescription(cursor.getString(7));
            grade.setWeight(cursor.getString(8));
            grade.setDate(cursor.getString(9));
            grade.setTeacher(cursor.getString(10));
            grade.setSemester(cursor.getString(11));
            grade.setIsNew(cursor.getInt(12) != 0);
            gradesList.add(grade);
        }
        cursor.close();
        return gradesList;
    }
}

