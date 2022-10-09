package tool.browser.fast.utils

import com.tencent.mmkv.MMKV
import kotlin.reflect.KProperty

class StringCacheDelegate(
    private val setReceiver: ((value: String) -> Unit)? = null,
    private val def: String = "",
    private val cacheKey: () -> String
) {
    private val mMmKv by lazy {
        MMKV.defaultMMKV()
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return mMmKv.decodeString(generateConsKey(cacheKey()), def) ?: def
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        mMmKv.encode(generateConsKey(cacheKey()), value)
        setReceiver?.invoke(value)
    }
}