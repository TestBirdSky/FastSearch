package tool.browser.fast.ad.admob

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import tool.browser.fast.R
import tool.browser.fast.ad.Cache
import tool.browser.fast.ad.logForAd
import tool.browser.fast.ad.manager.AdvertiseManager
import tool.browser.fast.ad.show.Shower
import tool.browser.fast.ad.show.param.FullScreenShowParam
import tool.browser.fast.ad.show.param.NativeShowParam
import tool.browser.fast.utils.gone
import tool.browser.fast.utils.show

object AdmobShower : Shower() {
    private class AdmobFullScreenCallback(
        private val param: FullScreenShowParam,
        private val continuation: () -> Unit
    ) : FullScreenContentCallback() {
        override fun onAdClicked() {
            super.onAdClicked()
            logForAd(param.space, "Clicked.")
            AdvertiseManager.onAdClicked()
        }

        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            logForAd(param.space, "Dismissed.")
            AdvertiseManager.onFullScreenAdDismiss()
            continuation()
        }

        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            super.onAdFailedToShowFullScreenContent(p0)
            logForAd(param.space, "Fail to show, message=${p0.message}.")
            AdvertiseManager.onFullScreenAdDismiss()
            AdvertiseManager.clearCache(param.space)
            continuation()
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            logForAd(param.space, "Showed.")
            AdvertiseManager.onAdShowed(true)
            AdvertiseManager.clearCache(param.space)
        }
    }

    override fun onShowFullScreen(param: FullScreenShowParam, continuation: () -> Unit) {
        when (val data = param.result.data) {
            is AppOpenAd -> {
                data.fullScreenContentCallback = AdmobFullScreenCallback(param, continuation)
                data.show(param.context)
            }
            is InterstitialAd -> {
                data.fullScreenContentCallback = AdmobFullScreenCallback(param, continuation)
                data.show(param.context)
            }
            else -> {
                continuation()
            }
        }
    }

    override fun onShowNative(param: NativeShowParam, continuation: (() -> Unit)?) {
        val nativeRoot = param.rootView.findViewById<NativeAdView>(R.id.n_native_view) ?: return
        val data = param.result.data as? NativeAd ?: return

        with(param.rootView) {
            findViewById<View>(R.id.n_holder)?.gone()
            findViewById<View>(R.id.n_content)?.show()

            findViewById<MediaView>(R.id.n_media_view)?.let {
                nativeRoot.mediaView = it
                data.mediaContent?.let { mc ->
                    it.setMediaContent(mc)
                    it.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                }
            }

            findViewById<ImageView>(R.id.n_icon_view)?.let {
                nativeRoot.iconView = it
                it.setImageDrawable(data.icon?.drawable)
            }

            findViewById<TextView>(R.id.n_title_view)?.let {
                nativeRoot.headlineView = it
                it.text = data.headline
            }

            findViewById<TextView>(R.id.n_action_view)?.let {
                nativeRoot.callToActionView = it
                it.text = data.callToAction
            }

            findViewById<TextView>(R.id.n_body_view)?.let {
                nativeRoot.bodyView = it
                it.text = data.body
            }
        }

        nativeRoot.setNativeAd(data)
        logForAd(param.space, "Showed.")
        Cache.mShowCount++
        AdvertiseManager.clearCache(param.space)
        continuation?.invoke()
    }
}