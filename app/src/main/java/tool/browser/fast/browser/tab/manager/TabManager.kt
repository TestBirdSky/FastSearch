package tool.browser.fast.browser.tab.manager

import androidx.annotation.MainThread
import androidx.core.net.toUri
import com.blankj.utilcode.util.ToastUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tool.browser.fast.browser.di.ComponentProvider
import tool.browser.fast.browser.tab.ITabHost
import tool.browser.fast.browser.tab.IWebTab
import tool.browser.fast.database.BookmarkDB
import tool.browser.fast.database.ReadListDB
import tool.browser.fast.database.RoomHelper
import tool.browser.fast.utils.webIconUrlToLoadUrl

@MainThread
object TabManager : ITabManager {
    private val mWebTabs = mutableListOf<IWebTab>()
    private var mHost: ITabHost? = null
    var mForegroundTab: IWebTab? = null
        private set

    fun attach(host: ITabHost) {
        mHost = host
        ensureTab()
    }

    private fun ensureTab(): Boolean {
        if (mWebTabs.isEmpty()) {
            addTab()
            return true
        }
        return false
    }

    override fun addTab() {
        val host = mHost ?: return
        val newTab = ComponentProvider.provideWebTab()
        newTab.attach(host)
        mWebTabs.add(newTab)
        changeToTab(newTab)
        tabCountChanged()
    }

    override fun addIncognitoTab() {
        val host = mHost ?: return
        val newTab = ComponentProvider.provideWebTab()
        newTab.attach(host)
        newTab.setWebLoadMode(true)
        mWebTabs.add(newTab)
        changeToTab(newTab)
        tabCountChanged()
    }

    override fun addBookmark() {
        val webUrl = mForegroundTab?.getLoadingUrl()
        if (webUrl != null) {
            if (RoomHelper.bookmarkDao.searchByUrl(webUrl) == null) {
                RoomHelper.bookmarkDao.add(BookmarkDB().apply {
                    title = mForegroundTab?.getTitle()
                    url = webUrl
                    host = webUrl.toUri().host
                    iconUrl = webIconUrlToLoadUrl(webUrl)
                })
                ToastUtils.showShort("Add successful")
            }
        }
    }

    override fun addReadLater() {
        val webUrl = mForegroundTab?.getLoadingUrl()
        if (webUrl != null) {
            if (RoomHelper.readListDao.searchByUrl(webUrl) == null) {
                RoomHelper.readListDao.add(ReadListDB().apply {
                    title = mForegroundTab?.getTitle()
                    url = webUrl
                    host = webUrl.toUri().host
                    iconUrl = webIconUrlToLoadUrl(webUrl)
                })
                ToastUtils.showShort("Add successful")
            }
        }
    }

    fun addTabAndLoadTagUrl(url: String) {
        addTab()
        CoroutineScope(Dispatchers.Main).launch {
            delay(100)
            mForegroundTab?.loadUrl(url)
        }
    }

    fun addTabAndLoadTagUrlWithIncognitoMode(url: String) {
        addIncognitoTab()
        CoroutineScope(Dispatchers.Main).launch {
            delay(100)
            mForegroundTab?.loadUrl(url)
        }
    }

    override fun reload() {
        mForegroundTab?.refreshWeb()
    }

    fun loadUrl(url: String) {
        mForegroundTab?.loadUrl(url)
    }

    override fun changeToTab(to: IWebTab) {
        mForegroundTab = to
        mHost?.onTabChanged(to)
    }

    override fun removeTab(removed: IWebTab) {
        if (!mWebTabs.contains(removed)) return
        removed.detach()
        mWebTabs.remove(removed)
        if (removed == mForegroundTab) {
            if (!ensureTab()) {
                changeToTab(mWebTabs.first())
            }
        }
        tabCountChanged()
    }

    private fun tabCountChanged() {
        mHost?.onTabCountChanged(mWebTabs.size)
    }

    fun detach() {
        mHost = null
        mWebTabs.forEach {
            it.detach()
        }
        mWebTabs.clear()
    }

    fun getAllTabs(): List<IWebTab> {
        return mWebTabs.toList()
    }
}