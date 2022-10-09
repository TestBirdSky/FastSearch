package tool.browser.fast.ad.admob

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
import tool.browser.fast.ad.logForAd
import tool.browser.fast.component.FastSearch

class AdImpl {
    private val mApp by lazy { FastSearch.mSelf }
    fun loadAd(type: String, id: String, loadFailed: () -> Unit, loadSuccess: (ad: Any) -> Unit) {
        when (type) {
            "i" -> {
                loadA(id, loadFailed, loadSuccess)
            }
            "o" -> {
                loadO(id, loadFailed, loadSuccess)
            }
            "n" -> {
                loadN(id, loadFailed, loadSuccess)
            }
        }
    }

    private fun loadA(id: String, loadFailed: () -> Unit, loadSuccess: (ad: Any) -> Unit) {
        val request = AdRequest.Builder().build()
        InterstitialAd.load(
            mApp,
            id,
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    loadFailed.invoke()
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    loadSuccess.invoke(p0)
                }
            }
        )
    }

    private fun loadO(id: String, loadFailed: () -> Unit, loadSuccess: (ad: Any) -> Unit) {
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            mApp,
            id,
            request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    loadFailed.invoke()
                }

                override fun onAdLoaded(p0: AppOpenAd) {
                    loadSuccess.invoke(p0)
                }
            }
        )
    }

    private fun loadN(id: String, loadFailed: () -> Unit, loadSuccess: (ad: Any) -> Unit) {
        val builder = AdLoader.Builder(mApp, id)
        builder.forNativeAd { nativeAd ->
            loadSuccess.invoke(nativeAd)
        }
        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                loadFailed.invoke()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                logForAd("","native onAdClicked")
//                CacheCar.addClickNum()
            }

            override fun onAdClosed() {
                super.onAdClosed()
                logForAd("","native onAdClosed")
            }
        }).withNativeAdOptions(
            NativeAdOptions.Builder()
                .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                .build()
        ).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }
}