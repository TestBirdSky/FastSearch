package tool.browser.fast.browser.bookmarkpage

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tool.browser.fast.component.BaseViewModel
import tool.browser.fast.database.BookmarkDB
import tool.browser.fast.database.RoomHelper

class BookmarkModel : BaseViewModel() {
    val bookmarkList = MutableLiveData<ArrayList<BookmarkUiBean>>()
    private val bookmarkDao by lazy { RoomHelper.bookmarkDao }
    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = bookmarkDao.get()
            bookmarkList.postValue(listTrasfrom(list))
        }
    }

    fun search(str: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = if (TextUtils.isEmpty(str)) {
                bookmarkDao.get()
            } else {
                bookmarkDao.search(str)
            }
            Log.i("searchHistory", "${list.size}")
            bookmarkList.postValue(listTrasfrom(list))
        }
    }

    private fun listTrasfrom(list: List<BookmarkDB>): ArrayList<BookmarkUiBean> {
        val result = arrayListOf<BookmarkUiBean>()
        for (l in list) {
            result.add(
                BookmarkUiBean(
                    title = l.title,
                    host = l.host,
                    url = l.url,
                    iconUrl = l.iconUrl,
                    bookmarkDB = l
                )
            )
        }
        return result
    }

    fun deleteData(bookmark: BookmarkDB) {
        bookmarkDao.delete(bookmark)
    }
}