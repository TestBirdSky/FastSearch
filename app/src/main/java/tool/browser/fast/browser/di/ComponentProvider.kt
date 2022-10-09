package tool.browser.fast.browser.di

import tool.browser.fast.browser.engine.IWebEngine
import tool.browser.fast.browser.engine.android.AndroidWebEngine
import tool.browser.fast.browser.search.*
import tool.browser.fast.browser.search.GoogleSearch.GOOGLE_NAME_ENGINE_URL
import tool.browser.fast.browser.tab.IWebTab
import tool.browser.fast.browser.tab.WebTabImpl
import tool.browser.fast.browser.tab.home.DefHomePage
import tool.browser.fast.browser.tab.home.IHomePage
import tool.browser.fast.setting.curSelectedEngine
import tool.browser.fast.utils.StringCacheDelegate

object ComponentProvider {
    fun provideWebEngine(): IWebEngine {
        return AndroidWebEngine()
    }
    fun provideWebTab(): IWebTab = WebTabImpl()
    fun provideHomePage(): IHomePage = DefHomePage()
    fun provideSearchEngine(): ISearchEngine {
        val en = when (curSelectedEngine) {
            BingSearch.BING_NAME_ENGINE_URL -> {
                BingSearch
            }
            DuckSearch.Duck_NAME_ENGINE_URL -> {
                DuckSearch
            }
            YahooSearch.YAHOO_NAME_ENGINE_URL -> {
                YahooSearch
            }
            else -> GoogleSearch
        }
        return en
    }
}