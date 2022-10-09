package tool.browser.fast.main

import android.net.VpnService
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tool.browser.fast.R
import tool.browser.fast.ad.AdBackgroundTask
import tool.browser.fast.ad.manager.AdvertiseManager
import tool.browser.fast.ad.show.param.FullScreenShowParam
import tool.browser.fast.ad.show.param.NativeShowParam
import tool.browser.fast.component.BaseActivity
import tool.browser.fast.component.FastSearch
import tool.browser.fast.component.FastSearch.Companion.isShowMainNAd
import tool.browser.fast.databinding.ActivityMainBinding
import tool.browser.fast.guide.PrivacyGuideManager
import tool.browser.fast.guide.PrivacyGuidePopupView
import tool.browser.fast.server.manager.ServerManager

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(
    layoutResId = R.layout.activity_main,
    viewModelClass = MainViewModel::class.java
) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServerManager.get()
            .attach(this, this)
        withViewModel {
            ServerManager.get()
                .observeState(this@MainActivity) {
                    connectState.postValue(it)
                }
            ServerManager.get()
                .observeNode(this@MainActivity) {
                    serverNode.postValue(it)
                }
            checkPermission.observe(this@MainActivity) { receiver ->
                if (VpnService.prepare(this@MainActivity) != null) {
                    ServerManager.get()
                        .getPermissionResultLauncher {
                            receiver(it)
                        }.launch(null)
                } else {
                    receiver(true)
                }
            }
            showConnectAd.observe(this@MainActivity) {
                AdvertiseManager.showFullScreen(
                    FullScreenShowParam(
                        space = AdvertiseManager.SPACE_CONNECT,
                        context = this@MainActivity,
                        result = it
                    )
                ) {
                    this.continueResult()
                }
            }
            showHomeNativeAd.observe(this@MainActivity) {
                withLayoutBinding {
                    AdvertiseManager.showNative(
                        NativeShowParam(
                            space = AdvertiseManager.SPACE_HOME,
                            result = it,
                            rootView = this.nNativeView
                        )
                    ) {
                        withViewModel {
                            FastSearch.isShowMainNAd = false
                            this.preloadHomeNativeAd()
                        }
                    }
                }
            }
            startConnectResult.observe(this@MainActivity) {
                lifecycleScope.launch {
                    delay(300L)
                    if (mIsOnResume) {
                        mModel.startResult(it)
                    } else {
                        mModel.enabled.postValue(true)
                    }
                }
            }
        }
        if (!ServerManager.get().isConnected()) {
            PrivacyGuideManager()
                .inject(this@MainActivity, {
                    it.show(this@MainActivity,
                        object : PrivacyGuidePopupView.IPrivacyGuideFunc {
                            override fun connectServer() {
                                mModel.justConnect()
                            }
                        })
                })
        }
    }

    override fun onResume() {
        super.onResume()
        if (FastSearch.isShowMainNAd)
            refreshHomeNativeAd()
    }

    private fun refreshHomeNativeAd() {
        lifecycleScope.launch {
            delay(300L)
            if (!AdvertiseManager.isLimited() && mIsOnResume) {
                withViewModel {
                    refreshNativeAd()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ServerManager.get().detach()
    }

    override fun onStop() {
        super.onStop()
        if (AdBackgroundTask.isInBackground()) {
            isShowMainNAd = true
        }
    }
}
