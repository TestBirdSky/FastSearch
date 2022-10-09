package tool.browser.fast.browser.readlistpage

import com.drake.brv.annotaion.ItemOrientation
import com.drake.brv.item.ItemSwipe
import tool.browser.fast.database.ReadListDB

data class ReadListUiBean(
    var title: String? = "",
    var host: String? = "",
    var url: String? = "",
    var iconUrl: String? = "",
    val readListDB: ReadListDB,
    override var itemOrientationSwipe: Int = ItemOrientation.LEFT
) : ItemSwipe {

}