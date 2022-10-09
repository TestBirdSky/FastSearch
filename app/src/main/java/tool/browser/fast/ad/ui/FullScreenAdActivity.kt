package tool.browser.fast.ad.ui

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import tool.browser.fast.ad.manager.AdvertiseManager
import tool.browser.fast.ad.manager.IAdvertiseManager
import tool.browser.fast.ad.request.Request
import tool.browser.fast.ad.request.RequestResult
import tool.browser.fast.ad.show.param.FullScreenShowParam
import tool.browser.fast.component.BaseActivity
import tool.browser.fast.component.BaseViewModel

abstract class FullScreenAdActivity<Binding : ViewDataBinding, Model : BaseViewModel>(
    @LayoutRes private val layoutResId: Int,
    viewModelClass: Class<Model>
) :
    BaseActivity<Binding, Model>(
        layoutResId, viewModelClass
    ) {

    protected var mAd: RequestResult? = null
        private set
    private var mAdRequest = Request.build {
        space = getAdSpace()
        callback = {
            clearAd()
            mAd = it
        }
    }

    @IAdvertiseManager.Space
    protected abstract fun getAdSpace(): String

    protected open fun isRequestAdWhenCreate(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isRequestAdWhenCreate()) {
            requestAd()
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun requestAd() {
        AdvertiseManager.request(mAdRequest)
    }

    protected fun showFullScreenAd(receiver: () -> Unit) {
        val result = mAd
        if (result?.isSuccessful == true) {
            AdvertiseManager
                .showFullScreen(
                    FullScreenShowParam(
                        space = getAdSpace(),
                        context = this,
                        result = result
                    ), receiver
                )
        } else {
            receiver()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearAd()
    }

    private fun clearAd() {
        mAd = null
        AdvertiseManager.cancelRequest(mAdRequest)
    }
}