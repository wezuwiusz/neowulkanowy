package io.github.wulkanowy.data.db.adapters

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

object PairAdapterFactory : JsonAdapter.Factory {

    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (type !is ParameterizedType || List::class.java != type.rawType) return null
        if (type.actualTypeArguments[0] != Pair::class.java) return null

        val listType = Types.newParameterizedType(List::class.java, Map::class.java, String::class.java)
        val listAdapter = moshi.adapter<List<Map<String, String>>>(listType)

        val mapType = Types.newParameterizedType(MutableMap::class.java, String::class.java, String::class.java)
        val mapAdapter = moshi.adapter<Map<String, String>>(mapType)

        return PairAdapter(listAdapter, mapAdapter)
    }

    private class PairAdapter(
        private val listAdapter: JsonAdapter<List<Map<String, String>>>,
        private val mapAdapter: JsonAdapter<Map<String, String>>,
    ) : JsonAdapter<List<Pair<String, String>>>() {

        override fun toJson(writer: JsonWriter, value: List<Pair<String, String>>?) {
            writer.beginArray()
            value?.forEach {
                writer.beginObject()
                writer.name("first").value(it.first)
                writer.name("second").value(it.second)
                writer.endObject()
            }
            writer.endArray()
        }

        override fun fromJson(reader: JsonReader): List<Pair<String, String>>? {
            return if (reader.peek() == JsonReader.Token.BEGIN_OBJECT) deserializeMoshiMap(reader)
            else deserializeGsonPair(reader)
        }

        // for compatibility with 0.21.0
        private fun deserializeMoshiMap(reader: JsonReader): List<Pair<String, String>>? {
            val map = mapAdapter.fromJson(reader) ?: return null

            return map.entries.map {
                it.key to it.value
            }
        }

        private fun deserializeGsonPair(reader: JsonReader): List<Pair<String, String>>? {
            val list = listAdapter.fromJson(reader) ?: return null

            require(list.size == 2 || list.isEmpty()) {
                "pair with more or less than two elements: $list"
            }

            return list.map {
                it["first"].orEmpty() to it["second"].orEmpty()
            }
        }
    }
}
