package tool.browser.fast.browser.tab

import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleOwner
import tool.browser.fast.component.BaseViewModel

interface ITabHost {
    fun getLifeOwner(): LifecycleOwner
    fun onTabContentViewChanged(view: IView?, isHomePage: Boolean)
    fun onTabChanged(newTab: IWebTab)
    fun onTabCountChanged(count: Int) {}
    fun createLayoutInflater(): LayoutInflater
    fun startActivityForTab(option: BaseViewModel.StartActivityOption)
    fun showMorePopupWindow(view: View,isHomePage: Boolean)
}