package tool.browser.fast.ad.admob

enum class AdSpace(val spaceName:String,var isLoading:Boolean,val adDataWrap: AdDataWrap) {
    SPACE_OPEN("fsp_op",false,AdDataWrap()),
    SPACE_BACK_BOOKMARK("fsp_bk_bm",false,AdDataWrap()),
    SPACE_BACK_READ_LIST("fsp_bk_rl",false,AdDataWrap()),
    SPACE_DISCOVER_RE_OPEN("fsp_discover_re_open",false, AdDataWrap()),
    SPACE_DISCOVER_HISTORY("fsp_discover_history",false, AdDataWrap()),
}