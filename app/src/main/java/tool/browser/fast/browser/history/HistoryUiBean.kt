package tool.browser.fast.browser.history

import com.drake.brv.annotaion.ItemOrientation
import com.drake.brv.item.ItemSwipe
import tool.browser.fast.database.BrowserHistory

data class HistoryUiBean(
    var isTitle: Boolean,
    var title: String? = "",
    val timeLeft: String? = "",
    val browserHistory: BrowserHistory? = null,
    override var itemOrientationSwipe: Int =ItemOrientation.LEFT
) : ItemSwipe {

}
