package io.github.wulkanowy.data.db.dao.migrations;

import org.greenrobot.greendao.database.Database;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.data.db.dao.DbHelper;
import io.github.wulkanowy.data.db.dao.entities.ExamDao;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;

public class Migration27 implements DbHelper.Migration {

    @Override
    public Integer getVersion() {
        return 27;
    }

    @Override
    public void runMigration(Database db, SharedPrefContract sharedPref, Vulcan vulcan) {
        ExamDao.dropTable(db, true);
        ExamDao.createTable(db, true);

        db.execSQL("UPDATE Weeks SET exams_synced = 0");
    }
}
