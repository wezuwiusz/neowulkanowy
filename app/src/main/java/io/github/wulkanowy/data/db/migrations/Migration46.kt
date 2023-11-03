package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

class Migration46 : Migration(45, 46) {

    override fun migrate(db: SupportSQLiteDatabase) {
        migrateConferences(db)
        migrateMessages(db)
        migrateMobileDevices(db)
        migrateNotifications(db)
        migrateTimetable(db)
        migrateTimetableAdditional(db)
    }

    private fun migrateConferences(db: SupportSQLiteDatabase) {
        db.query("SELECT * FROM Conferences").use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow("id"))
                val timestampLocal = it.getLong(it.getColumnIndexOrThrow("date"))
                val timestampUtc = timestampLocal.timestampLocalToUTC()

                db.execSQL("UPDATE Conferences SET date = $timestampUtc WHERE id = $id")
            }
        }
    }

    private fun migrateMessages(db: SupportSQLiteDatabase) {
        db.query("SELECT * FROM Messages").use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow("id"))
                val timestampLocal = it.getLong(it.getColumnIndexOrThrow("date"))
                val timestampUtc = timestampLocal.timestampLocalToUTC()

                db.execSQL("UPDATE Messages SET date = $timestampUtc WHERE id = $id")
            }
        }
    }

    private fun migrateMobileDevices(db: SupportSQLiteDatabase) {
        db.query("SELECT * FROM MobileDevices").use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow("id"))
                val timestampLocal = it.getLong(it.getColumnIndexOrThrow("date"))
                val timestampUtc = timestampLocal.timestampLocalToUTC()

                db.execSQL("UPDATE MobileDevices SET date = $timestampUtc WHERE id = $id")
            }
        }
    }

    private fun migrateNotifications(db: SupportSQLiteDatabase) {
        db.query("SELECT * FROM Notifications").use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow("id"))
                val timestampLocal = it.getLong(it.getColumnIndexOrThrow("date"))
                val timestampUtc = timestampLocal.timestampLocalToUTC()

                db.execSQL("UPDATE Notifications SET date = $timestampUtc WHERE id = $id")
            }
        }
    }

    private fun migrateTimetable(db: SupportSQLiteDatabase) {
        db.query("SELECT * FROM Timetable").use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow("id"))
                val timestampLocalStart = it.getLong(it.getColumnIndexOrThrow("start"))
                val timestampLocalEnd = it.getLong(it.getColumnIndexOrThrow("end"))
                val timestampUtcStart = timestampLocalStart.timestampLocalToUTC()
                val timestampUtcEnd = timestampLocalEnd.timestampLocalToUTC()

                db.execSQL("UPDATE Timetable SET start = $timestampUtcStart, end = $timestampUtcEnd WHERE id = $id")
            }
        }
    }

    private fun migrateTimetableAdditional(db: SupportSQLiteDatabase) {
        db.query("SELECT * FROM TimetableAdditional").use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow("id"))
                val timestampLocalStart = it.getLong(it.getColumnIndexOrThrow("start"))
                val timestampLocalEnd = it.getLong(it.getColumnIndexOrThrow("end"))
                val timestampUtcStart = timestampLocalStart.timestampLocalToUTC()
                val timestampUtcEnd = timestampLocalEnd.timestampLocalToUTC()

                db.execSQL("UPDATE TimetableAdditional SET start = $timestampUtcStart, end = $timestampUtcEnd WHERE id = $id")
            }
        }
    }

    private fun Long.timestampLocalToUTC(): Long = Instant.ofEpochMilli(this)
        .atZone(ZoneOffset.UTC)
        .withZoneSameLocal(ZoneId.of("Europe/Warsaw"))
        .withZoneSameInstant(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}
