package io.github.wulkanowy.data.db.dao.migrations;

import org.greenrobot.greendao.database.Database;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.data.db.dao.DbHelper;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;

public class Migration26 implements DbHelper.Migration {

    @Override
    public Integer getVersion() {
        return 26;
    }

    @Override
    public void runMigration(final Database db, final SharedPrefContract sharedPref, final Vulcan vulcan) throws Exception {
        throw new Exception("No migrations");
    }
}
