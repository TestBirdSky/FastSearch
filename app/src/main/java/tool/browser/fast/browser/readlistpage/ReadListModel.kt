package tool.browser.fast.browser.readlistpage

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.drake.brv.annotaion.ItemOrientation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tool.browser.fast.browser.bookmarkpage.BookmarkUiBean
import tool.browser.fast.browser.history.HistoryUiBean
import tool.browser.fast.component.BaseViewModel
import tool.browser.fast.database.BrowserHistory
import tool.browser.fast.database.ReadListDB
import tool.browser.fast.database.RoomHelper
import tool.browser.fast.utils.getDate
import tool.browser.fast.utils.getTime

class ReadListModel:BaseViewModel() {
    val readList = MutableLiveData<ArrayList<ReadListUiBean>>()
    private val readListDao by lazy { RoomHelper.readListDao }
    fun refreshData() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = readListDao.get()
            readList.postValue(listTrasfrom(list))
        }
    }

    fun search(str: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = if (TextUtils.isEmpty(str)) {
                readListDao.get()
            } else {
                readListDao.search(str)
            }
            Log.i("searchHistory", "${list.size}")
            readList.postValue(listTrasfrom(list))
        }
    }

    private fun listTrasfrom(list: List<ReadListDB>): ArrayList<ReadListUiBean> {
        val result = arrayListOf<ReadListUiBean>()
        for (l in list) {
            result.add(
                ReadListUiBean(
                    title = l.title,
                    host = l.host,
                    url = l.url,
                    iconUrl = l.iconUrl,
                    readListDB = l
                )
            )
        }
        return result
    }

    fun deleteData(history: ReadListDB) {
        readListDao.delete(history)
    }
}