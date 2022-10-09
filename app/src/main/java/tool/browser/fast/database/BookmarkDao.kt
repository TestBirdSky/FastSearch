package tool.browser.fast.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookmarkDao {
    @Insert
    fun add(value: BookmarkDB)

    @Delete
    fun delete(value: BookmarkDB)

    @Query("Delete from `bookmark.db` where id=:id")
    fun deleteWhitId(id: Int)

    @Query("SELECT * FROM `bookmark.db` order by id desc")
    fun get(): List<BookmarkDB>

    @Query("SELECT * FROM `bookmark.db` where title LIKE '%' || :str ||'%' OR host Like '%' || :str ||'%' order by id desc")
    fun search(str: String): List<BookmarkDB>

    @Query("SELECT * FROM `bookmark.db` where url =:string order by id desc limit 1")
    fun searchByUrl(string: String): ReadListDB?
}