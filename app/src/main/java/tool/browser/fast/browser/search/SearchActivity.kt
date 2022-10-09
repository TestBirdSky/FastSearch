package tool.browser.fast.browser.search

import android.os.Bundle
import android.view.KeyEvent
import com.blankj.utilcode.util.KeyboardUtils
import tool.browser.fast.R
import tool.browser.fast.browser.bookmark.Bookmarks
import tool.browser.fast.browser.bookmark.ItemBookmark
import tool.browser.fast.component.BaseActivity
import tool.browser.fast.databinding.ActivitySearchBinding
import tool.browser.fast.utils.grid

class SearchActivity : BaseActivity<ActivitySearchBinding, SearchViewModel>(
    layoutResId = R.layout.activity_search,
    viewModelClass = SearchViewModel::class.java
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        withLayoutBinding {
            rvBookmarks
                .grid(
                    layoutResId = R.layout.item_bookmark,
                    row = 4
                )
                .apply {
                    onItemClick = {
                        this@SearchActivity.finish()
                        (it as? ItemBookmark)?.openBookmark()
                    }
                }
                .models = listOf(
                ItemBookmark(Bookmarks.Ins),
                ItemBookmark(Bookmarks.Twitter),
                ItemBookmark(Bookmarks.Tiktok),
                ItemBookmark(Bookmarks.Facebook),
            )
            etSearchContent.run {
                post {
                    KeyboardUtils.showSoftInput(this)
                }
            }
            etSearchContent.setOnKeyListener { v, ketCode, event ->
                if (ketCode==KeyEvent.KEYCODE_ENTER){
                    mModel.search(v)
                }
                false
            }
        }
        withViewModel {
            mDefSearchContent = this@SearchActivity.intent.getStringExtra("search_content")
            searchContent.postValue(mDefSearchContent)
            bindEt(mLayoutBinding.etSearchContent)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}