package io.github.wulkanowy.data.db.migrations

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec

@DeleteColumn(
    tableName = "AdminMessages",
    columnName = "type",
)
class Migration57 : AutoMigrationSpec
