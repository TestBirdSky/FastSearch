package tool.browser.fast.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BrowserHistoryDao {
    @Insert
    fun add(value: BrowserHistory)

    @Delete
    fun delete(value: BrowserHistory)

    @Query("SELECT * FROM BrowserHistory order by systemTime desc")
    fun get(): List<BrowserHistory>

    @Query("SELECT * FROM BrowserHistory where title LIKE '%' || :str ||'%' OR host Like '%' || :str ||'%' order by systemTime desc")
    fun search(str: String): List<BrowserHistory>
}