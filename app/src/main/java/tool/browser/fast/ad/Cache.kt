package tool.browser.fast.ad

import com.blankj.utilcode.util.ResourceUtils
import tool.browser.fast.ad.manager.AdvertiseManager
import tool.browser.fast.ad.request.Param
import tool.browser.fast.utils.IntCacheDelegate
import tool.browser.fast.utils.toSimpleDateStr

object Cache {
    var mClickCount: Int by IntCacheDelegate {
        "${
            System.currentTimeMillis().toSimpleDateStr()
        }_CLICK_COUNT"
    }
    var mShowCount: Int by IntCacheDelegate {
        "${
            System.currentTimeMillis().toSimpleDateStr()
        }_SHOW_COUNT"
    }
    private var adClickLimitNum = 15
    private var adShowLimitNum = 50

    private fun setAdLimitData() {
        adClickLimitNum = AdvertiseManager.parseLimitField("fsp_click_num")
        adShowLimitNum = AdvertiseManager.parseLimitField("fsp_show_num")
        if (adClickLimitNum == 0) {
            adClickLimitNum =15
        }
        if (adShowLimitNum == 0) {
            adShowLimitNum = 50
        }
    }

    fun isAdUpperLimit(): Boolean {
        setAdLimitData()
        logForAd(content = "mClickCount$mClickCount==mShowCount$mShowCount --$adClickLimitNum  --$adShowLimitNum")
        if (mClickCount >= adClickLimitNum || mShowCount >= adShowLimitNum){
            logForAd(content = "ad limit...")
            return true
        }
        return false
    }

    fun getListParam( spaceName:String, receiver: (params: List<Param>) -> Unit){
        AdvertiseManager.getRequestParams(spaceName,receiver)
    }

}