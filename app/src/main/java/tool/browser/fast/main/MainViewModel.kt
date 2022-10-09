package tool.browser.fast.main

import android.content.Intent
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tool.browser.fast.ad.manager.AdvertiseManager
import tool.browser.fast.ad.request.Request
import tool.browser.fast.ad.request.RequestResult
import tool.browser.fast.browser.BrowserActivity
import tool.browser.fast.component.BaseViewModel
import tool.browser.fast.server.ServerNode
import tool.browser.fast.server.State
import tool.browser.fast.server.manager.ServerManager
import tool.browser.fast.server.nodes.NodesActivity
import tool.browser.fast.server.result.ResultActivity
import tool.browser.fast.setting.SettingActivity
import tool.browser.fast.utils.generateConsKey

class MainViewModel : BaseViewModel(), ServerManager.ServerTimeManager.OnClockListener {
    companion object {
        const val STATE_TEXT_CONNECTED = "Connected"
        const val STATE_TEXT_CONNECTING = "Connecting"
        const val STATE_TEXT_DISCONNECTING = "Disconnecting"
        const val STATE_TEXT_DISCONNECT = "Connect"

        //result
        const val REQUEST_CODE = 300
        const val ACTION_CONNECT = "connect"
        const val ACTION_DISCONNECT = "disconnect"
        val RESULT_ACTION = generateConsKey("action")
    }

    val serverNode = MutableLiveData(ServerNode.fast)
    val connectTime = MutableLiveData(0L)
    val connectState = MutableLiveData(State.STOPPED)
    val checkPermission = MutableLiveData<(hasPermission: Boolean) -> Unit>()
    val startConnectResult = MutableLiveData<Boolean>()

    //ad
    val showConnectAd = MutableLiveData<RequestResult>()
    private var mConnectAdResult: RequestResult? = null
    private val mConnectRequest = Request.forConnect {
        clearConnectAd()
        mConnectAdResult = it
    }
    private var mContinueResult: (() -> Unit)? = null
    val showHomeNativeAd = MutableLiveData<RequestResult>()
    private var mHomeNativeAdResult: RequestResult? = null
    private var mHomeRequest = Request.forHome {
        clearHomeAd()
        mHomeNativeAdResult = it
        if (it.isSuccessful && !it.isLimited) {
            showHomeNativeAd.postValue(it)
        }
    }

    private val mServerManager by lazy {
        ServerManager.get()
    }
    private val mStartNodesOption by lazy {
        StartActivityOption(
            target = NodesActivity::class.java,
            requestCode = REQUEST_CODE
        )
    }
    private val mStartSettingOption by lazy {
        StartActivityOption(
            target = SettingActivity::class.java
        )
    }
    private val mStartBrowserOption by lazy {
        StartActivityOption(
            target = BrowserActivity::class.java
        )
    }

    init {
        ServerManager.ServerTimeManager.register(this)
    }

    fun openSetting(view: View) {
        startActivity.postValue(mStartSettingOption)
    }

    fun openServers(view: View) {
        startActivity.postValue(mStartNodesOption)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            val action = data?.getStringExtra(RESULT_ACTION)
            if (action == ACTION_CONNECT) {
                toggle(false)
            } else if (action == ACTION_DISCONNECT) {
                toggle(true)
            }
        }
    }

    fun openBrowser(view: View) {
        startActivity.postValue(mStartBrowserOption)
    }

    fun toggleServer(view: View) {
        toggle()
    }

    private fun toggle(isConnected: Boolean = mServerManager.isConnected()) {
        enabled.postValue(false)
        checkPermission.postValue {
            if (it) {
                if (isConnected) {
                    disconnect()
                } else {
                    connect()
                }
            } else {
                ToastUtils.showShort("Connected fail!")
                enabled.postValue(true)
            }
        }
    }

    fun justConnect() {
        if (ServerManager.get().isConnected()) return
        toggle()
    }

    private fun connect() {
        mServerManager.connect(viewModelScope)
        waitResult(true) {
            if (mServerManager.isConnected()) {
                mServerManager.changeState(State.CONNECTED)
                startConnectResult.postValue(true)
            } else {
                mServerManager.forceDisconnect()
                ToastUtils.showLong("Connected fail!")
                enabled.postValue(true)
            }
        }
    }

    private fun disconnect() {
        mServerManager.disconnect()
        waitResult(false) {
            mServerManager.changeState(State.STOPPED)
            startConnectResult.postValue(false)
        }
    }

    fun startResult(connected: Boolean) {
        startActivity.postValue(
            StartActivityOption(
                target = ResultActivity::class.java,
                bundle = bundleOf(Pair("result", connected))
            )
        )
        enabled.postValue(true)
    }

    private fun waitResult(forConnect: Boolean, receiver: () -> Unit) {
        AdvertiseManager.request(mConnectRequest)
        viewModelScope.launch {
            var invoked = false
            var duration = 0L
            val interval = 500L
            while (duration <= 10000L && !invoked) {
                delay(interval)
                duration += interval
                if (duration >= 1500L
                    && (if (forConnect)
                        mServerManager.isConnected()
                    else mServerManager.isDisconnect())
                ) {
                    mConnectAdResult?.let {
                        if (it.isSuccessful) {
                            invoked = true
                            mContinueResult = receiver
                            showConnectAd.postValue(it)
                        } else if (it.isLimited) {
                            invoked = true
                            receiver()
                        }
                    }
                }
            }
            if (!invoked) {
                val connectAdRes = this@MainViewModel.mConnectAdResult
                if (connectAdRes?.isSuccessful == true) {
                    mContinueResult = receiver
                    showConnectAd.postValue(connectAdRes!!)
                } else {
                    receiver()
                }
            }
        }
    }

    override fun onUpdate(time: Long) {
        connectTime.postValue(time)
    }

    override fun onCleared() {
        super.onCleared()
        clearConnectAd()
        clearHomeAd()
        ServerManager.ServerTimeManager.unregister(this)
    }

    fun continueResult() {
        clearConnectAd()
        AdvertiseManager.request(
            Request.forConnect()
        )
        mContinueResult?.invoke()
        mContinueResult = null
    }

    fun refreshNativeAd() {
        AdvertiseManager.request(mHomeRequest)
    }

    fun preloadHomeNativeAd() {
        AdvertiseManager.request(
            Request.forHome()
        )
    }

    private fun clearConnectAd() {
        mConnectAdResult = null
        AdvertiseManager.cancelRequest(mConnectRequest)
    }

    private fun clearHomeAd() {
        AdvertiseManager.clearNativeAd(mHomeNativeAdResult?.data)
        mHomeNativeAdResult = null
        AdvertiseManager.cancelRequest(mHomeRequest)
    }
}