package tool.browser.fast.ad.manager

import com.blankj.utilcode.util.ResourceUtils
import tool.browser.fast.ad.Cache
import tool.browser.fast.ad.admob.AdmobManager
import tool.browser.fast.ad.log
import tool.browser.fast.ad.logForAd
import tool.browser.fast.ad.request.Param
import tool.browser.fast.remote.ConfigManager
import tool.browser.fast.utils.IntCacheDelegate
import tool.browser.fast.utils.optListObj
import tool.browser.fast.utils.toJsonObjectOrNull
import tool.browser.fast.utils.toSimpleDateStr

object AdvertiseManager : IAdvertiseManager by AdmobManager {
    const val SPACE_OPEN = "fsp_op"
    const val SPACE_HOME = "fsp_hm"
    const val SPACE_RESULT = "fsp_ru"
    const val SPACE_CONNECT = "fsp_cc"
    const val SPACE_BACK = "fsp_bk"
    const val SPACE_RETURN = "fsp_rt"
    const val SPACE_BACK_BOOKMARK = "fsp_bk_bm"
    const val SPACE_BACK_READ_LIST = "fsp_bk_rl"
    const val SPACE_DISCOVER_RE_OPEN = "fsp_discover_re_open"
    const val SPACE_DISCOVER_HISTORY = "fsp_discover_history"

    private const val mDefSource = "admob"

    private val mLocalAdJson: String by lazy {
        ResourceUtils.readAssets2String("ad_manifest.json")
    }
//    private var mClickCount: Int by IntCacheDelegate {
//        "${
//            System.currentTimeMillis().toSimpleDateStr()
//        }_CLICK_COUNT"
//    }
//    private var mShowCount: Int by IntCacheDelegate {
//        "${
//            System.currentTimeMillis().toSimpleDateStr()
//        }_SHOW_COUNT"
//    }

    private var mIsShowingFullScreen = false

    override fun getRequestParams(space: String, receiver: (params: List<Param>) -> Unit) {
        receiver(
            (parseRequestParam(ConfigManager.mAdManifestJson, space)
                .ifEmpty {
                    parseRequestParam(mLocalAdJson, space)
                })
                .filter {
                    it.source == mDefSource
                }
                .sortedByDescending {
                    it.sort
                }
        )
    }

    override fun isLimited(): Boolean {
        return Cache.isAdUpperLimit()
    }

    override fun onAdClicked() {
        Cache.mClickCount++
    }

    override fun onAdShowed(isFullScreen: Boolean) {
        Cache.mShowCount++
        if (isFullScreen) {
            mIsShowingFullScreen = true
        }
    }

    override fun onFullScreenAdDismiss() {
        mIsShowingFullScreen = false
    }

    override fun isShowingFullScreen(): Boolean {
        return mIsShowingFullScreen
    }

    fun parseLimitField(key: String): Int {
        return ConfigManager.mAdManifestJson.toJsonObjectOrNull()
            ?.optInt(key) ?: (mLocalAdJson.toJsonObjectOrNull()
            ?.optInt(key) ?: 50)
    }

    private fun parseRequestParam(json: String, space: String): List<Param> {
        return json.toJsonObjectOrNull()
            ?.optJSONArray(space)
            ?.optListObj {
                Param.from(it)
            } ?: emptyList()
    }
}