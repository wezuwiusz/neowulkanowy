package io.github.wulkanowy.data.db.dao.migrations;

import android.database.Cursor;
import android.os.AsyncTask;

import org.greenrobot.greendao.database.Database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.generic.Diary;
import io.github.wulkanowy.data.db.dao.DbHelper;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.utils.security.Scrambler;

public class Migration23 implements DbHelper.Migration {

    @Override
    public Integer getVersion() {
        return 23;
    }

    @Override
    public void runMigration(final Database db, final SharedPrefContract sharedPref, final Vulcan vulcan) throws Exception {
        createDiaryTable(db);
        migrateAccountsTable(db);

        final Map<String, String> user = getAccountData(db);
        vulcan.setCredentials(
                user.get("email"),
                Scrambler.decrypt(user.get("email"), user.get("password")),
                user.get("symbol"),
                user.get("school_id"),
                "", // inserted in code bellow
                ""
        );

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    insertDiaries(db, vulcan.getStudentAndParent().getDiaries());
                    updateAccount(db, vulcan.getStudentAndParent().getStudentID());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createDiaryTable(Database db) {
        db.execSQL("DROP TABLE IF EXISTS Diaries");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"Diaries\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"STUDENT_ID\" TEXT," + // 1: studentId
                "\"NAME\" TEXT," + // 2: name
                "\"VALUE\" TEXT," + // 3: value
                "\"IS_CURRENT\" INTEGER NOT NULL );"); // 4: isCurrent
    }

    private void migrateAccountsTable(Database db) {
        db.execSQL("DROP TABLE IF EXISTS tmp_account");
        db.execSQL("ALTER TABLE Accounts RENAME TO tmp_account");
        db.execSQL("CREATE TABLE IF NOT EXISTS \"Accounts\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"REAL_ID\" TEXT," + // 1: realId
                "\"SYMBOL\" TEXT," + // 2: symbol
                "\"SCHOOL_ID\" TEXT," + // 3: schoolId
                "\"NAME\" TEXT," + // 4: name
                "\"E_MAIL\" TEXT," + // 5: email
                "\"PASSWORD\" TEXT);"); // 6: password
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS IDX_Accounts_REAL_ID ON \"Accounts\" (\"REAL_ID\" ASC);");
        db.execSQL("INSERT INTO Accounts(NAME, E_MAIL, PASSWORD, SYMBOL, SCHOOL_ID)" +
                "SELECT `NAME`, `E-MAIL`, `PASSWORD`, `SYMBOL`, `SNPID` FROM tmp_account");
        db.execSQL("DROP TABLE tmp_account");
    }

    private Map<String, String> getAccountData(Database db) {
        Map<String, String> values = new HashMap<>();
        Cursor cursor = db.rawQuery("SELECT SYMBOL, SCHOOL_ID, NAME, E_MAIL, PASSWORD FROM Accounts", null);

        if (cursor.moveToFirst()) {
            do {
                values.put("symbol", cursor.getString(cursor.getColumnIndex("SYMBOL")));
                values.put("school_id", cursor.getString(cursor.getColumnIndex("SCHOOL_ID")));
                values.put("name", cursor.getString(cursor.getColumnIndex("NAME")));
                values.put("email", cursor.getString(cursor.getColumnIndex("E_MAIL")));
                values.put("password", cursor.getString(cursor.getColumnIndex("PASSWORD")));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return values;
    }

    private void insertDiaries(Database db, List<Diary> list) {
        for (Diary diary : list) {
            db.execSQL("INSERT INTO Diaries(STUDENT_ID, NAME, VALUE, IS_CURRENT) VALUES(" +
                    "\"" + diary.getId() + "\"," +
                    "\"" + diary.getName() + "\"," +
                    "\"" + diary.getId() + "\"," +
                    "\"" + (diary.isCurrent() ? "1" : "0") + "\"" +
                    ")");
        }
    }

    private void updateAccount(Database db, String realId) {
        db.execSQL("UPDATE Accounts SET REAL_ID = ?", new String[]{realId});
    }
}
