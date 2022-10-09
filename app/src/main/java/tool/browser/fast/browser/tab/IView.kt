package tool.browser.fast.browser.tab

import android.graphics.Bitmap
import android.view.View

interface IView {
    fun onCreate(host: ITabHost) {}
    fun getRoot(): View? = null
    fun onDestroy()
    fun snapshot(): Bitmap? = null
}