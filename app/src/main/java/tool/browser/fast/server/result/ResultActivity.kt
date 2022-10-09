package tool.browser.fast.server.result

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tool.browser.fast.R
import tool.browser.fast.ad.AdBackgroundTask
import tool.browser.fast.ad.manager.AdvertiseManager
import tool.browser.fast.ad.show.param.NativeShowParam
import tool.browser.fast.component.BaseActivity
import tool.browser.fast.component.FastSearch
import tool.browser.fast.databinding.ActivityResultBinding
import tool.browser.fast.server.manager.ServerManager

class ResultActivity : BaseActivity<ActivityResultBinding, ResultViewModel>(
    layoutResId = R.layout.activity_result,
    viewModelClass = ResultViewModel::class.java
) {
    private val mIsConnected by lazy {
        intent.extras?.getBoolean("result") ?: false
    }
    private val mServerTimeListener by lazy {
        object : ServerManager.ServerTimeManager.OnClockListener {
            override fun onUpdate(time: Long) {
                withViewModel {
                    connectTime.postValue(time)
                }
            }
        }
    }

    //ad
    private var mCanRefreshNativeAd = true
    private val mAdBgTask = Runnable {
        mCanRefreshNativeAd = true
    }

    override fun onResume() {
        super.onResume()
        refreshResultNativeAd()
    }

    private fun refreshResultNativeAd() {
        lifecycleScope.launch {
            delay(300L)
            if (!AdvertiseManager.isLimited() && mCanRefreshNativeAd && mIsOnResume) {
                withViewModel {
                    refreshResultAd()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AdBackgroundTask.register(mAdBgTask)
        withViewModel {
            result.postValue(mIsConnected)
            if (mIsConnected) {
                ServerManager.ServerTimeManager.register(mServerTimeListener)
            } else {
                connectTime.postValue(
                    ServerManager.ServerTimeManager.mLastConnectedTime
                )
            }
            serverNode.postValue(
                if (mIsConnected)
                    ServerManager.get().getCurNode()
                else
                    ServerManager.get().getLastConnectedNode()
            )
            showResultNativeAd.observe(this@ResultActivity) {
                withLayoutBinding {
                    AdvertiseManager.showNative(
                        NativeShowParam(
                            space = AdvertiseManager.SPACE_RESULT,
                            result = it,
                            rootView = this@ResultActivity.mLayoutBinding.nNativeView
                        )
                    ) {
                        mCanRefreshNativeAd = false
                        withViewModel {
                            this.preloadResultAd()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AdBackgroundTask.unregister(mAdBgTask)
        if (mIsConnected) {
            ServerManager.ServerTimeManager.unregister(mServerTimeListener)
        }
    }
    override fun onStop() {
        super.onStop()
        if (AdBackgroundTask.isInBackground()) {
            mCanRefreshNativeAd = true
        }
    }
}