package tool.browser.fast.browser.search

object GoogleSearch : ISearchEngine {
    const val GOOGLE_NAME_ENGINE_URL = "https://www.google.com/search?client=lightning&ie=UTF-8&oe=UTF-8&q="
    override val queryUrl: String
        get() = GOOGLE_NAME_ENGINE_URL
}