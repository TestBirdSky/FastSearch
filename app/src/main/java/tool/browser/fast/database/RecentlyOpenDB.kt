package tool.browser.fast.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recently.db")
class RecentlyOpenDB {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var title: String? = ""
    var host: String? = ""
    var iconUrl: String? = ""
    var url: String? = ""
}