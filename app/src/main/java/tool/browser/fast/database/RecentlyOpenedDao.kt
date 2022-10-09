package tool.browser.fast.database

import androidx.room.*

@Dao
interface RecentlyOpenedDao {
    @Insert
    fun add(value: RecentlyOpenedDB)

    @Query("SELECT * FROM `recently.db` order by id desc limit 4")
    fun get(): List<RecentlyOpenedDB>

    @Query("SELECT * FROM `recently.db` where time =:t order by id desc")
    fun get(t: Long): RecentlyOpenedDB

    @Update
    fun update(value: RecentlyOpenedDB)
}