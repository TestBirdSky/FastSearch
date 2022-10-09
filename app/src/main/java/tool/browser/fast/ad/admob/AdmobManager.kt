package tool.browser.fast.ad.admob

import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd
import tool.browser.fast.ad.AdBackgroundTask
import tool.browser.fast.ad.manager.AdvertiseManager
import tool.browser.fast.ad.manager.IAdvertiseManager
import tool.browser.fast.ad.request.Param
import tool.browser.fast.ad.request.Request
import tool.browser.fast.ad.show.param.FullScreenShowParam
import tool.browser.fast.ad.show.param.NativeShowParam
import tool.browser.fast.component.FastSearch

internal object AdmobManager : IAdvertiseManager {
    private val mRequester by lazy {
        hashMapOf(
            AdvertiseManager.SPACE_OPEN to AdmobTransmitter(AdvertiseManager.SPACE_OPEN),
            AdvertiseManager.SPACE_HOME to AdmobTransmitter(AdvertiseManager.SPACE_HOME),
            AdvertiseManager.SPACE_RESULT to AdmobTransmitter(AdvertiseManager.SPACE_RESULT),
            AdvertiseManager.SPACE_CONNECT to AdmobTransmitter(AdvertiseManager.SPACE_CONNECT),
            AdvertiseManager.SPACE_BACK to AdmobTransmitter(AdvertiseManager.SPACE_BACK),
            AdvertiseManager.SPACE_RETURN to AdmobTransmitter(AdvertiseManager.SPACE_RETURN),
            AdvertiseManager.SPACE_BACK_BOOKMARK to AdmobTransmitter(AdvertiseManager.SPACE_BACK_BOOKMARK),
            AdvertiseManager.SPACE_BACK_READ_LIST to AdmobTransmitter(AdvertiseManager.SPACE_BACK_READ_LIST),
            AdvertiseManager.SPACE_DISCOVER_RE_OPEN to AdmobTransmitter(AdvertiseManager.SPACE_DISCOVER_RE_OPEN),
            AdvertiseManager.SPACE_DISCOVER_HISTORY to AdmobTransmitter(AdvertiseManager.SPACE_DISCOVER_HISTORY),
        )
    }
    private val mShower by lazy {
        AdmobShower
    }

    override fun initialize() {
        MobileAds.initialize(FastSearch.mSelf)
        AdBackgroundTask.initialize()
    }

    override fun request(request: Request) {
        mRequester[request.space]?.transmit(request)
    }

    override fun cancelRequest(request: Request) {
        mRequester[request.space]?.cancelTransmit(request)
    }

    override fun getRequestParams(space: String, receiver: (params: List<Param>) -> Unit) {

    }

    override fun isLimited(): Boolean {
        return false
    }

    override fun onAdClicked() {

    }

    override fun onAdShowed(isFullScreen: Boolean) {

    }

    override fun onFullScreenAdDismiss() {

    }

    override fun isShowingFullScreen(): Boolean {
        return true
    }

    override fun showFullScreen(
        fullScreenShowParam: FullScreenShowParam,
        continuation: () -> Unit
    ) {
        mShower.showFullScreen(fullScreenShowParam, continuation)
    }

    override fun showNative(param: NativeShowParam, continuation: (() -> Unit)?) {
        mShower.showNative(param, continuation)
    }

    override fun clearCache(space: String) {
        mRequester[space]?.clearCache()
    }

    override fun clearNativeAd(native: Any?) {
        (native as? NativeAd)?.destroy()
    }
}