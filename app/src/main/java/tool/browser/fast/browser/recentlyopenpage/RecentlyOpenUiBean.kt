package tool.browser.fast.browser.recentlyopenpage

import tool.browser.fast.database.RecentlyOpenedDB

data class RecentlyOpenUiBean(
    var title: String? = "",
    var host: String? = "",
    var url: String? = "",
    var iconUrl: String? = "",
    val recentlyOpenedDB: RecentlyOpenedDB,
)