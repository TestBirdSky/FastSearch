package tool.browser.fast.utils

import org.json.JSONArray
import org.json.JSONObject

fun String.toJsonObjectOrNull(): JSONObject? {
    return kotlin.runCatching {
        JSONObject(this)
    }.getOrNull()
}

fun <T> JSONArray?.optListObj(
    deserializer: (obj: JSONObject) -> T
): List<T> {
    return optList { index, array ->
        deserializer(array.optJSONObject(index))
    }
}

fun JSONArray?.optListString(): List<String> {
    return optList { index, array ->
        array.optString(index)
    }
}

fun <T> JSONArray?.optList(
    receiver: (index: Int, array: JSONArray) -> T?
): List<T> {
    this ?: return emptyList()
    val lis = mutableListOf<T>()
    for (i in 0 until length()) {
        lis.addIfNotNull(receiver(i, this))
    }
    return lis.toList()
}