package tool.browser.fast.ad.manager

import androidx.annotation.StringDef
import tool.browser.fast.ad.request.Param
import tool.browser.fast.ad.request.Request
import tool.browser.fast.ad.show.param.FullScreenShowParam
import tool.browser.fast.ad.show.param.NativeShowParam

interface IAdvertiseManager {

    @StringDef(
        AdvertiseManager.SPACE_OPEN,
        AdvertiseManager.SPACE_HOME,
        AdvertiseManager.SPACE_RESULT,
        AdvertiseManager.SPACE_CONNECT,
        AdvertiseManager.SPACE_BACK
    )
    annotation class Space

    fun initialize()

    fun request(request: Request)

    fun cancelRequest(request: Request)

    fun getRequestParams(@Space space: String, receiver: (params: List<Param>) -> Unit)

    fun isLimited(): Boolean

    fun onAdClicked()

    fun onAdShowed(isFullScreen: Boolean)

    fun onFullScreenAdDismiss()

    fun isShowingFullScreen(): Boolean

    fun showFullScreen(fullScreenShowParam: FullScreenShowParam, continuation: () -> Unit)

    fun showNative(param: NativeShowParam, continuation: (() -> Unit)? = null)

    fun clearCache(@Space space: String)

    fun clearNativeAd(native: Any?)
}