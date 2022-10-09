package tool.browser.fast.ad.request

import tool.browser.fast.ad.manager.AdvertiseManager
import tool.browser.fast.ad.manager.IAdvertiseManager

class Request private constructor() {

    @IAdvertiseManager.Space
    lateinit var space: String
    var callback: ((result: RequestResult) -> Unit)? = null

    companion object {
        fun build(receiver: Request.() -> Unit): Request {
            val request = Request()
            receiver(request)
            return request
        }

        fun forOpen(result: ((result: RequestResult) -> Unit)? = null): Request {
            return build {
                space = AdvertiseManager.SPACE_OPEN
                callback = result
            }
        }

        fun forHome(result: ((result: RequestResult) -> Unit)? = null): Request {
            return build {
                space = AdvertiseManager.SPACE_HOME
                callback = result
            }
        }

        fun forConnect(result: ((result: RequestResult) -> Unit)? = null): Request {
            return build {
                space = AdvertiseManager.SPACE_CONNECT
                callback = result
            }
        }

        fun forResult(result: ((result: RequestResult) -> Unit)? = null): Request {
            return build {
                space = AdvertiseManager.SPACE_RESULT
                callback = result
            }
        }

        fun forBack(result: ((result: RequestResult) -> Unit)? = null): Request {
            return build {
                space = AdvertiseManager.SPACE_BACK
                callback = result
            }
        }
    }
}