package tool.browser.fast.browser.history

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.BindingAdapter
import com.drake.brv.listener.DefaultItemTouchCallback
import com.drake.brv.utils.setup
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tool.browser.fast.R
import tool.browser.fast.ad.Cache
import tool.browser.fast.ad.admob.AdSpace
import tool.browser.fast.ad.admob.AdUtils
import tool.browser.fast.ad.manager.AdvertiseManager
import tool.browser.fast.browser.tab.manager.TabManager
import tool.browser.fast.component.BaseActivity
import tool.browser.fast.databinding.ActivityHistoryBinding
import tool.browser.fast.databinding.HistoryItem2Binding
import tool.browser.fast.utils.linear

class BrowserHistoryActivity : BaseActivity<ActivityHistoryBinding, HistoryModel>(
    layoutResId = R.layout.activity_history,
    viewModelClass = HistoryModel::class.java
) {
    private lateinit var adapter: BindingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLayoutBinding.run {
            btnBack.setOnClickListener {
                onBackPressed()
            }
            adapter = rv.linear().setup {
                addType<HistoryUiBean> {
                    if (isTitle) {
                        R.layout.history_item2
                    } else {
                        R.layout.history_item
                    }
                }
                onClick(R.id.item) {
                    Log.i("onSwiped", "click-->")
                    getModel<HistoryUiBean>().browserHistory?.let {
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
                        Log.i("onSwiped", "$position --${adapter.models?.size ?: 0}")
                        val data = adapter.getModel<HistoryUiBean>(position)
                        if (data.browserHistory != null) {
                            mModel.deleteHistoryData(data.browserHistory)
                            super.onSwiped(viewHolder, direction)
                            lifecycleScope.launch {
                                kotlin.runCatching {
                                    delay(300)
                                    handleTitle(position)
                                }
                            }
                        } else {
                            super.onSwiped(viewHolder, direction)
                        }
                    }
                })

            }
        }
        initView()
        initData()
    }

    private fun initView() {
        mLayoutBinding.run {
            etSearchContent.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    mModel.searchHistory(p0?.toString() ?: "")
                }
            })
        }
    }

    private fun initData() {
        AdUtils.loadAd(AdSpace.SPACE_DISCOVER_HISTORY)
        mModel.run {
            refreshHistoryListData()
            historyListData.observe(this@BrowserHistoryActivity) {
                adapter.models = it
            }
        }
    }

    private fun handleTitle(position: Int) {
        val size = adapter.models?.size ?: 0
        Log.i("handleTitle", "$position ---  $size")
        if (position > 0) {
            if (position < size - 1) {
                val nextData = adapter.getModel<HistoryUiBean>(position)
                if (adapter.getModel<HistoryUiBean>(position - 1).isTitle && nextData.isTitle) {
                    Log.i("handleTitle", "remove1--${position - 1}")
                    adapter.mutable.removeAt(position - 1)
                    adapter.notifyItemRemoved(position - 1)
                }
            } else {
                if (adapter.getModel<HistoryUiBean>(position - 1).isTitle) {
                    Log.i("handleTitle", "remove2--${position - 1}")
                    adapter.mutable.removeAt(position - 1)
                    adapter.notifyItemRemoved(position - 1)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshHomeNativeAd()
    }

    private fun refreshHomeNativeAd() {
        lifecycleScope.launch {
            delay(300L)
            if (!Cache.isAdUpperLimit() && mIsOnResume) {
                while (mIsOnResume) {
                    val view = AdUtils.getNativeView(AdSpace.SPACE_DISCOVER_HISTORY, layoutInflater, R.layout.ad_discover_history_layout)
                    view?.let {
                        mLayoutBinding.adParent.removeAllViews()
                        mLayoutBinding.adParent.addView(it)
                        AdUtils.loadAd(AdSpace.SPACE_DISCOVER_HISTORY)
                        return@launch
                    }
                    if (Cache.isAdUpperLimit()) {
                        break
                    }
                    delay(200)
                }
            }
        }
    }
}