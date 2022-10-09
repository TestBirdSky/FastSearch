package tool.browser.fast.browser.bookmarkpage

import com.drake.brv.annotaion.ItemOrientation
import com.drake.brv.item.ItemSwipe
import tool.browser.fast.database.BookmarkDB
import tool.browser.fast.database.BrowserHistory

data class BookmarkUiBean(
    var title: String? = "",
    var host: String? = "",
    var url: String? = "",
    var iconUrl: String? = "",
    val bookmarkDB: BookmarkDB,
    override var itemOrientationSwipe: Int = ItemOrientation.LEFT
) : ItemSwipe {

}
