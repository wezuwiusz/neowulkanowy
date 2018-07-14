package io.github.wulkanowy.api.login

import io.github.wulkanowy.api.Client
import io.github.wulkanowy.api.VulcanException
import io.github.wulkanowy.api.generic.School
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory

class StartPage(val client: Client) {

    private val logger = LoggerFactory.getLogger(StartPage::class.java)

    fun getSchools(startPage: Document): MutableList<School> {
        val schoolList = mutableListOf<School>()

        val snpLinks = startPage.select(".panel.linkownia.pracownik.klient a")

        logger.debug("SnP links: {}", snpLinks.size)

        if (snpLinks.isEmpty()) {
            throw VulcanException("Na pewno używasz konta z dostępem do Witryny ucznia i rodzica?")
        }

        snpLinks.map {
            schoolList.add(School(
                    it.text(),
                    getExtractedIdFromUrl(it.attr("href")),
                    it == snpLinks.first()
            ))
        }

        return schoolList
    }

    internal fun getExtractedIdFromUrl(snpPageUrl: String): String {
        val path = snpPageUrl.split(client.host).getOrNull(1)?.split("/")

        if (6 != path?.size) {
            logger.error("Expected snp url, got {}", snpPageUrl)
            throw VulcanException("Na pewno używasz konta z dostępem do Witryny ucznia i rodzica?")
        }

        return path[2]
    }
}
