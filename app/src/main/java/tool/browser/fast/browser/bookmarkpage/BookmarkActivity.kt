package tool.browser.fast.browser.bookmarkpage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.BindingAdapter
import com.drake.brv.listener.DefaultItemTouchCallback
import com.drake.brv.utils.setup
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tool.browser.fast.R
import tool.browser.fast.ad.admob.AdSpace
import tool.browser.fast.ad.admob.AdUtils
import tool.browser.fast.browser.history.HistoryModel
import tool.browser.fast.browser.history.HistoryUiBean
import tool.browser.fast.browser.tab.manager.TabManager
import tool.browser.fast.component.BaseActivity
import tool.browser.fast.databinding.ActivityBookmarkBinding
import tool.browser.fast.utils.linear

class BookmarkActivity : BaseActivity<ActivityBookmarkBinding, BookmarkModel>(
    layoutResId = R.layout.activity_bookmark,
    viewModelClass = BookmarkModel::class.java
) {
    private lateinit var adapter: BindingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLayoutBinding.run {
            btnBack.setOnClickListener {
                onBackPressed()
            }
            adapter = rv.linear().setup {
                addType<BookmarkUiBean> {
                    R.layout.bookmark_item
                }
                onClick(R.id.item) {
                    Log.i("onSwiped", "click-->")
                    getModel<BookmarkUiBean>().let {
                        it.url?.let { url ->
                            finish()
                            TabManager.loadUrl(url)
                        }
                    }
                }
                itemTouchHelper = ItemTouchHelper(object : DefaultItemTouchCallback() {
                    // 这里可以重写函数
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        // 这是侧滑删除后回调, 这里可以同步服务器
                        val position = viewHolder.layoutPosition
                        val data = adapter.getModel<BookmarkUiBean>(position)
                        mModel.deleteData(data.bookmarkDB)
                        super.onSwiped(viewHolder, direction)
                    }
                })

            }
        }
        initView()
        refreshData()
        AdUtils.loadAd(AdSpace.SPACE_BACK_BOOKMARK)
    }

    private fun initView() {
        mLayoutBinding.run {
            etSearchContent.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    mModel.search(p0?.toString() ?: "")
                }
            })
        }
    }

    private fun refreshData() {
        mModel.bookmarkList.observe(this) {
            adapter.models = it
        }
        mModel.refresh()
    }

    override fun onBackPressed() {
        if (!AdUtils.showAd(AdSpace.SPACE_BACK_BOOKMARK, this) {
                finish()
            }) {
            super.onBackPressed()
        }
    }
}