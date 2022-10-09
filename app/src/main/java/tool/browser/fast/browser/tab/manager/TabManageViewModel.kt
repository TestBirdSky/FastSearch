package tool.browser.fast.browser.tab.manager

import android.view.View
import tool.browser.fast.component.BaseViewModel

class TabManageViewModel : BaseViewModel() {

    fun addTab(view: View) {
        TabManager.addTab()
        finishActivity(view)
    }
}