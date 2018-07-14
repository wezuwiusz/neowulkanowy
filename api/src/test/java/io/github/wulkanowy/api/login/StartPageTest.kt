package io.github.wulkanowy.api.login

import io.github.wulkanowy.api.Client
import io.github.wulkanowy.api.FixtureHelper
import io.github.wulkanowy.api.VulcanException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

class StartPageTest {

    private val client: Client = mock(Client::class.java)

    @Before fun setUp() {
        Mockito.`when`(client.host).thenReturn("fakelog.cf")
    }

    private fun getDoc(name: String): Document = Jsoup.parse(FixtureHelper.getAsString(javaClass.getResourceAsStream(name)))

    @Test fun getSchoolTest() {
        assertEquals("534213", StartPage(client).getSchools(getDoc("../Start-std.html"))[0].id)
    }

    @Test fun getMultiSchoolTest() {
        val schools = StartPage(client).getSchools(getDoc("../Start-multi.html"))

        assertEquals("123456", schools[0].id)
        assertEquals("123457", schools[1].id)
    }

    @Test fun getSchoolNameTest() {
        assertEquals("Uczeń", StartPage(client).getSchools(getDoc("../Start-std.html"))[0].name)
    }

    @Test fun getMultiSchoolNameTest() {
        val schools = StartPage(client).getSchools(getDoc("../Start-multi.html"))

        assertEquals("GIMBB", schools[0].name)
        assertEquals("SPBB", schools[1].name)
    }

    @Test(expected = VulcanException::class)
    fun getSnpPageUrlWithWrongPage() {
        StartPage(client).getSchools(getDoc("../OcenyWszystkie-semester.html"))
    }

    @Test
    fun getExtractedIDStandardTest() {
        assertEquals("123456", StartPage(client)
                .getExtractedIdFromUrl("https://uonetplus-opiekun.fakelog.cf/powiat/123456/Start/Index/"))
    }

    @Test
    fun getExtractedIDDemoTest() {
        assertEquals("demo12345", StartPage(client)
                .getExtractedIdFromUrl("https://uonetplus-opiekun.fakelog.cf/demoupowiat/demo12345/Start/Index/"))
    }

    @Test(expected = VulcanException::class)
    fun getExtractedIDNotLoggedTest() {
        assertEquals("123", StartPage(client)
                .getExtractedIdFromUrl("https://uonetplus.NOTfakelog.cf/powiat/"))
    }
}
