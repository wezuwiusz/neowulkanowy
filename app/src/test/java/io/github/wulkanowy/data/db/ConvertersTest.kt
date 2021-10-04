package io.github.wulkanowy.data.db

import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersTest {

    @Test
    fun stringPairListToJson() {
        assertEquals(Converters().stringPairListToJson(listOf("aaa" to "bbb", "ccc" to "ddd", "eee" to "fff")), "[{\"first\":\"aaa\",\"second\":\"bbb\"},{\"first\":\"ccc\",\"second\":\"ddd\"},{\"first\":\"eee\",\"second\":\"fff\"}]")
        assertEquals(Converters().stringPairListToJson(listOf("aaa" to "bbb", "ccc" to "ddd")), "[{\"first\":\"aaa\",\"second\":\"bbb\"},{\"first\":\"ccc\",\"second\":\"ddd\"}]")
        assertEquals(Converters().stringPairListToJson(listOf("aaa" to "bbb")), "[{\"first\":\"aaa\",\"second\":\"bbb\"}]")
        assertEquals(Converters().stringPairListToJson(listOf()), "[]")
    }

    @Test
    fun jsonToStringPairList() {
        assertEquals(Converters().jsonToStringPairList("[{\"first\":\"aaa\",\"second\":\"bbb\"},{\"first\":\"ccc\",\"second\":\"ddd\"},{\"first\":\"eee\",\"second\":\"fff\"}]"), listOf("aaa" to "bbb", "ccc" to "ddd", "eee" to "fff"))
        assertEquals(Converters().jsonToStringPairList("[{\"first\":\"aaa\",\"second\":\"bbb\"},{\"first\":\"ccc\",\"second\":\"ddd\"}]"), listOf("aaa" to "bbb", "ccc" to "ddd"))
        assertEquals(Converters().jsonToStringPairList("[{\"first\":\"aaa\",\"second\":\"bbb\"}]"), listOf("aaa" to "bbb"))
        assertEquals(Converters().jsonToStringPairList("[]"), listOf<Pair<String, String>>())
    }

    @Test
    fun jsonToStringPairList_0210() {
        assertEquals(Converters().jsonToStringPairList("{\"aaa\":\"bbb\",\"ccc\":\"ddd\"}"), listOf<Pair<String, String>>())
        assertEquals(Converters().jsonToStringPairList("{\"aaa\":\"bbb\"}"), listOf<Pair<String, String>>())
        assertEquals(Converters().jsonToStringPairList("{}"), listOf<Pair<String, String>>())
    }
}
