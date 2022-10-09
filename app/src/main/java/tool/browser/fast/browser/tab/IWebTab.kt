package tool.browser.fast.browser.tab

import android.graphics.Bitmap
import android.view.View

interface IWebTab {
    fun attach(host: ITabHost)

    fun detach()

    fun goBack()

    fun goForward()

    fun goHome()

    fun goWebResult()

    fun refreshWeb()

    fun goTaskManager()

    fun loadUrl(url: String)

    fun searchContent(content: String)

    fun getCurView(): IView?

    fun snapshot(): Bitmap?

    fun getLoadingUrl(): String?

    fun onBackPressed(): Boolean

    fun showMorePopupWindow(view : View)

    fun setWebLoadMode(isIncognitoMode: Boolean)

    fun getTitle():String?
}