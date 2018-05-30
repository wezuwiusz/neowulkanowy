package io.github.wulkanowy.api.mobile

import io.github.wulkanowy.api.SnP
import org.jsoup.nodes.Element

class RegisterDevice(private val snp: SnP) {

    companion object {
        const val REGISTER_URL = "DostepMobilny.mvc/Rejestruj"
    }

    data class Token(
            val token: String,
            val symbol: String,
            val pin: String
    )

    fun getToken(): Token {
        val form = snp.getSnPPageDocument(REGISTER_URL).selectFirst("#rejestracja-formularz")

        val fields = form.select(".blockElement")

        return Token(
                getValue(fields[1]),
                getValue(fields[2]),
                getValue(fields[3])
        )
    }

    fun getValue(e: Element): String {
        return e.text().split(":")[1].trim()
    }
}
