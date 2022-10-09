package tool.browser.fast.setting

import android.os.Bundle
import tool.browser.fast.R
import tool.browser.fast.component.BaseActivity
import tool.browser.fast.component.FastSearch
import tool.browser.fast.databinding.ActivityPrivacyBinding

class PrivacyActivity : BaseActivity<ActivityPrivacyBinding, SettingViewModel>(
    layoutResId = R.layout.activity_privacy,
    viewModelClass = SettingViewModel::class.java
) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        withLayoutBinding {
            wvPrivacy.loadUrl(FastSearch.PRIVACY_URL)
        }
    }
}