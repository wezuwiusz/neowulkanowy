package io.github.wulkanowy.utils

import java.net.CookiePolicy
import java.net.URI
import android.webkit.CookieManager as WebkitCookieManager
import java.net.CookieManager as JavaCookieManager

class WebkitCookieManagerProxy : JavaCookieManager(null, CookiePolicy.ACCEPT_ALL) {

    private val webkitCookieManager: WebkitCookieManager = WebkitCookieManager.getInstance()

    override fun put(uri: URI?, responseHeaders: Map<String?, List<String?>>?) {
        if (uri == null || responseHeaders == null) return
        val url = uri.toString()
        for (headerKey in responseHeaders.keys) {
            if (headerKey == null || !(
                    headerKey.equals("Set-Cookie2", ignoreCase = true) ||
                        headerKey.equals("Set-Cookie", ignoreCase = true)
                    )
            ) continue

            // process each of the headers
            for (headerValue in responseHeaders[headerKey].orEmpty()) {
                webkitCookieManager.setCookie(url, headerValue)
            }
        }
    }

    override operator fun get(
        uri: URI?,
        requestHeaders: Map<String?, List<String?>?>?
    ): Map<String, List<String>> {
        require(!(uri == null || requestHeaders == null)) { "Argument is null" }
        val res = mutableMapOf<String, List<String>>()
        val cookie = webkitCookieManager.getCookie(uri.toString())
        if (cookie != null) res["Cookie"] = listOf(cookie)
        return res
    }
}
