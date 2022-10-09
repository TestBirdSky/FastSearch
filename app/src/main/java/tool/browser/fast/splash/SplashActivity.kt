package tool.browser.fast.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import tool.browser.fast.R
import tool.browser.fast.ad.manager.AdvertiseManager
import tool.browser.fast.ad.ui.FullScreenAdActivity
import tool.browser.fast.browser.BrowserActivity
import tool.browser.fast.component.BaseActivity
import tool.browser.fast.component.BaseViewModel
import tool.browser.fast.component.FastSearch
import tool.browser.fast.databinding.ActivitySplashBinding
import tool.browser.fast.helpers.progress.ValueAnimProgress
import tool.browser.fast.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : FullScreenAdActivity<ActivitySplashBinding, SplashViewModel>(
    layoutResId = R.layout.activity_splash,
    viewModelClass = SplashViewModel::class.java
) {
    private val mStartMainOption by lazy {
        BaseViewModel.StartActivityOption(
            target = MainActivity::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FastSearch.isShowMainNAd = true
        ValueAnimProgress(
            onProgressUpdate = { progress, helper ->
                withViewModel {
                    if (progress in 20..99) {
                        val result = this@SplashActivity.mAd
                        when {
                            result?.isSuccessful == true -> {
                                helper.dismiss()
                                this.progress.postValue(100)
                                this@SplashActivity.showFullScreenAd {
                                    this.startActivity.postValue(mStartMainOption)
                                }
                            }
                            result?.isLimited == true -> {
                                helper.dismiss()
                                this.progress.postValue(100)
                                this.startActivity.postValue(mStartMainOption)
                            }
                            else -> {
                                this.progress.postValue(progress)
                            }
                        }
                    } else {
                        this.progress.postValue(progress)
                    }
                }
            },
            onProgressEnd = {
                withViewModel {
                    this.progress.postValue(100)
                    this.startActivity.postValue(mStartMainOption)
                }
            },
            duration = 11000L,
            lifecycleOwner = this
        ).start()
    }

    override fun doStartActivityForVm(option: BaseViewModel.StartActivityOption) {
        if (!ActivityUtils.isActivityExistsInStack(MainActivity::class.java)) {
            this@SplashActivity.startActivity(
                Intent(
                    this@SplashActivity,
                    MainActivity::class.java
                )
            )
        }
        this@SplashActivity.finish()
    }

    override fun onBackPressed() {
    }

    override fun getAdSpace(): String {
        return AdvertiseManager.SPACE_OPEN
    }
}