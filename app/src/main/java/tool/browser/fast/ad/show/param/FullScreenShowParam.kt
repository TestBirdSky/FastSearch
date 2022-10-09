package tool.browser.fast.ad.show.param

import tool.browser.fast.ad.request.RequestResult
import tool.browser.fast.component.BaseActivity

class FullScreenShowParam(
    space: String,
    val context: BaseActivity<*, *>,
    result: RequestResult
) : ShowParam(space, result)