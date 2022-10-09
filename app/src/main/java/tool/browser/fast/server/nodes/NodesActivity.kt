package tool.browser.fast.server.nodes

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tool.browser.fast.R
import tool.browser.fast.ad.manager.AdvertiseManager
import tool.browser.fast.ad.ui.FullScreenAdActivity
import tool.browser.fast.component.BindingRvAdapter
import tool.browser.fast.databinding.ActivityNodesBinding
import tool.browser.fast.server.ServerNode
import tool.browser.fast.server.ServersRepository
import tool.browser.fast.server.manager.ServerManager
import tool.browser.fast.utils.linear
import tool.browser.fast.utils.show

class NodesActivity : FullScreenAdActivity<ActivityNodesBinding, NodesViewModel>(
    layoutResId = R.layout.activity_nodes,
    viewModelClass = NodesViewModel::class.java
) {
    private var mAdapter: BindingRvAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope
            .launch(Dispatchers.Main) {
                withLayoutBinding {
                    mAdapter = rvNodes
                        .linear(R.layout.item_node)
                        .apply {
                            onItemSelected = {
                                (it as? ItemNode)?.let { node ->
                                    withLayoutBinding {
                                        btnConnect.show()
                                    }
                                    withViewModel {
                                        onNewNode(node.serverNode)
                                    }
                                }
                            }
                        }
                    this@NodesActivity.getModels()
                }
            }
    }

    private fun getModels() {
        lifecycleScope.launch(Dispatchers.IO) {
            val items = mutableListOf<ItemNode>()
            val node = ServerManager.get().getCurNode()
            with(listOf(ServerNode.fast) + ServersRepository.getAllNodes()) {
                forEach {
                    items.add(
                        ItemNode(
                            it
                        ).apply {
                            checked = it == node
                        }
                    )
                }
            }
            lifecycleScope.launch(Dispatchers.Main) {
                mAdapter?.models = items.toList()
            }
        }
    }

    override fun getAdSpace(): String {
        return AdvertiseManager.SPACE_BACK
    }

    override fun onBackPressed() {
        val ad = mAd
        if (ad?.isSuccessful == true) {
            showFullScreenAd {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }
}