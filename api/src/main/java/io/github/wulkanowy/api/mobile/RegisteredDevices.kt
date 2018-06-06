package io.github.wulkanowy.api.mobile

import io.github.wulkanowy.api.SnP
import io.github.wulkanowy.api.getFormattedDate

class RegisteredDevices(private val snp: SnP) {

    companion object {
        const val DEVICES_LIST_URL = "DostepMobilny.mvc"
    }

    data class Device(
            val name: String,
            val system: String,
            val date: String,
            val id: Int
    )

    fun getList(): List<Device> {
        val items = snp.getSnPPageDocument(DEVICES_LIST_URL).select("table tbody tr")
        val devices: MutableList<Device> = mutableListOf()

        for (item in items) {
            val cells = item.select("td")
            val system = cells[0].text().split("(").last().removeSuffix(")")

            devices.add(Device(
                    cells[0].text().replace(" ($system)", ""),
                    system,
                    getFormattedDate(cells[1].text(), "dd.MM.yyyy 'godz:' HH:mm:ss", "yyyy-MM-dd HH:mm:ss"),
                    cells[2].select("a").attr("href")
                            .split("/").last().toInt()
            ))
        }

        return devices
    }
}
