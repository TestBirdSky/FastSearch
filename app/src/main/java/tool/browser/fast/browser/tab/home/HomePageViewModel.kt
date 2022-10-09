package tool.browser.fast.browser.tab.home

import android.os.Bundle
import android.view.View
import tool.browser.fast.browser.search.SearchActivity
import tool.browser.fast.browser.tab.HOME_PLACEHOLDER_URL
import tool.browser.fast.browser.tab.IWebTab
import tool.browser.fast.browser.tab.manager.TabManager
import tool.browser.fast.component.BaseViewModel

open class HomePageViewModel : BaseViewModel() {
    protected val mCurTab: IWebTab?
        get() {
            return TabManager.mForegroundTab
        }
    private val mStartSearchOption by lazy {
        StartActivityOption(
            target = SearchActivity::class.java,
            bundle = Bundle(),
            enterAnimResId = 0,
            exitAnimResId = 0
        )
    }

    fun goHome(view: View) {
        mCurTab?.loadUrl(HOME_PLACEHOLDER_URL)
        mCurTab?.goHome()
    }

    fun refreshWeb(view: View) {
        mCurTab?.refreshWeb()
    }

    fun search(view: View) {
        startSearch(view)
    }

    protected fun startSearch(view: View, search: String? = null) {
        startActivity(view, mStartSearchOption.apply {
            bundle?.let {
                it.clear()
                it.putString("search_content", search)
            }
        })
    }
}