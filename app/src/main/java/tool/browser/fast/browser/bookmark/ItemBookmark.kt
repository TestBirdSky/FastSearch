package tool.browser.fast.browser.bookmark

import android.view.Gravity
import android.view.View
import com.lxj.xpopup.XPopup
import tool.browser.fast.R
import tool.browser.fast.browser.tab.manager.TabManager
import tool.browser.fast.component.BindingRvAdapter
import tool.browser.fast.utils.copyText
import tool.browser.fast.utils.createShareIntent
import tool.browser.fast.utils.shareText

class ItemBookmark(
    val bookmark: Bookmark
) : BindingRvAdapter.BaseItemModel() {
    fun openBookmark() {
        val url = bookmark.url ?: return
        TabManager.mForegroundTab?.loadUrl(url)
    }

    fun showControlDialog(view: View) {
        if (bookmark.isRecommendApp) {
            XPopup.Builder(view.context).apply {
            }.atView(view).asAttachList(
                arrayOf<String>("Open new tab", "Open in incognito mode", "Copy Link", "Share"),
                intArrayOf(R.drawable.ic_more2, R.drawable.ic_more3, R.drawable.ic_copy_link, R.drawable.ic_share),
                { position, text ->
                    when (position) {
                        0 -> {
                            bookmark.url?.let { TabManager.addTabAndLoadTagUrl(it) }
                        }
                        1 -> {
                            bookmark.url?.let { TabManager.addTabAndLoadTagUrlWithIncognitoMode(it) }
                        }
                        2 -> {
                            bookmark.url?.let { view.context.copyText(it) }
                        }
                        3 -> {
                            bookmark.url?.let { view.context.shareText(it) }
                        }
                    }
                },
                0, 0, Gravity.START,
            ).show()
        }
    }
}