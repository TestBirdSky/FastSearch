package tool.browser.fast.server.result

import androidx.lifecycle.MutableLiveData
import tool.browser.fast.ad.manager.AdvertiseManager
import tool.browser.fast.ad.request.Request
import tool.browser.fast.ad.request.RequestResult
import tool.browser.fast.component.BaseViewModel
import tool.browser.fast.server.ServerNode

class ResultViewModel : BaseViewModel() {
    val result = MutableLiveData<Boolean>()
    val serverNode = MutableLiveData<ServerNode>()
    val connectTime = MutableLiveData(0L)
    //ad
    val showResultNativeAd = MutableLiveData<RequestResult>()
    private var mResultAd: RequestResult? = null
    private var mResultRequest = Request.forResult {
        clearResultAd()
        mResultAd = it
        if (it.isSuccessful && !it.isLimited) {
            showResultNativeAd.postValue(it)
        }
    }

    fun refreshResultAd() {
        AdvertiseManager.request(mResultRequest)
    }

    fun preloadResultAd() {
        AdvertiseManager.request(
            Request.forResult()
        )
    }

    private fun clearResultAd() {
        AdvertiseManager.clearNativeAd(mResultAd?.data)
        AdvertiseManager.cancelRequest(mResultRequest)
    }

    override fun onCleared() {
        super.onCleared()
        clearResultAd()
    }
}