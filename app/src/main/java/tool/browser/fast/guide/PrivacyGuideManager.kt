package tool.browser.fast.guide

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.lxj.xpopup.XPopup
import tool.browser.fast.ad.log
import tool.browser.fast.component.BaseActivity
import tool.browser.fast.remote.ConfigManager
import tool.browser.fast.tbalog.TbaManager.mInstallReferrer

class PrivacyGuideManager {
    private val mShowGuideDialog = MutableLiveData<PrivacyGuideManager>()
    private val mGlobalSwitch: Boolean
        get() {
            return when (ConfigManager.mPrivacyGuideSwitch.toIntOrNull() ?: 0) {
                1 -> {
                    log("=mInstallReferrer", mInstallReferrer)
                    mInstallReferrer.contains("facebook") || mInstallReferrer.contains("fb4a")
                }
                else -> true
            }
        }

    fun inject(
        owner: LifecycleOwner,
        observer: Observer<PrivacyGuideManager>
    ) {
        mShowGuideDialog.observe(owner, observer)
        if (mGlobalSwitch) {
            mShowGuideDialog.postValue(this)
        }
    }

    fun show(
        context: BaseActivity<*, *>,
        privacyGuideFunc: PrivacyGuidePopupView.IPrivacyGuideFunc
    ) {
        XPopup.Builder(context)
            .isDestroyOnDismiss(true)
            .hasStatusBar(true)
            .asCustom(
                PrivacyGuidePopupView(
                    context = context,
                    lifecycleOwner = context,
                    privacyGuideFunc = privacyGuideFunc
                )
            )
            .show()
    }
}