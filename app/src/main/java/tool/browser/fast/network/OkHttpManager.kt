package tool.browser.fast.network

import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit


import okhttp3.RequestBody
import tool.browser.fast.tbalog.TabUtils
import javax.net.ssl.*

object OkHttpManager {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient().newBuilder()
            .writeTimeout(2, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.SECONDS)
//            .hostnameVerifier(TrustAllHostnameVerifier())
//            .sslSocketFactory(createSSLSocketFactory())
            .connectTimeout(2, TimeUnit.SECONDS)
            .build()
    }

    fun request(url: String, body: String, logId: String, networkType: String, callback: Callback) {
        val requestBody: RequestBody =
            RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), body)
        val request = Request.Builder().url(url)
            .addHeader("jeremiah", logId)
            .addHeader("moiety", networkType)
            .post(requestBody).build()
        okHttpClient.newCall(request).enqueue(callback)
    }

    fun request(url: String, body: String, callback: Callback) {
        val requestBody: RequestBody =
            RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), body)
        val request = Request.Builder().url(url).post(requestBody)
            .build()
        okHttpClient.newCall(request).enqueue(callback)
    }

    private fun createSSLSocketFactory(): SSLSocketFactory {
        var ssfFactory: SSLSocketFactory? = null
        val sc = SSLContext.getInstance("TLS")
        sc.init(null, arrayOf<TrustManager>(TrustAllCerts()), SecureRandom());
        ssfFactory = sc.socketFactory
        return ssfFactory
    }

    data class IPBean(var ip: String, val country: String)

    private const val GET_IP_URL = "https://api.myip.com/"
    suspend fun getPublicIPAndCon(): IPBean {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(GET_IP_URL)
                .build()
            var ipBean = IPBean("", "")
            try {
                val r = okHttpClient.newCall(request).execute()
                if (r.isSuccessful) {
                    val body = r.body?.string()
                    if (!TextUtils.isEmpty(body)) {
                        try {
                            val g = Gson()
                            ipBean = g.fromJson(body, IPBean::class.java)
                            if (!TabUtils.isCorrectIp(ipBean.ip)) {
                                ipBean.ip = ""
                            }
                            return@withContext ipBean
                        } catch (e: Exception) {
                            Log.e("TAG", "getPublicIPAndCon: $e")
                            ipBean = IPBean("", "")
                        }
                    }
                } else {
                    ipBean = IPBean("", "")
                }
                return@withContext ipBean
            } catch (e: Exception) {
                Log.e("TAG", "getPublicIPAndCon: $e")
            }
            return@withContext ipBean
        }
    }
}


private class TrustAllHostnameVerifier : HostnameVerifier {
    override fun verify(hostname: String?, session: SSLSession?): Boolean {
        return true
    }
}

private class TrustAllCerts : X509TrustManager {
    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

    }

    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {

    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return arrayOf<X509Certificate>()
    }
}
