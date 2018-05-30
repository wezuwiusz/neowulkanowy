package io.github.wulkanowy.api.mobile

import io.github.wulkanowy.api.StudentAndParentTestCase
import org.junit.Assert.assertEquals
import org.junit.Test

class RegisteredDevicesListTest : StudentAndParentTestCase() {

    private val filled = RegisteredDevices(getSnp("DostepMobilny-filled.html"))

    @Test
    fun getListTest() {
        assertEquals(2, filled.getList().size)
    }

    @Test
    fun getNameTest() {
        assertEquals("google Android SDK built for x86", filled.getList()[0].name)
        assertEquals("google (Android SDK) built for x86", filled.getList()[1].name)
    }

    @Test
    fun getSystemTest() {
        assertEquals("Android 8.1.0", filled.getList()[0].system)
        assertEquals("Android 8.1.0", filled.getList()[1].system)
    }

    @Test
    fun getDateTest() {
        assertEquals("2018-01-20 22:35:30", filled.getList()[0].date)
    }

    @Test
    fun getIdTest() {
        assertEquals(321, filled.getList()[0].id)
    }
}
