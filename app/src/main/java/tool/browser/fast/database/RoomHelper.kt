package tool.browser.fast.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tool.browser.fast.component.FastSearch

@Database(entities = [BrowserHistory::class, BookmarkDB::class, ReadListDB::class, RecentlyOpenedDB::class], version = 1)
abstract class RoomHelper : RoomDatabase() {
    companion object {
        private val instance by lazy {
            Room.databaseBuilder(FastSearch.mSelf, RoomHelper::class.java, "browser_history").apply {
                allowMainThreadQueries()
                enableMultiInstanceInvalidation()
                fallbackToDestructiveMigration()
                setQueryExecutor { GlobalScope.launch { it.run() } }
            }.build()
        }
        val browserHistoryDao get() = instance.historyDao()
        val bookmarkDao get() = instance.bookmarkDao()
        val readListDao get() = instance.readListDao()
        val recentlyOpenedDao get() = instance.recentlyDao()
    }

    abstract fun historyDao(): BrowserHistoryDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun readListDao(): ReadListDao
    abstract fun recentlyDao(): RecentlyOpenedDao
}