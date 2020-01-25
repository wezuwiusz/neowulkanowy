package io.github.wulkanowy.ui.modules.attendance

import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import io.github.wulkanowy.data.db.entities.Attendance

class AttendanceAdapter<T : IFlexible<*>> : FlexibleAdapter<T>(null, null, true) {

    var excuseActionMode: Boolean = false

    var onExcuseCheckboxSelect: (attendanceItem: Attendance, checked: Boolean) -> Unit = { _, _ -> }
}
