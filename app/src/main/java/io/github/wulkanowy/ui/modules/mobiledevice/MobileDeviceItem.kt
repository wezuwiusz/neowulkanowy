package io.github.wulkanowy.ui.modules.mobiledevice

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_mobile_device.*

class MobileDeviceItem(val device: MobileDevice) : AbstractFlexibleItem<MobileDeviceItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_mobile_device

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.apply {
            mobileDeviceItemDate.text = device.date.toFormattedString("dd.MM.yyyy HH:mm:ss")
            mobileDeviceItemName.text = device.name
            mobileDeviceItemUnregister.setOnClickListener {
                (adapter as MobileDeviceAdapter).onDeviceUnregisterListener(device, holder.flexibleAdapterPosition)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MobileDeviceItem

        if (device.id != other.device.id) return false
        return true
    }

    override fun hashCode(): Int {
        var result = device.hashCode()
        result = 31 * result + device.id.toInt()
        return result
    }

    class ViewHolder(val view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter), LayoutContainer {

        override val containerView: View
            get() = contentView
    }
}
