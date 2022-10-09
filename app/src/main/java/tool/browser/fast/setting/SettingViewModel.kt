package tool.browser.fast.setting

import android.view.View
import com.blankj.utilcode.util.ToastUtils
import tool.browser.fast.component.BaseViewModel
import tool.browser.fast.component.FastSearch
import tool.browser.fast.utils.createEmailIntent
import tool.browser.fast.utils.startSafely

class SettingViewModel : BaseViewModel() {

    fun openContactUs(view: View) {
        createEmailIntent(FastSearch.EMAIL)
            .startSafely(view.context) {
                ToastUtils.showLong("Contact us by email: ${FastSearch.EMAIL}")
            }
    }

    fun openPrivacy(view: View) {
        startActivity.postValue(
            StartActivityOption(
                target = PrivacyActivity::class.java
            )
        )
    }

    fun openEngineSetting(view: View){
        startActivity.postValue(
            StartActivityOption(
                target = EngineSettingActivity::class.java
            )
        )
    }
}