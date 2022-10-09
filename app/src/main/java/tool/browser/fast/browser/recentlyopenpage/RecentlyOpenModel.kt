package tool.browser.fast.browser.recentlyopenpage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tool.browser.fast.component.BaseViewModel
import tool.browser.fast.database.RecentlyOpenedDB
import tool.browser.fast.database.RoomHelper

class RecentlyOpenModel : BaseViewModel() {
    val recentlyData = MutableLiveData<ArrayList<RecentlyOpenUiBean>>()
    private val recentlyDao by lazy { RoomHelper.recentlyOpenedDao }
    fun refreshData() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = recentlyDao.get()
            recentlyData.postValue(listTrasfrom(list))
        }
    }

    private fun listTrasfrom(list: List<RecentlyOpenedDB>): ArrayList<RecentlyOpenUiBean> {
        val result = arrayListOf<RecentlyOpenUiBean>()
        for (l in list) {
            result.add(
                RecentlyOpenUiBean(
                    title = l.title,
                    host = l.host,
                    url = l.url,
                    iconUrl = l.iconUrl,
                    l
                )
            )
        }
        return result
    }
}