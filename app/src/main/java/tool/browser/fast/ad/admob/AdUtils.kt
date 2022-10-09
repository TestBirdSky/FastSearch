package tool.browser.fast.ad.admob

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import tool.browser.fast.R
import tool.browser.fast.ad.Cache
import tool.browser.fast.ad.logForAd
import tool.browser.fast.ad.request.Param

object AdUtils {
    private val impl by lazy { AdImpl() }

    fun loadAd(space: AdSpace, loadFailed: () -> Unit = {}) {
        if (Cache.isAdUpperLimit()) {
            return
        }
        if (space.isLoading) {
            return
        }
        if (space.adDataWrap.ad != null) {
            addLogi("have ad cache==  ${space.spaceName}")
            return
        }
        Cache.getListParam(space.spaceName) {
            val iterator = it.iterator()
            if (iterator.hasNext()) {
                space.isLoading = true
                load(space, iterator, loadFailed)
            } else {
                addLoge("no find ad configure ${space.spaceName}")
            }
        }

    }

    private fun load(
        space: AdSpace,
        iterator: Iterator<Param>,
        loadFailed: () -> Unit
    ) {
        val con = iterator.next()
        addLogi("load ad ${space.spaceName} --${con.sort}---${con.dataId}")
        impl.loadAd(con.type, con.dataId, {
            addLoge("load ad failed ${space.spaceName} --${con.sort}---${con.dataId}")
            if (iterator.hasNext()) {
                load(space, iterator, loadFailed)
            } else {
                space.isLoading = false
                loadFailed.invoke()

            }
        }, {
            addLogi("load ad success ${space.spaceName} --${con.sort}---${con.dataId}")
            space.isLoading = false
            space.adDataWrap.ad = it
        })
    }

    private fun addLogi(msg: String) {
        logForAd("", msg)
    }

    private fun addLoge(msg: String) {
        logForAd("", msg)
    }

    fun showAd(space: AdSpace, activity: Activity, adClose: () -> Unit): Boolean {
        val call = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                adClose.invoke()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                adClose.invoke()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                Cache.mClickCount++
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                Cache.mShowCount++
            }
        }
        when (val ad = space.adDataWrap.ad) {
            is AppOpenAd -> {
                ad.fullScreenContentCallback = call
                ad.show(activity)
            }
            is InterstitialAd -> {
                ad.fullScreenContentCallback = call
                ad.show(activity)
            }
            else -> return false
        }
        space.adDataWrap.ad = null
        addLogi("show ad--> ${space.spaceName}")
        return true
    }

    fun getNativeView(
        space: AdSpace,
        layoutInflater: LayoutInflater,
        layoutId: Int
    ): NativeAdView? {
        val nativeAd = space.adDataWrap.ad
        if (nativeAd is NativeAd) {
            val adView = layoutInflater.inflate(layoutId, null) as NativeAdView
            adView.mediaView = adView.findViewById(R.id.n_media_view)
            adView.headlineView = adView.findViewById(R.id.n_title_view)
            adView.callToActionView = adView.findViewById(R.id.n_action_view)
            adView.iconView = adView.findViewById(R.id.n_icon_view)
            adView.bodyView = adView.findViewById(R.id.n_body_view)
            (adView.headlineView as TextView).text = nativeAd.headline
            nativeAd.mediaContent?.let {
                adView.mediaView?.setMediaContent(it)
            }
            if (nativeAd.body == null) {
                adView.bodyView?.visibility = View.INVISIBLE
            } else {
                adView.bodyView?.visibility = View.VISIBLE
                (adView.bodyView as TextView).text = nativeAd.body
            }
            if (nativeAd.callToAction == null) {
                adView.callToActionView?.visibility = View.INVISIBLE
            } else {
                adView.callToActionView?.visibility = View.VISIBLE
                (adView.callToActionView as TextView).text = nativeAd.callToAction
            }
            if (nativeAd.icon == null) {
                adView.iconView?.visibility = View.INVISIBLE
            } else {
                (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon?.drawable
                )
                adView.iconView?.visibility = View.VISIBLE
            }
            adView.setNativeAd(nativeAd)
            addLogi("show ad--> ${space.spaceName}")
            space.adDataWrap.ad = null
            Cache.mShowCount++
            return adView
        } else {
            return null
        }
    }


}