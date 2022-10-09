package tool.browser.fast.ad.show.param

import tool.browser.fast.ad.manager.IAdvertiseManager
import tool.browser.fast.ad.request.RequestResult

open class ShowParam(
    @IAdvertiseManager.Space val space: String,
    val result: RequestResult
)