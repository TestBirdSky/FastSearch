package tool.browser.fast.server.nodes

import tool.browser.fast.component.BindingRvAdapter
import tool.browser.fast.server.ServerNode

class ItemNode(
    val serverNode: ServerNode
) : BindingRvAdapter.CheckableItemModel()