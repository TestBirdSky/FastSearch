package tool.browser.fast.server.manager

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.blankj.utilcode.util.ThreadUtils
import com.github.shadowsocks.Core
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.database.ProfileManager
import com.github.shadowsocks.preference.DataStore
import com.github.shadowsocks.utils.StartService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tool.browser.fast.server.ServersRepository
import tool.browser.fast.server.ServerNode
import tool.browser.fast.server.State
import tool.browser.fast.utils.LongCacheDelegate
import tool.browser.fast.utils.addIf

object ServerManager {
    object ServerTimeManager {
        interface OnClockListener {
            fun onUpdate(time: Long)
        }

        private val mListeners = mutableListOf<OnClockListener>()
        private var mTime by LongCacheDelegate(setReceiver = { value ->
            mListeners.forEach { it.onUpdate(value) }
        }) { "ser_conn_time" }
        var mLastConnectedTime = 0L
            private set
        private var mHandler: Handler? = null
        private var mDelayRun = Runnable {
            mTime += 1000L
            turnNext()
        }

        fun register(callback: OnClockListener) {
            mListeners.addIf(callback) { !mListeners.contains(callback) }
        }

        fun unregister(callback: OnClockListener) {
            mListeners.remove(callback)
        }

        fun start() {
            dismiss()
            mHandler = Handler(Looper.getMainLooper())
            turnNext()
        }

        private fun turnNext() {
            mHandler?.postDelayed(mDelayRun, 1000L)
        }

        fun stop() {
            dismiss()
            mLastConnectedTime = mTime
            mTime = 0L
        }

        private fun dismiss() {
            mHandler?.removeCallbacks(mDelayRun)
            mHandler = null
        }
    }

    abstract class BaseServerManager : IServerManager, LifecycleEventObserver {
        private var mContext: Context? = null
        private var mState = State.STOPPED
            set(value) {
                field = value
                mObservableState.postValue(value)
            }
        private val mObservableState by lazy {
            MutableLiveData<State>()
        }
        private val mObservableNode by lazy {
            MutableLiveData<ServerNode>()
        }
        private var mNode = ServerNode.fast
            set(value) {
                field = value
                mObservableNode.postValue(value)
            }
        private var mLastConnectedNode = ServerNode.fast

        final override fun initialize(app: Application) {
            onInitialize(app)
        }

        final override fun observeState(
            lifecycleOwner: LifecycleOwner,
            receiver: (state: State) -> Unit
        ) {
            mObservableState.observe(lifecycleOwner,
                Observer {
                    receiver(it)
                })
        }

        final override fun observeNode(
            lifecycleOwner: LifecycleOwner,
            receiver: (node: ServerNode) -> Unit
        ) {
            mObservableNode.observe(lifecycleOwner,
                Observer {
                    receiver(it)
                })
        }

        final override fun attach(context: Context, lifecycleOwner: LifecycleOwner) {
            lifecycleOwner.lifecycle.addObserver(this)
            mContext = context
            onAttach()
        }

        final override fun detach() {
            onDetach()
            this.mContext = null
        }

        protected fun withContext(receiver: Context.() -> Unit) {
            mContext?.let {
                receiver(it)
            }
        }

        final override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_START) {
                onStart()
            } else if (event == Lifecycle.Event.ON_DESTROY) {
                source.lifecycle.removeObserver(this)
                onDestroy()
            }
        }

        final override fun connect(scope: CoroutineScope) {
            changeState(State.CONNECTING)
            doConnect(scope)
        }

        private fun doConnect(scope: CoroutineScope) {
            scope.launch(Dispatchers.IO) {
                val node = mNode
                onConnect(
                    if (node == ServerNode.fast) {
                        ServersRepository.getSmartNodes().randomOrNull()
                    } else {
                        node
                    }
                )
            }
        }

        final override fun changeNode(newNode: ServerNode) {
            mNode = newNode
        }

        final override fun changeState(state: State) {
            mState = state
            if (state == State.CONNECTED) {
                mLastConnectedNode = mNode
                ServerTimeManager.start()
            } else if (state == State.STOPPING) {
                ServerTimeManager.stop()
            }
        }

        final override fun disconnect() {
            changeState(State.STOPPING)
            onDisconnect()
        }

        override fun forceDisconnect() {
            changeState(State.STOPPED)
            Core.stopService()
        }

        final override fun getLastConnectedNode(): ServerNode {
            return mLastConnectedNode
        }

        final override fun getCurNode(): ServerNode {
            return mNode
        }

        protected open fun onInitialize(app: Application) {}
        protected open fun onAttach() {}
        protected abstract fun onConnect(node: ServerNode?)
        protected abstract fun onDisconnect()
        protected open fun onStart() {}
        protected open fun onDetach() {}
        protected open fun onDestroy() {}
    }

    @SuppressLint("StaticFieldLeak")
    private object ShadowSocks : BaseServerManager(), ShadowsocksConnection.Callback {
        private var mSsState = BaseService.State.Idle
        private val mSsConnection = ShadowsocksConnection(true)
        private lateinit var mSsPermissionLauncher: ActivityResultLauncher<Void?>
        private var mPermissionRequestHandler: ((granted: Boolean) -> Unit)? = null

        override fun onAttach() {
            withContext {
                mSsConnection.connect(this, this@ShadowSocks)
                mSsPermissionLauncher =
                    (this as? FragmentActivity)?.registerForActivityResult(StartService()) {
                        mPermissionRequestHandler?.invoke(!it)
                    }!!
            }
        }

        override fun getPermissionResultLauncher(handler: (granted: Boolean) -> Unit): ActivityResultLauncher<Void?> {
            mPermissionRequestHandler = handler
            return mSsPermissionLauncher
        }

        override fun onConnect(node: ServerNode?) {
            DataStore.profileId = if (node == null) 0L else queryDataIdFromNode(node)
            Core.startService()
        }

        private fun queryDataIdFromNode(node: ServerNode): Long {
            ProfileManager.getAllProfiles()?.forEach {
                if (it.host == node.host && it.remotePort == node.port) {
                    return it.id
                }
            }
            return 0L
        }

        override fun isConnected(): Boolean {
            return mSsState == BaseService.State.Connected
        }

        override fun isDisconnect(): Boolean {
            return mSsState == BaseService.State.Stopped
        }

        override fun onDisconnect() {
            Core.stopService()
        }

        override fun onStart() {
            mSsConnection.bandwidthTimeout = 300L
        }

        override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
            mSsState = state
        }

        override fun onServiceConnected(service: IShadowsocksService) {
            val stat = BaseService.State.values()[service.state]
            mSsState = stat
            if (stat == BaseService.State.Connected) {
                changeState(State.CONNECTED)
                ServerTimeManager.start()
            } else if (stat == BaseService.State.Stopped) {
                changeState(State.STOPPED)
                ServerTimeManager.stop()
            }

        }

        override fun onServiceDisconnected() {
            changeState(State.STOPPED)
        }

        override fun onBinderDied() {
            withContext {
                mSsConnection.disconnect(this)
            }
        }

        override fun onDetach() {
            onBinderDied()
        }

        override fun onInitialize(app: Application) {
            Core.init(app, null)
        }

        override fun writeToDatabase(node: ServerNode) {
            if (ThreadUtils.isMainThread())
                throw IllegalStateException("Can not call me on the main thread.")
            val profile = toSsProfile(node)
            var oldProfileId: Long? = null
            ProfileManager.getAllProfiles()?.forEach {
                if (it.host == profile.host && it.remotePort == profile.remotePort) {
                    oldProfileId = it.id
                    return@forEach
                }
            }
            if (oldProfileId == null) {
                ProfileManager.createProfile(profile)
            } else {
                ProfileManager.updateProfile(profile.apply {
                    id = oldProfileId!!
                })
            }
        }

        private fun toSsProfile(node: ServerNode): Profile {
            return Profile(
                id = 0L,
                name = node.nodeName,
                host = node.host,
                remotePort = node.port,
                password = node.password,
                method = node.method
            )
        }
    }

    fun get(): IServerManager {
        return ShadowSocks
    }
}