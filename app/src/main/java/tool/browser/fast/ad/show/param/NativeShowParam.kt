package tool.browser.fast.ad.show.param

import android.view.View
import tool.browser.fast.ad.request.RequestResult

class NativeShowParam(
    space: String,
    result: RequestResult,
    val rootView: View
) : ShowParam(space, result)