package tool.browser.fast.ad

import android.annotation.SuppressLint
import android.util.Log
import tool.browser.fast.component.FastSearch

@SuppressLint("LogNotTimber")
fun log(tag: String = "", content: String) {
    if (!FastSearch.IS_DEBUG) return
    Log.e("FastSearch_${tag}", content)
}

fun logForAd(space: String = "", content: String) {
    log("AD[${space}]", content)
}