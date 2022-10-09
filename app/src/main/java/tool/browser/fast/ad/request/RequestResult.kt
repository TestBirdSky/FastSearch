package tool.browser.fast.ad.request

@Suppress("MemberVisibilityCanBePrivate")
class RequestResult private constructor(
    val code: String,
    val data: Any?
) {
    private var createTime = System.currentTimeMillis()

    val isSuccessful: Boolean
        get() {
            return code == CODE_SUCCESS && data != null
        }
    val isLimited: Boolean
        get() {
            return code == CODE_LIMIT
        }

    fun isExpired(): Boolean {
        return System.currentTimeMillis() - createTime >= 3600000L
    }

    companion object {
        const val CODE_SUCCESS = "success"
        const val CODE_NO_AD = "no_ad"
        const val CODE_LIMIT = "limit"

        fun forLimit(): RequestResult {
            return forError(CODE_LIMIT)
        }

        fun forNoAd(): RequestResult {
            return forError(CODE_NO_AD)
        }

        fun forError(code: String): RequestResult {
            return RequestResult(
                code, null
            )
        }

        fun forResult(data: Any): RequestResult {
            return RequestResult(
                CODE_SUCCESS,
                data
            )
        }
    }
}