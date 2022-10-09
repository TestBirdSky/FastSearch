package tool.browser.fast.setting

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import tool.browser.fast.R
import tool.browser.fast.component.BaseActivity
import tool.browser.fast.databinding.SearchEngineLayoutBinding

class EngineSettingActivity : BaseActivity<SearchEngineLayoutBinding, SettingViewModel>(
    layoutResId = R.layout.search_engine_layout,
    viewModelClass = SettingViewModel::class.java
) {
    private val adapter = EngineAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        withLayoutBinding {
            rv.adapter = adapter
            adapter.itemClick = {
                finish()
            }
        }
    }
}