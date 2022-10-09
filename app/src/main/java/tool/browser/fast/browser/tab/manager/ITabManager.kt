package tool.browser.fast.browser.tab.manager

import tool.browser.fast.browser.tab.IWebTab

interface ITabManager {
    fun addTab()
    fun removeTab(removed: IWebTab)
    fun changeToTab(to: IWebTab)
    fun reload()
    fun addIncognitoTab()
    fun addReadLater()
    fun addBookmark()
}