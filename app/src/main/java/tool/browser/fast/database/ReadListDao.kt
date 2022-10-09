package tool.browser.fast.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ReadListDao {
    @Insert
    fun add(value: ReadListDB)

    @Delete
    fun delete(value: ReadListDB)

    @Query("SELECT * FROM `read_list.db` order by id desc")
    fun get(): List<ReadListDB>

    @Query("SELECT * FROM `read_list.db` where title LIKE '%' || :string ||'%' OR host Like '%' || :string ||'%' order by id desc")
    fun search(string: String): List<ReadListDB>

    @Query("SELECT * FROM `read_list.db` where url =:string order by id desc limit 1")
    fun searchByUrl(string: String): ReadListDB?
}