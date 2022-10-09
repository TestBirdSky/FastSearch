package tool.browser.fast.browser.recentlyopenpage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.setup
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tool.browser.fast.R
import tool.browser.fast.ad.Cache
import tool.browser.fast.ad.admob.AdSpace
import tool.browser.fast.ad.admob.AdUtils
import tool.browser.fast.browser.history.HistoryUiBean
import tool.browser.fast.browser.tab.manager.TabManager
import tool.browser.fast.component.BaseActivity
import tool.browser.fast.databinding.ActivityRecentlyOpenBinding
import tool.browser.fast.utils.linear

class RecentlyOpenActivity : BaseActivity<ActivityRecentlyOpenBinding, RecentlyOpenModel>(
    layoutResId = R.layout.activity_recently_open,
    viewModelClass = RecentlyOpenModel::class.java
) {
    private lateinit var adapter: BindingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLayoutBinding.run {
            btnBack.setOnClickListener {
                onBackPressed()
            }
            adapter = rv.linear().setup {
                addType<RecentlyOpenUiBean> {
                    R.layout.recently_open_item
                }
                onClick(R.id.item) {
                    Log.i("onSwiped", "click-->")
                    getModel<RecentlyOpenUiBean>().recentlyOpenedDB.let {
                        it.url?.let { url ->
                            finish()
                            TabManager.addTabAndLoadTagUrl(url)
                        }
                    }
                }
            }
        }
        initView()
        initData()
    }

    private fun initView() {

    }

    private fun initData() {
        mModel.run {
            refreshData()
            recentlyData.observe(this@RecentlyOpenActivity) {
                adapter.models = it
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
                    val view = AdUtils.getNativeView(AdSpace.SPACE_DISCOVER_RE_OPEN, layoutInflater, R.layout.ad_discover_history_layout)
                    view?.let {
                        mLayoutBinding.adParent.removeAllViews()
                        mLayoutBinding.adParent.addView(it)
                        AdUtils.loadAd(AdSpace.SPACE_DISCOVER_RE_OPEN)
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