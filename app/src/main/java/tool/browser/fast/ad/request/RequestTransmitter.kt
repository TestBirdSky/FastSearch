package tool.browser.fast.ad.request

import tool.browser.fast.ad.logForAd
import tool.browser.fast.ad.manager.AdvertiseManager
import tool.browser.fast.ad.manager.IAdvertiseManager
import tool.browser.fast.utils.addIfNotNull

abstract class RequestTransmitter(
    @IAdvertiseManager.Space val space: String
) {
    private val mCallbacks by lazy {
        mutableListOf<((result: RequestResult) -> Unit)>()
    }
    private var mIsLoading = false
    private var mCachedResult: RequestResult? = null

    fun transmit(
        request: Request
    ) {
        mCallbacks.addIfNotNull(request.callback)
        if (AdvertiseManager.isLimited()) {
            log("ad limited...")
            notifyResult(RequestResult.forLimit())
            return
        }
        if (mIsLoading) {
            log("Is loading...")
            return
        }
        val cache = mCachedResult
        if (cache?.isSuccessful == true) {
            if (cache.isExpired()) {
                log("Cache is expired.")
                clearCache()
            } else {
                request.callback?.let {
                    log("Loaded from cache.")
                }
                notifyResult(cache)
                return
            }
        }
        mIsLoading = true
        log("Start load.")
        AdvertiseManager
            .getRequestParams(space) { params ->
                val finalResult: ((result: RequestResult) -> Unit) = {
                    log("End load, message=${it.code}.")
                    mCachedResult = it
                    notifyResult(it)
                    mIsLoading = false
                }
                startLooped(params) { result ->
                    if (result.isSuccessful) {
                        finalResult(result)
                    } else {
                        if (space == AdvertiseManager.SPACE_OPEN) {
                            startLooped(params) {
                                finalResult(it)
                            }
                        } else {
                            finalResult(result)
                        }
                    }
                }
            }
    }

    fun cancelTransmit(
        request: Request
    ) {
        request.callback?.let {
            mCallbacks.remove(it)
        }
    }

    private fun startLooped(
        params: List<Param>,
        dataIndex: Int = 0,
        receiver: (result: RequestResult) -> Unit
    ) {
        val param = params.getOrNull(dataIndex)
        if (param == null) {
            receiver(RequestResult.forNoAd())
            return
        }
        val internalResult: ((result: RequestResult) -> Unit) = {
            if (it.isSuccessful) {
                receiver(it)
            } else {
                startLooped(params, dataIndex + 1, receiver)
            }
        }
        log("Request ad, param=${param}")
        onRequest(param, internalResult)
    }

    protected abstract fun onRequest(
        param: Param,
        receiver: (result: RequestResult) -> Unit
    )

    private fun notifyResult(result: RequestResult) {
        val callbacks = mCallbacks.toList()
        callbacks.forEach {
            if (mCallbacks.contains(it)) {
                it.invoke(result)
            }
        }
    }

    fun clearCache() {
        mCachedResult = null
        log("Cleared cache.")
    }

    protected fun log(content: String) {
        logForAd(space, content)
    }
}