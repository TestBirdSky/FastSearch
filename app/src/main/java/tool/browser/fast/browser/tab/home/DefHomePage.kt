package tool.browser.fast.browser.tab.home

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import tool.browser.fast.R
import tool.browser.fast.browser.bookmark.Bookmarks
import tool.browser.fast.browser.bookmark.ItemBookmark
import tool.browser.fast.browser.bookmarkpage.BookmarkActivity
import tool.browser.fast.browser.history.BrowserHistoryActivity
import tool.browser.fast.browser.readlistpage.ReadListActivity
import tool.browser.fast.browser.recentlyopenpage.RecentlyOpenActivity
import tool.browser.fast.browser.search.SearchActivity
import tool.browser.fast.browser.tab.ITabHost
import tool.browser.fast.browser.tab.IView
import tool.browser.fast.browser.view.BaseContentView
import tool.browser.fast.component.BaseViewModel
import tool.browser.fast.databinding.BrowserTabHomeBinding
import tool.browser.fast.utils.grid

class DefHomePage : BaseContentView<BrowserTabHomeBinding, HomePageViewModel>(
    layoutResId = R.layout.browser_tab_home,
    viewModelClass = HomePageViewModel::class.java
), IHomePage {

    override fun getView(): IView {
        return this
    }

    override fun onCreate(host: ITabHost) {
        super.onCreate(host)
        withLayoutBinding {
            rvBookmarks
                .grid(
                    layoutResId = R.layout.item_bookmark,
                    row = 4
                )
                .apply {
                    onItemClick = {
                        (it as? ItemBookmark)?.run {
                            if (bookmark.isRecommendApp) {
                                openBookmark()
                            } else {
                                when (bookmark.name) {
                                    Bookmarks.BookMark.name -> {
                                        mModel.startActivity(
                                            this@DefHomePage.getRoot(), BaseViewModel.StartActivityOption(
                                                target = BookmarkActivity::class.java,
                                            )
                                        )
                                    }
                                    Bookmarks.ReadList.name -> {
                                        mModel.startActivity(
                                            this@DefHomePage.getRoot(), BaseViewModel.StartActivityOption(
                                                target = ReadListActivity::class.java,
                                            )
                                        )
                                    }
                                    Bookmarks.RecentlyOpened.name -> {
                                        mModel.startActivity(
                                            this@DefHomePage.getRoot(), BaseViewModel.StartActivityOption(
                                                target = RecentlyOpenActivity::class.java,
                                            )
                                        )
                                    }
                                    Bookmarks.History.name -> {
                                        mModel.startActivity(
                                            this@DefHomePage.getRoot(), BaseViewModel.StartActivityOption(
                                                target = BrowserHistoryActivity::class.java,
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                    onItemLongClick = { itemModel, view ->
                        (itemModel as? ItemBookmark)?.showControlDialog(view)
                    }
                }
                .models = listOf(
                ItemBookmark(Bookmarks.Ins),
                ItemBookmark(Bookmarks.Twitter),
                ItemBookmark(Bookmarks.Tiktok),
                ItemBookmark(Bookmarks.Facebook),
                ItemBookmark(Bookmarks.BookMark),
                ItemBookmark(Bookmarks.ReadList),
                ItemBookmark(Bookmarks.RecentlyOpened),
                ItemBookmark(Bookmarks.History),
            )
        }
    }
}