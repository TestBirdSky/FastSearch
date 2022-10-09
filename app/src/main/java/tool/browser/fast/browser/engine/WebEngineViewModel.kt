package tool.browser.fast.browser.engine

import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tool.browser.fast.browser.tab.HOME_PLACEHOLDER_URL
import tool.browser.fast.browser.tab.home.HomePageViewModel
import tool.browser.fast.database.RecentlyOpenedDB
import tool.browser.fast.database.RoomHelper
import tool.browser.fast.utils.webIconUrlToLoadUrl

class WebEngineViewModel : HomePageViewModel() {
    val webLoadProgress = MutableLiveData(0)
    val webTitle = MutableLiveData<String>()
    private var recentlyOpenedDB: RecentlyOpenedDB? = null
    private val dao by lazy { RoomHelper.recentlyOpenedDao }
    private var curWebUrl: String = HOME_PLACEHOLDER_URL

    fun searchWithDef(view: View) {
        var curUrl = mCurTab?.getLoadingUrl()
        if (curUrl == HOME_PLACEHOLDER_URL) {
            curUrl = ""
        }
        startSearch(view, curUrl)
    }

    fun setCurWebUrl(url: String) {
        curWebUrl = url
        if (curWebUrl == HOME_PLACEHOLDER_URL) {
            mCurTab?.goHome()
        } else {
            mCurTab?.goWebResult()
        }
    }

    fun updateRecentlyOpen(url: String?, title: String?) {
        viewModelScope.launch {
            if (recentlyOpenedDB == null) {
                recentlyOpenedDB = RecentlyOpenedDB()
                recentlyOpenedDB?.run {
                    this.url = url
                    this.title = title
                    iconUrl = webIconUrlToLoadUrl(url)
                    host = url?.toUri()?.host
                    dao.add(this)
                    recentlyOpenedDB = dao.get(time)
                }
            } else {
                recentlyOpenedDB?.run {
                    this.url = url
                    this.title = title
                    iconUrl = webIconUrlToLoadUrl(url)
                    host = url?.toUri()?.host
                    dao.update(this)
                }
            }
        }
    }
}