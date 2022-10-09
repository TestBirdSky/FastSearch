package tool.browser.fast.browser.search

object BingSearch : ISearchEngine {
   public  const val BING_NAME_ENGINE_URL = "https://www.bing.com/search?q="
    override val queryUrl: String
        get() = BING_NAME_ENGINE_URL
}

object YahooSearch : ISearchEngine {
    const val YAHOO_NAME_ENGINE_URL = "https://search.yahoo.com/search?p="
    override val queryUrl: String
        get() = YAHOO_NAME_ENGINE_URL
}

object DuckSearch : ISearchEngine {
    const val Duck_NAME_ENGINE_URL = "https://duckduckgo.com/t=mysearch&q="
    override val queryUrl: String
        get() = Duck_NAME_ENGINE_URL
}