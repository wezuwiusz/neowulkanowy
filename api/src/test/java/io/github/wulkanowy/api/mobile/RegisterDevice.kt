package io.github.wulkanowy.api.mobile

import io.github.wulkanowy.api.StudentAndParentTestCase
import org.junit.Assert.assertEquals
import org.junit.Test

class RegisterDeviceTest : StudentAndParentTestCase() {

    @Test
    fun getTokenTest() {
        val registration = RegisterDevice(getSnp("Rejestruj.html"))

        assertEquals("3S1A1B2C", registration.getToken().token)
        assertEquals("Default", registration.getToken().symbol)
        assertEquals("1234567", registration.getToken().pin)
    }
}
