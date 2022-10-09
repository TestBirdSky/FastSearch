package tool.browser.fast.browser

import android.view.View
import androidx.lifecycle.MutableLiveData
import tool.browser.fast.browser.tab.HOME_PLACEHOLDER_URL
import tool.browser.fast.browser.tab.IView
import tool.browser.fast.browser.tab.IWebTab
import tool.browser.fast.browser.tab.manager.TabManager
import tool.browser.fast.component.BaseViewModel

class BrowserViewModel : BaseViewModel() {
    val curTab = MutableLiveData<IWebTab?>()
    val curTabView = MutableLiveData<IView?>()
    val curShowingHomePage = MutableLiveData(true)
    val totalTabCount = MutableLiveData<String>()

    private val mWebTab: IWebTab?
        get() {
            return curTab.value
        }

    fun goBack(view: View) {
        mWebTab?.goBack()
    }

    fun goForward(view: View) {
        mWebTab?.goForward()
    }

    fun goHome(view: View) {
        mWebTab?.loadUrl(HOME_PLACEHOLDER_URL)
        mWebTab?.goHome()
    }

    fun goTabManager(view: View) {
        mWebTab?.goTaskManager()
    }

    //add by skyHuang
    fun showMore(view: View) {
        mWebTab?.showMorePopupWindow(view)
    }
}