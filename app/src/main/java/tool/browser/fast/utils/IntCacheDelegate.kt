package tool.browser.fast.utils

import com.tencent.mmkv.MMKV
import kotlin.reflect.KProperty

class IntCacheDelegate(
    private val setReceiver: ((value: Int) -> Unit)? = null,
    private val def: Int = 0,
    private val cacheKey: () -> String
) {
    private val mMmKv by lazy {
        MMKV.defaultMMKV()
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return mMmKv.decodeInt(generateConsKey(cacheKey()), def)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        mMmKv.encode(generateConsKey(cacheKey()), value)
        setReceiver?.invoke(value)
    }
}