package tool.browser.fast.tbalog

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import tool.browser.fast.component.FastSearch
import tool.browser.fast.utils.StringCacheDelegate
import java.util.*
import android.telephony.TelephonyManager
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.security.MessageDigest
import java.util.regex.Matcher
import java.util.regex.Pattern
import android.net.ConnectivityManager

import android.net.NetworkInfo
import android.util.Log


var distinctIdMd5 by StringCacheDelegate(def = "") { "distinct_id" }

object TabUtils {
    fun distinctIdMd5(): String {
        if (TextUtils.isEmpty(distinctIdMd5)) {
            val id = getAndroidId()
            distinctIdMd5 = md5(id)
        }
        return distinctIdMd5
    }

    private fun md5(content: String): String {
        val hash = MessageDigest.getInstance("MD5").digest(content.toByteArray())
        val hex = StringBuilder(hash.size * 2)
        for (b in hash) {
            var str = Integer.toHexString(b.toInt())
            if (b < 0x10) {
                str = "0$str"
            }
            hex.append(str.substring(str.length - 2))
        }
        return hex.toString()
    }

    fun getAndroidId(): String {
        var androidId = ""
        runCatching {
            androidId = Settings.Secure.getString(
                FastSearch.mSelf.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }
        if (TextUtils.isEmpty(androidId)) {
            androidId = getUUID()
        }
        return androidId
    }

    fun getLogId(): String {
        return getUUID()
    }

    fun getUUID(): String {
        return UUID.randomUUID().toString()
    }

    fun getLanguageAndCountry(): String {
        return Locale.getDefault().language + "_" + Locale.getDefault().country
    }

    fun getPhoneCountryCode(): String {
        return Locale.getDefault().country
    }

    fun getPackInfo(context: Context): PackageInfo {
        val pm = context.packageManager
        return pm.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
    }

    fun getPackageName(context: Context): String {
        return getPackInfo(context).packageName
    }

    fun getAppVersion(context: Context): String {
        return getPackInfo(context).versionName
    }

    fun getOperator(app: Application): String {
        val manager = app.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val operator = manager.networkOperator
        Log.i("getOperator", "getOperator: $operator")
        var mcc = ""
        var mnc = ""
        runCatching {
//            /**通过operator获取 MCC 和MNC  */
//            mcc = operator.substring(0, 3)
//            mnc = operator.substring(3)
            return operator
        }
        return ""
    }

    fun getGaid(context: Context): String? {
        return AdvertisingIdClient.getAdvertisingIdInfo(context).id
    }

    suspend fun getCpuName(): String {
        return withContext(Dispatchers.IO) {
            val str1 = "/proc/cpuinfo"
            var str2: String? = ""
            var cpuName = ""
            try {
                val fileReader = FileReader(str1)
                val bufferedReader = BufferedReader(fileReader)
                while (bufferedReader.readLine().also { str2 = it } != null) {
                    if (TextUtils.isEmpty(str2) || str2 == null) {
                        continue
                    }
                    val list = str2!!.split(":")
                    if (TextUtils.equals(list[0], "Hardware") && list.size > 1) {
                        cpuName = list[1]
                        break
                    }
                }
                bufferedReader.close()
                fileReader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            cpuName
        }

    }

    fun isCorrectIp(ipAddress: String?): Boolean {
        val ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}"
        val pattern: Pattern = Pattern.compile(ip)
        val matcher: Matcher = pattern.matcher(ipAddress)
        return matcher.matches()
    }

    //    var deviceModel = Build.MODEL //设备型号
//    var versionCode = Build.VERSION.RELEASE //os_version
    //设备 名称
    fun getDevBrand(): String {
        return Build.BRAND
    }

    // phone 厂商
    fun getManufacturer(): String {
        return Build.MANUFACTURER
    }

    // raw to change
    fun getTimeZoneOffset(): Int {
        return TimeZone.getDefault().rawOffset / 3600 / 1000
    }

    fun isWifiConnected(context: Context): Boolean {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiNetworkInfo = connectivityManager
            .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return wifiNetworkInfo!!.isConnected
    }

    fun Context.networkIsAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //如果仅仅是用来判断网络连接 //则可以使用 cm.getActiveNetworkInfo().isAvailable();
        val info = cm.allNetworkInfo
        for (i in info.indices) {
            if (info[i].state == NetworkInfo.State.CONNECTED) {
                return true
            }
        }
        return false
    }

    fun getNetworkType(context: Context): String {
        return if (context.networkIsAvailable()) {
            if (isWifiConnected(context)) {
                "wifi"
            } else {
                "4g"
            }
        } else {
            "unknown"
        }
    }
}
