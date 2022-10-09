package tool.browser.fast.ad

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.google.android.gms.ads.AdActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tool.browser.fast.ad.manager.AdvertiseManager
import tool.browser.fast.ad.request.Request
import tool.browser.fast.browser.BrowserActivity
import tool.browser.fast.component.FastSearch
import tool.browser.fast.main.MainActivity
import tool.browser.fast.splash.SplashActivity
import tool.browser.fast.utils.addIfNotNull

object AdBackgroundTask {
    private var mActiveActivityCount = 0
    private var mJob: Job? = null
    private var mTaskFlag = false
    private val mTasks = mutableListOf<Runnable>()

    private val mActivityCallback = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(p0: Activity, p1: Bundle?) {

        }

        override fun onActivityStarted(p0: Activity) {
            mActiveActivityCount++
            mJob?.cancel()
            mJob = null
            if (mTaskFlag) {
                AdvertiseManager
                    .request(
                        Request.forOpen()
                    )
//                if (ActivityUtils.isActivityExistsInStack(MainActivity::class.java)) {
//                    ActivityUtils.getTopActivity()?.let {
//                        it.startActivity(Intent(it, SplashActivity::class.java))
//                    }
//                }
                ActivityUtils.getTopActivity()?.let {
                    it.startActivity(Intent(it, SplashActivity::class.java))
                }
                mTaskFlag = false
                notifyTask()
            }
        }

        override fun onActivityResumed(p0: Activity) {

        }

        override fun onActivityPaused(p0: Activity) {

        }

        override fun onActivityStopped(p0: Activity) {
            mActiveActivityCount--
            if (mActiveActivityCount <= 0) {
                mJob = GlobalScope.launch {
                    delay(3000L)
                    logForAd("", "Arrived 3s.")
                    mTaskFlag = true
                    ActivityUtils.finishActivity(SplashActivity::class.java)
                    ActivityUtils.finishActivity(AdActivity::class.java)
                }
            }
        }

        override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

        }

        override fun onActivityDestroyed(p0: Activity) {

        }

    }

    fun initialize() {
        FastSearch.mSelf.registerActivityLifecycleCallbacks(mActivityCallback)
    }

    private fun notifyTask() {
        val tasks = mTasks.toList()
        tasks.forEach {
            if (mTasks.contains(it)) {
                it.run()
            }
        }
    }

    fun register(runnable: Runnable) {
        mTasks.addIfNotNull(runnable)
    }

    fun unregister(runnable: Runnable) {
        mTasks.remove(runnable)
    }

    fun isInBackground():Boolean{
        return mActiveActivityCount==0
    }
}