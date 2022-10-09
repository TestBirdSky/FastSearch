package tool.browser.fast.component

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.blankj.utilcode.util.ProcessUtils
import com.drake.brv.utils.BRV
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.tencent.mmkv.MMKV
import tool.browser.fast.BR
import tool.browser.fast.ad.log
import tool.browser.fast.ad.manager.AdvertiseManager
import tool.browser.fast.ad.request.Request
import tool.browser.fast.remote.ConfigManager
import tool.browser.fast.server.ServersRepository
import tool.browser.fast.server.manager.ServerManager
import tool.browser.fast.tbalog.TbaManager
import java.util.*

class FastSearch : Application() {
    companion object {
        const val EMAIL = "sname9455@gmail.com"
        const val PRIVACY_URL = "https://sites.google.com/view/fastsearchapp/home"
        const val IS_DEBUG = true
        var isShowMainNAd = true
        lateinit var mSelf: FastSearch
            private set
    }

    override fun onCreate() {
        super.onCreate()
        ServerManager.get().initialize(this)
        if (ProcessUtils.isMainProcess()) {
            // 初始化BindingAdapter的默认绑定ID
            BRV.modelId = BR.data
            mSelf = this
            MMKV.initialize(this)
            ServersRepository.initialize()
            Firebase.initialize(this)
            ConfigManager.initialize()
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("1A495E037EB76F19D11EEB4EC4885E07")).build()
            )
            AdvertiseManager.initialize()
            listOf(
                AdvertiseManager.SPACE_OPEN,
                AdvertiseManager.SPACE_HOME,
                AdvertiseManager.SPACE_CONNECT,
                AdvertiseManager.SPACE_RESULT
            ).forEach {
                AdvertiseManager
                    .request(
                        Request.build {
                            space = it
                        }
                    )
            }
            TbaManager.tbaInit()
        }
//        test()
    }

    private fun test() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(p0: Activity, p1: Bundle?) {
                log(content = "onActivityCreated $p0")
            }

            override fun onActivityStarted(p0: Activity) {
                log(content = "onActivityCreated $p0")
            }

            override fun onActivityResumed(p0: Activity) {
                log(content = "onActivityResumed $p0")
            }

            override fun onActivityPaused(p0: Activity) {
                log(content = "onActivityPaused $p0")
            }

            override fun onActivityStopped(p0: Activity) {
                log(content = "onActivityStopped $p0")
            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
                log(content = "onActivitySaveInstanceState $p0")
            }

            override fun onActivityDestroyed(p0: Activity) {
                log(content = "onActivityDestroyed $p0")
            }

        })
    }
}