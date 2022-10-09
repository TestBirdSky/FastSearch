package tool.browser.fast.ad.admob

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
import tool.browser.fast.ad.manager.AdvertiseManager
import tool.browser.fast.ad.manager.AdvertiseManager.SPACE_HOME
import tool.browser.fast.ad.request.Param
import tool.browser.fast.ad.request.RequestResult
import tool.browser.fast.ad.request.RequestTransmitter
import tool.browser.fast.component.FastSearch

class AdmobTransmitter(space: String) : RequestTransmitter(space) {
    override fun onRequest(
        param: Param,
        receiver: (result: RequestResult) -> Unit
    ) {
        when (param.type) {
            "o" -> {
                AppOpenAd.load(
                    FastSearch.mSelf,
                    param.dataId,
                    AdRequest.Builder().build(),
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                    object : AppOpenAd.AppOpenAdLoadCallback() {
                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            onRequestError(receiver, p0)
                        }

                        override fun onAdLoaded(p0: AppOpenAd) {
                            super.onAdLoaded(p0)
                            onRequestSuccess(receiver, p0)
                        }
                    }
                )
            }
            "n" -> {
                AdLoader
                    .Builder(
                        FastSearch.mSelf,
                        param.dataId
                    )
                    .forNativeAd {
                        onRequestSuccess(receiver, it)
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdClicked() {
                            super.onAdClicked()
                            log("Clicked.")
                            if (space == SPACE_HOME) {
                                FastSearch.isShowMainNAd = true
                            }
                            AdvertiseManager.onAdClicked()
                        }

                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            onRequestError(receiver, p0)
                        }
                    })
                    .withNativeAdOptions(
                        NativeAdOptions.Builder()
                            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_LEFT)
                            .build()
                    )
                    .build()
                    .loadAd(
                        AdRequest.Builder().build()
                    )
            }
            "i" -> {
                InterstitialAd
                    .load(
                        FastSearch.mSelf,
                        param.dataId,
                        AdRequest.Builder().build(),
                        object : InterstitialAdLoadCallback() {
                            override fun onAdFailedToLoad(p0: LoadAdError) {
                                super.onAdFailedToLoad(p0)
                                onRequestError(receiver, p0)
                            }

                            override fun onAdLoaded(p0: InterstitialAd) {
                                super.onAdLoaded(p0)
                                onRequestSuccess(receiver, p0)
                            }
                        }
                    )
            }
        }
    }

    private fun onRequestSuccess(
        receiver: (result: RequestResult) -> Unit,
        data: Any
    ) {
        receiver(RequestResult.forResult(data))
    }

    private fun onRequestError(
        receiver: (result: RequestResult) -> Unit,
        error: LoadAdError
    ) {
        receiver(RequestResult.forError(error.message))
    }
}