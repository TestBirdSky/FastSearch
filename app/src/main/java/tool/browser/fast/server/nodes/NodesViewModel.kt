package tool.browser.fast.server.nodes

import android.content.Intent
import android.view.View
import tool.browser.fast.component.BaseViewModel
import tool.browser.fast.main.MainViewModel
import tool.browser.fast.server.ServerNode
import tool.browser.fast.server.manager.ServerManager

class NodesViewModel : BaseViewModel() {
    private var newNode: ServerNode? = null

    fun onNewNode(serverNode: ServerNode) {
        newNode = serverNode
    }

    fun connect(view: View) {
        val selectedNode = newNode ?: return
        val isConnected = ServerManager.get().isConnected()
        val node = ServerManager.get().getCurNode()
        val action: String = if (newNode == node) {
            if (isConnected) {
                return
            }
            MainViewModel.ACTION_CONNECT
        } else {
            ServerManager.get().changeNode(selectedNode)
            if (isConnected) MainViewModel.ACTION_DISCONNECT else MainViewModel.ACTION_CONNECT
        }
        finishActivityForResult.postValue(
            FinishActivityForResult(
                requestCode = MainViewModel.REQUEST_CODE,
                data = Intent()
                    .apply {
                        putExtra(MainViewModel.RESULT_ACTION, action)
                    }
            )
        )
    }
}