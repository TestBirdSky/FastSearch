package tool.browser.fast.browser.engine

import android.graphics.Bitmap
import tool.browser.fast.browser.tab.IView

interface IWebEngine {
    fun getView(): IView

    fun goBack()

    fun goForward()

    fun canGoBack(): Boolean

    fun canGoForward(): Boolean

    fun loadUrl(url: String)

    fun refreshWeb()

    fun hasLoadHistory(): Boolean

    fun getWebCapture(): Bitmap?

    fun searchContent(content: String)

    fun getLoadingUrl(): String?

    fun setWebLoadMode(isIncognitoMode: Boolean)

    fun getTitle(): String?
}