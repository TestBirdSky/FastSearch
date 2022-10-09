package tool.browser.fast.browser.readlistpage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.BindingAdapter
import com.drake.brv.listener.DefaultItemTouchCallback
import com.drake.brv.utils.setup
import tool.browser.fast.R
import tool.browser.fast.ad.admob.AdSpace
import tool.browser.fast.ad.admob.AdUtils
import tool.browser.fast.browser.tab.manager.TabManager
import tool.browser.fast.component.BaseActivity
import tool.browser.fast.databinding.ActivityReadListBinding
import tool.browser.fast.utils.linear

class ReadListActivity : BaseActivity<ActivityReadListBinding, ReadListModel>(R.layout.activity_read_list, ReadListModel::class.java) {
    private lateinit var adapter: BindingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLayoutBinding.run {
            btnBack.setOnClickListener {
                onBackPressed()
            }
            adapter = rv.linear().setup {
                addType<ReadListUiBean> {
                    R.layout.read_list_item
                }
                onClick(R.id.item) {
                    Log.i("onSwiped", "click-->")
                    getModel<ReadListUiBean>().let {
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
                        val data = adapter.getModel<ReadListUiBean>(position)
                        mModel.deleteData(data.readListDB)
                        super.onSwiped(viewHolder, direction)
                    }
                })

            }
        }
        initView()
        refreshData()
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
        mModel.readList.observe(this) {
            adapter.models = it
        }
        mModel.refreshData()
        AdUtils.loadAd(AdSpace.SPACE_BACK_READ_LIST)
    }

    override fun onBackPressed() {
        if (!AdUtils.showAd(AdSpace.SPACE_BACK_READ_LIST, this) {
                finish()
            }) {
            super.onBackPressed()
        }
    }
}