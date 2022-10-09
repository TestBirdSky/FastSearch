package tool.browser.fast.utils

import androidx.annotation.LayoutRes
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.BindingAdapter
import com.drake.brv.layoutmanager.HoverLinearLayoutManager
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import tool.browser.fast.component.BindingRvAdapter
import java.lang.reflect.Modifier

fun RecyclerView.linear(
    @LayoutRes layoutResId: Int
): BindingRvAdapter {
    ensureAdapter(layoutResId, this)
    layoutManager = LinearLayoutManager(context)
    return adapter as BindingRvAdapter
}

fun RecyclerView.grid(
    @LayoutRes layoutResId: Int,
    row: Int
): BindingRvAdapter {
    ensureAdapter(layoutResId, this)
    layoutManager = GridLayoutManager(context, row)
    return adapter as BindingRvAdapter
}

private fun ensureAdapter(@LayoutRes layoutResId: Int, rv: RecyclerView): BindingRvAdapter {
    return (rv.adapter as? BindingRvAdapter) ?: BindingRvAdapter(
        context = rv.context,
        lifecycleOwner = rv.context as LifecycleOwner,
        layoutResId = layoutResId
    ).apply {
        rv.adapter = this
    }
}

fun RecyclerView.linear(
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
    scrollEnabled: Boolean = true,
    stackFromEnd: Boolean = false
): RecyclerView {
    layoutManager = HoverLinearLayoutManager(context, orientation, reverseLayout).apply {
        setScrollEnabled(scrollEnabled)
        this.stackFromEnd = stackFromEnd
    }
    return this
}

fun RecyclerView.setup(block: BindingAdapter.(RecyclerView) -> Unit): BindingAdapter {
    val adapter = BindingAdapter()
    adapter.block(this)
    this.adapter = adapter
    return adapter
}

inline fun <reified M> addType(@LayoutRes layout: Int) {
    if (Modifier.isInterface(M::class.java.modifiers)) {
        M::class.java.addInterfaceType { layout }
    } else {
        typePool[M::class.java] = { layout }
    }
}

/** 类型池 */
val typePool = mutableMapOf<Class<*>, Any.(Int) -> Int>()
var interfacePool: MutableMap<Class<*>, Any.(Int) -> Int>? = null

/**
 * 接口类型, 即类型必须为接口, 同时其子类都会被认为属于该接口而对应其布局
 * @receiver 接口类
 * @see addType
 */
fun Class<*>.addInterfaceType(block: Any.(Int) -> Int) {
    (interfacePool ?: mutableMapOf<Class<*>, Any.(Int) -> Int>().also {
        interfacePool = it
    })[this] = block
}