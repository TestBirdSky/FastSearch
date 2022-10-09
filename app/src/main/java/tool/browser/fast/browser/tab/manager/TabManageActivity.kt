package tool.browser.fast.browser.tab.manager

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tool.browser.fast.R
import tool.browser.fast.component.BaseActivity
import tool.browser.fast.databinding.ActivityTabManageBinding
import tool.browser.fast.utils.grid

class TabManageActivity : BaseActivity<ActivityTabManageBinding, TabManageViewModel>(
    layoutResId = R.layout.activity_tab_manage,
    viewModelClass = TabManageViewModel::class.java
) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        withLayoutBinding {
            lifecycleScope.launch(Dispatchers.Main) {
                rvTabManage
                    .grid(
                        layoutResId = R.layout.item_web_tab,
                        row = 2
                    )
                    .apply {
                        onBind = { model, holder ->
                            val itemTab = model as ItemTab
                            holder.itemView.findViewById<View>(R.id.btn_web_remove_tab)
                                ?.setOnClickListener {
                                    this.removeItem(model)
                                    TabManager.removeTab(itemTab.tab)
                                    if (this.models?.isEmpty() == true) {
                                        this@TabManageActivity.finish()
                                    }
                                }
                            itemTab.tab.snapshot()?.let { capture ->
                                holder.itemView.findViewById<ImageView>(R.id.iv_web_capture)
                                    ?.setImageBitmap(capture)
                            }
                        }
                        onItemClick = {
                            TabManager.changeToTab((it as ItemTab).tab)
                            this@TabManageActivity.finish()
                        }
                    }
                    .models = getModels()
            }
        }
    }

    private fun getModels(): List<ItemTab> {
        val models = mutableListOf<ItemTab>()
        TabManager.getAllTabs()
            .forEach {
                models.add(ItemTab(it))
            }
        return models
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.anim_exit)
    }
}