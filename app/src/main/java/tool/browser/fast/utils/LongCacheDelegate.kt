package tool.browser.fast.utils

import com.tencent.mmkv.MMKV
import kotlin.reflect.KProperty

class LongCacheDelegate(
    private val setReceiver: ((value: Long) -> Unit)? = null,
    private val cacheKey: () -> String
) {
    private val mMmKv by lazy {
        MMKV.defaultMMKV()
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Long {
        return mMmKv.decodeLong(generateConsKey(cacheKey()), 0L)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
        mMmKv.encode(generateConsKey(cacheKey()), value)
        setReceiver?.invoke(value)
    }
}