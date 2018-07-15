package io.github.wulkanowy.data.db.dao.migrations

import android.database.Cursor

import org.greenrobot.greendao.database.Database

import io.github.wulkanowy.api.Vulcan
import io.github.wulkanowy.data.db.dao.DbHelper
import io.github.wulkanowy.data.db.shared.SharedPrefContract

class Migration29 : DbHelper.Migration {

    override fun getVersion(): Int? {
        return 29
    }

    override fun runMigration(db: Database, sharedPref: SharedPrefContract, vulcan: Vulcan) {
        createSchoolsTable(db)
        modifyStudents(db)
        insertSchool(db, getRealSchoolId(db))
    }

    private fun createSchoolsTable(db: Database) {
        db.execSQL("DROP TABLE IF EXISTS \"Schools\";")
        db.execSQL("CREATE TABLE IF NOT EXISTS \"Schools\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"symbol_id\" INTEGER," + // 1: symbolId
                "\"current\" INTEGER NOT NULL ," + // 2: current
                "\"real_id\" TEXT," + // 3: realId
                "\"name\" TEXT);") // 4: name
    }

    private fun modifyStudents(db: Database) {
        db.execSQL("ALTER TABLE Students ADD COLUMN school_id INTEGER")
        db.execSQL("UPDATE Students SET school_id = '1'")
    }

    private fun getRealSchoolId(db: Database): String {
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT school_id FROM Symbols WHERE _id=?", arrayOf("1"))

            return if (cursor!!.count > 0) {
                cursor.moveToFirst()
                cursor.getString(cursor.getColumnIndex("school_id"))
            } else ""
        } finally {
            cursor!!.close()
        }
    }

    private fun insertSchool(db: Database, realId: String) {
        db.execSQL("INSERT INTO Schools(symbol_id, current, real_id, name) VALUES(" +
                "\"1\"," +
                "\"1\"," +
                "\"" + realId + "\"," +
                "\"Uczeń\"" +
                ")")
    }
}
