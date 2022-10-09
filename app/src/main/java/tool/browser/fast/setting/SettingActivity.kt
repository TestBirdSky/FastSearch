package tool.browser.fast.setting

import tool.browser.fast.R
import tool.browser.fast.component.BaseActivity
import tool.browser.fast.databinding.ActivitySettingBinding

class SettingActivity : BaseActivity<ActivitySettingBinding, SettingViewModel>(
    layoutResId = R.layout.activity_setting,
    viewModelClass = SettingViewModel::class.java
) {
}