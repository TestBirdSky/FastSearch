package tool.browser.fast.browser.history

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.drake.brv.annotaion.ItemOrientation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tool.browser.fast.ad.manager.AdvertiseManager
import tool.browser.fast.component.BaseViewModel
import tool.browser.fast.database.BrowserHistory
import tool.browser.fast.database.RoomHelper
import tool.browser.fast.server.manager.ServerManager
import tool.browser.fast.utils.getDate
import tool.browser.fast.utils.getTime

class HistoryModel : BaseViewModel() {
    val historyListData = MutableLiveData<ArrayList<HistoryUiBean>>()
    private val historyDao by lazy { RoomHelper.browserHistoryDao }
    fun refreshHistoryListData() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = historyDao.get()
            historyListData.postValue(listTrasfrom(list))
        }
    }

    fun searchHistory(str: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = if (TextUtils.isEmpty(str)) {
                historyDao.get()
            } else {
                historyDao.search(str)
            }
            Log.i("searchHistory", "${list.size}")
            historyListData.postValue(listTrasfrom(list))
        }
    }

    private fun listTrasfrom(list: List<BrowserHistory>): ArrayList<HistoryUiBean> {
        val result = arrayListOf<HistoryUiBean>()
        var curTitle = ""
        for (l in list) {
            val date = getDate(l.systemTime)
            if (curTitle != date) {
                curTitle = date
                result.add(HistoryUiBean(true, date, itemOrientationSwipe = ItemOrientation.NONE))
            }
            result.add(
                HistoryUiBean(
                    false,
                    title = l.title,
                    browserHistory = l,
                    timeLeft = getTime(l.systemTime)
                )
            )
        }
        return result
    }

    fun deleteHistoryData(history: BrowserHistory) {
        historyDao.delete(history)
    }
}