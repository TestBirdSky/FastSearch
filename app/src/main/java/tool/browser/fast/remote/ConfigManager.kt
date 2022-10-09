package tool.browser.fast.remote

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import tool.browser.fast.ad.log
import tool.browser.fast.utils.StringCacheDelegate

object ConfigManager {
    const val FAST_CITY = "fsct567"
    const val SERVER_NODE = "fsnd023"

    @Suppress("MemberVisibilityCanBePrivate")
    const val PRIVACY_GUIDE_SWITCH = "fspg11"

    @Suppress("MemberVisibilityCanBePrivate")
    const val AD_MANIFEST = "fsad389"

    var mFastCityJson: String by StringCacheDelegate { "FAST_CITY" }
        private set
    var mServerNodeJson: String by StringCacheDelegate { "SERVER_NODE" }
        private set
    var mAdManifestJson: String by StringCacheDelegate { "AD_MANIFEST" }
        private set
    var mPrivacyGuideSwitch: String by StringCacheDelegate { "PRIVACY_GUIDE_SWITCH" }
        private set

    fun initialize() {
        mFastCityJson = ""
        mServerNodeJson = ""
        getConfigFromFirebase()
    }

    private fun getConfigFromFirebase() {
        Firebase.remoteConfig
            .apply {
                remoteConfigSettings {
                    minimumFetchIntervalInSeconds = 3600
                }
            }
            .fetchAndActivate()
            .addOnCompleteListener {
                val remoteConfig = Firebase.remoteConfig
                if (it.isSuccessful) {
                    mFastCityJson = remoteConfig.getString(FAST_CITY)
                    mServerNodeJson = remoteConfig.getString(SERVER_NODE)
                    mAdManifestJson = remoteConfig.getString(AD_MANIFEST)
                    mPrivacyGuideSwitch = remoteConfig.getString(PRIVACY_GUIDE_SWITCH)
                    log(content = "remoteConfig=== ${remoteConfig.getString(PRIVACY_GUIDE_SWITCH)}  $mFastCityJson --$mServerNodeJson")
                }
            }
    }

}