package tool.browser.fast.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.random.Random

@Entity(tableName = "recently.db")
class RecentlyOpenedDB {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var title: String? = ""
    var host: String? = ""
    var iconUrl: String? = ""
    var url: String? = ""
    var time: Long = Random(Long.MAX_VALUE).nextLong() + System.currentTimeMillis()
}