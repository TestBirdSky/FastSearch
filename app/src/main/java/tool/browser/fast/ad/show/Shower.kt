package tool.browser.fast.ad.show

import tool.browser.fast.ad.manager.AdvertiseManager
import tool.browser.fast.ad.show.param.FullScreenShowParam
import tool.browser.fast.ad.show.param.NativeShowParam

abstract class Shower {

    fun showFullScreen(
        param: FullScreenShowParam,
        continuation: () -> Unit
    ) {
        if (canShow(param)) {
            onShowFullScreen(param, continuation)
        } else {
            continuation()
        }
    }

    protected abstract fun onShowFullScreen(
        param: FullScreenShowParam,
        continuation: () -> Unit
    )

    fun showNative(
        param: NativeShowParam,
        continuation: (() -> Unit)? = null
    ) {
        onShowNative(param, continuation)
    }

    protected abstract fun onShowNative(
        param: NativeShowParam,
        continuation: (() -> Unit)?
    )

    private fun canShow(param: FullScreenShowParam): Boolean {
        return param.context.mIsOnResume
                && !AdvertiseManager.isShowingFullScreen()
                && !AdvertiseManager.isLimited()
    }
}