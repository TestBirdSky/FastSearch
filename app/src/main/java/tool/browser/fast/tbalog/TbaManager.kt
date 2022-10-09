package tool.browser.fast.tbalog

import android.app.Application
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.webkit.WebSettings
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import tool.browser.fast.ad.log
import tool.browser.fast.component.FastSearch
import tool.browser.fast.network.OkHttpManager
import tool.browser.fast.tbalog.TabUtils.getNetworkType
import tool.browser.fast.tbalog.TabUtils.networkIsAvailable
import tool.browser.fast.utils.StringCacheDelegate
import java.io.IOException

//
object TbaManager {
    private const val TAG = "TbaManager"
    private val mApp by lazy { FastSearch.mSelf }
    private var deviceModel = Build.MODEL //设备型号
    private var versionCode = Build.VERSION.RELEASE //os_version
    private var cpu_type = Build.CPU_ABI //cpu 类型
    private const val BASE_URL = "http://ntaguedecode-ce1be92aced2bf9a.elb.us-east-1.amazonaws.com/montague/decode/placater/greed"
    private const val RELEASE_URL = "https://viscera.aabnr.com/however/bismarck"
    private val mUrl = "$RELEASE_URL?look=%s&chimique=%s&mitt=%s"

    fun tbaInit() {
        postInstaller(mApp)
        postSessionEvent()
    }

    private fun postInstallerLog(
        commJson: String,
        logId: String,
        gaid: String?,
        networkType: String,
        retryNum: Int = 0,
        isNullReferrer: Boolean = false
    ) {
        val url = String.format(mUrl, TabUtils.distinctIdMd5(), gaid, TabUtils.getTimeZoneOffset())
        log(TAG, "url--->$url")
        OkHttpManager.request(url, commJson, logId, networkType, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                log(TAG, "onFailure $e ---postInstallerLog$commJson ")
                if (mApp.networkIsAvailable()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (retryNum < MAX_RETRY) {
                            delay(5000)
                            postInstallerLog(commJson, logId, gaid, networkType, retryNum + 1, isNullReferrer)
                        } else {
                            delay(10000)
                            postInstaller(mApp)
                        }
                    }
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        while (!mApp.networkIsAvailable()) {
                            delay(1000)
                        }
                        postInstaller(mApp)
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                log(TAG, "onResponse $ ---postInstallerLog$commJson $response")
                if (response.isSuccessful && response.code == 200) {
                    if (!isNullReferrer)
                        postInstallLogStatus = "true"
                }
            }
        })
    }

    private const val MAX_RETRY = 3
    private fun postSessionLog(commJson: String, logId: String, gaid: String?, networkType: String, retryNum: Int = 0) {
        val url = String.format(mUrl, TabUtils.distinctIdMd5(), gaid, TabUtils.getTimeZoneOffset())
        log(TAG, "url--->$url")
        OkHttpManager.request(url, commJson, logId, networkType, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                log(TAG, "onFailure $e ---postSessionLog$commJson ")
                if (mApp.networkIsAvailable()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (retryNum < MAX_RETRY) {
                            delay(10000)
                            postSessionLog(commJson, logId, gaid, networkType, retryNum + 1)
                        } else {
                            delay(20000)
                            postSessionEvent()
                        }
                    }
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        while (!mApp.networkIsAvailable()) {
                            delay(1000)
                        }
                        postInstaller(mApp)
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                log(TAG, "postSessionLog  $commJson onResponse response $response ")
                if (response.isSuccessful && response.code == 200) {
                    return
                }
                if (mApp.networkIsAvailable()) {
                    if (retryNum < MAX_RETRY) {
                        postSessionLog(commJson, logId, gaid, networkType, retryNum + 1)
                    }
                }
            }
        })
    }


    private fun createBrevity(ip: String, gaid: String?, networkType: String): JsonObject {
        return JsonObject().apply {
            addProperty("moiety", networkType)
            addProperty("chimique", gaid ?: "")
            addProperty("bygone", ip)
            addProperty("woodpeck", deviceModel)
            addProperty("thatd", versionCode)
            addProperty("neuroses", cpu_type)
            addProperty("look", TabUtils.distinctIdMd5())
            addProperty("cufflink", TabUtils.getLanguageAndCountry())

        }
    }

    private fun createCagey(logId: String): JsonObject {
        return JsonObject().apply {
            addProperty("fairgoer", TabUtils.getManufacturer())
            addProperty("sanchez", "gp")
            addProperty("jeremiah", logId)
            addProperty("wingback", TabUtils.getPackageName(FastSearch.mSelf))
            addProperty("biltmore", TabUtils.getOperator(FastSearch.mSelf))
            addProperty("august", "")
            addProperty("stoic", TabUtils.getAppVersion(FastSearch.mSelf))
            addProperty("goodbye", TabUtils.getAndroidId())
            addProperty("mitt", TabUtils.getTimeZoneOffset())
            addProperty("pod", System.currentTimeMillis())
            addProperty("odium", "festive")
            addProperty("lame", TabUtils.getDevBrand())
            addProperty("etruria", TabUtils.getPhoneCountryCode())
        }
    }

    private lateinit var referrerClient: InstallReferrerClient
    private var postInstallLogStatus by StringCacheDelegate(def = "false") { "post_install_status" }
    var mInstallReferrer: String by StringCacheDelegate { "install_referrer" }
        private set

    private fun postInstaller(app: Application) {
        if (postInstallLogStatus == "false" || TextUtils.isEmpty(mInstallReferrer)) {
            referrerClient = InstallReferrerClient.newBuilder(app).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    Log.i("TBA", "get Referrer ---onInstallReferrerSetupFinished $responseCode ")
                    val response: ReferrerDetails?
                    try {
                        when (responseCode) {
                            InstallReferrerClient.InstallReferrerResponse.OK -> {
                                // Connection established.
                                response = referrerClient.installReferrer
                                mInstallReferrer = response?.installReferrer ?: ""
                                log("=mInstallReferrer", mInstallReferrer)
                                postInstallerLog(response)
                                referrerClient.endConnection()
                            }
                            else -> {
                                postInstallerLog(null)
                                referrerClient.endConnection()
                            }
                        }
                    } catch (e: Exception) {
                        postInstallerLog(null)
                    }
                }

                override fun onInstallReferrerServiceDisconnected() {
                    Log.i("TBA", "postReferrerLog ---onInstallReferrerServiceDisconnected")
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                }
            })
        }
    }

    private fun postInstallerLog(referrer: ReferrerDetails?) {
        CoroutineScope(Dispatchers.IO).launch {
            val gaid = TabUtils.getGaid(FastSearch.mSelf)
            val logId = TabUtils.getLogId()
            var ip = OkHttpManager.getPublicIPAndCon().ip
            val networkType = getNetworkType(mApp)
            val brevityJson = createBrevity(ip, gaid, networkType)
            val commJson = JsonObject().apply {
                add("brevity", brevityJson)
                add("cagey", createCagey(logId))
                addProperty("seoul", "build/${Build.ID}")
                addProperty("puffin", referrer?.installReferrer ?: "")
                addProperty("redolent", referrer?.installVersion ?: "")
                //webview中的user_agent, 注意为webview的，android中的useragent有;wv关键字
                addProperty("lace", WebSettings.getDefaultUserAgent(mApp))
                addProperty("topple", "hidden")
                addProperty("nib", "up")
                addProperty("smelt", referrer?.referrerClickTimestampSeconds ?: 0)
                addProperty("negligee", referrer?.installBeginTimestampSeconds ?: 0)
                addProperty("mutagen", referrer?.referrerClickTimestampServerSeconds ?: 0)
            }
            postInstallerLog(commJson.toString(), logId, gaid, networkType, isNullReferrer = referrer != null)
        }
    }

    private fun postSessionEvent() {
        CoroutineScope(Dispatchers.IO).launch {
            val gaid = TabUtils.getGaid(FastSearch.mSelf)
            val logId = TabUtils.getLogId()
            var ip = OkHttpManager.getPublicIPAndCon().ip
            val networkType = getNetworkType(mApp)
            val brevityJson = createBrevity(ip, gaid, networkType)
            val commJson = JsonObject().apply {
                add("brevity", brevityJson)
                add("cagey", createCagey(logId))
                add("wig", JsonObject())
            }
            postSessionLog(commJson.toString(), logId, gaid, networkType)
        }
    }

}