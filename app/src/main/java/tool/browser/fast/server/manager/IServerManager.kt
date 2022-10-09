package tool.browser.fast.server.manager

import android.app.Application
import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.WorkerThread
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import tool.browser.fast.server.ServerNode
import tool.browser.fast.server.State

interface IServerManager {
    fun initialize(app: Application)

    fun attach(context: Context, lifecycleOwner: LifecycleOwner)

    fun detach()

    fun changeState(state: State)

    fun changeNode(newNode: ServerNode)

    fun connect(scope: CoroutineScope)

    fun disconnect()

    fun forceDisconnect()

    fun isConnected(): Boolean

    fun isDisconnect(): Boolean

    fun getLastConnectedNode(): ServerNode

    fun getCurNode(): ServerNode

    fun observeState(lifecycleOwner: LifecycleOwner, receiver: (state: State) -> Unit)

    fun observeNode(lifecycleOwner: LifecycleOwner, receiver: (node: ServerNode) -> Unit)

    @WorkerThread
    fun writeToDatabase(node: ServerNode)

    fun getPermissionResultLauncher(handler: (granted: Boolean) -> Unit): ActivityResultLauncher<Void?>
}