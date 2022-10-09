package tool.browser.fast.browser.tab

import android.graphics.Bitmap
import android.text.TextUtils
import android.util.Log
import android.view.View
import tool.browser.fast.R
import tool.browser.fast.browser.di.ComponentProvider
import tool.browser.fast.browser.engine.IWebEngine
import tool.browser.fast.browser.tab.home.IHomePage
import tool.browser.fast.browser.tab.manager.TabManageActivity
import tool.browser.fast.component.BaseViewModel

const val HOME_PLACEHOLDER_URL = "file:///android_asset/discover_null_page.html"

class WebTabImpl : IWebTab {
    private var mTabHost: ITabHost? = null
    private var mWebEngine: IWebEngine? = null
    private var mHomePage: IHomePage? = null
    private var mIsHomePos: Boolean = true

    override fun goBack() {
        if (mWebEngine?.canGoBack() == true) {
            mWebEngine?.goBack()
        }
    }

    override fun goForward() {
        if (mIsHomePos) {
            if (mWebEngine?.canGoForward()==true){
                mWebEngine?.goForward()
                goWebResult()
            }
            return
        }
        mWebEngine?.goForward()
    }

    override fun goHome() {
        changeViewTo(true)
    }

    override fun goWebResult() {
        changeViewTo(false)
    }

    override fun refreshWeb() {
        mWebEngine?.refreshWeb()
    }

    override fun goTaskManager() {
        mTabHost?.startActivityForTab(
            BaseViewModel.StartActivityOption(
                target = TabManageActivity::class.java,
                enterAnimResId = R.anim.anim_enter,
                exitAnimResId = R.anim.anim_hold
            )
        )
    }

    override fun attach(host: ITabHost) {
        mTabHost = host
        mWebEngine = ComponentProvider.provideWebEngine()
            .apply {
                getView().onCreate(host)
            }
        mHomePage = ComponentProvider.provideHomePage()
            .apply {
                getView().onCreate(host)
            }
    }

    private fun changeViewTo(isHome: Boolean) {
        if (mIsHomePos == isHome) return
        mIsHomePos = isHome
        mTabHost?.onTabContentViewChanged(getCurView(), mIsHomePos)
    }

    override fun loadUrl(url: String) {
        ensureWebResult()
        mWebEngine?.loadUrl(url)
    }

    override fun searchContent(content: String) {
        ensureWebResult()
        mWebEngine?.searchContent(content)
    }

    private fun ensureWebResult() {
        if (mIsHomePos) {
            goWebResult()
        }
    }

    override fun snapshot(): Bitmap? {
        return getCurView()?.snapshot()
    }

    override fun getCurView(): IView? {
        return if (mIsHomePos) mHomePage?.getView() else mWebEngine?.getView()
    }

    override fun detach() {
        mTabHost = null
        mWebEngine?.getView()?.onDestroy()
        mHomePage?.getView()?.onDestroy()
    }

    override fun getLoadingUrl(): String? {
        return mWebEngine?.getLoadingUrl()
    }

    override fun onBackPressed(): Boolean {
        if (mWebEngine?.canGoBack() == true) {
            mWebEngine?.goBack()
            return true
        }
        if (!mIsHomePos) {
            goHome()
            return true
        }
        return false
    }

    override fun showMorePopupWindow(view: View) {
        mTabHost?.showMorePopupWindow(view, mIsHomePos)
    }

    override fun setWebLoadMode(isIncognitoMode: Boolean) {
        mWebEngine?.setWebLoadMode(isIncognitoMode)
    }

    override fun getTitle(): String? {
        return mWebEngine?.getTitle()
    }

}