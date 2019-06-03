package io.github.wulkanowy.ui.modules.mobiledevice

import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import io.github.wulkanowy.data.db.entities.MobileDevice

class MobileDeviceAdapter<T : IFlexible<*>> : FlexibleAdapter<T>(null, null, true) {

    var onDeviceUnregisterListener: (MobileDevice, position: Int) -> Unit = {  _, _ -> }
}
