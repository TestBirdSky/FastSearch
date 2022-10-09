package tool.browser.fast.browser.engine.android

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.core.net.toUri
import tool.browser.fast.R
import tool.browser.fast.browser.di.ComponentProvider
import tool.browser.fast.browser.engine.IWebEngine
import tool.browser.fast.browser.engine.WebEngineViewModel
import tool.browser.fast.browser.tab.HOME_PLACEHOLDER_URL
import tool.browser.fast.browser.tab.ITabHost
import tool.browser.fast.browser.tab.IView
import tool.browser.fast.browser.view.BaseContentView
import tool.browser.fast.database.BrowserHistory
import tool.browser.fast.database.RoomHelper
import tool.browser.fast.databinding.BrowserAndroidEngineBinding
import tool.browser.fast.utils.captureByDrawCache
import tool.browser.fast.utils.webIconUrlToLoadUrl

class AndroidWebEngine : BaseContentView<BrowserAndroidEngineBinding, WebEngineViewModel>(
    layoutResId = R.layout.browser_android_engine,
    viewModelClass = WebEngineViewModel::class.java
), IWebEngine {
    private var isIncognitoMode: Boolean = false
    private val mAndroidWebView: WebView
        get() {
            return mLayoutBinding.webView
        }
    private val mChromeClient by lazy {
        object : AndroidChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                withViewModel {
                    webLoadProgress.postValue(newProgress)
                }
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                if (title?.isNotBlank() == true) {
                    updateTitle(title)
                }
            }
        }
    }
    private val mViewClient by lazy {
        object : AndroidViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.i("WebTabImpl onPageStarted ", "$isIncognitoMode  -$url ")
                mModel.setCurWebUrl(url ?: "")
                if (url == HOME_PLACEHOLDER_URL) {
                    return
                }
                if (!isIncognitoMode) {
                    RoomHelper.browserHistoryDao.add(BrowserHistory().apply {
                        title = view?.title
                        systemTime = System.currentTimeMillis()
                        url?.let {
                            this.url = it
                            host = it.toUri().host
                            iconUrl = webIconUrlToLoadUrl(it)
                            Log.i("onPageStarted", "iconUrl -- $iconUrl")
                        }
                    })
                    mModel.updateRecentlyOpen(url, view?.title)
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.i("WebTabImpl onPageFinished", "isIncognitoMode-title${view?.title}--$isIncognitoMode $url")
                mModel.setCurWebUrl(url ?: "")
            }
        }
    }
    private val mSearchEngine by lazy {
        ComponentProvider.provideSearchEngine()
    }

    private fun updateTitle(title: String?) {
        withViewModel {
            webTitle.postValue(title?:"")
        }
    }

    override fun getView(): IView {
        return this
    }

    override fun goBack() {
        mAndroidWebView.goBack()
    }

    override fun goForward() {
        mAndroidWebView.goForward()
    }

    override fun canGoBack(): Boolean {
        return mAndroidWebView.canGoBack()
    }

    override fun canGoForward(): Boolean {
        return mAndroidWebView.canGoForward()
    }

    override fun loadUrl(url: String) {
        mAndroidWebView.stopLoading()
        updateTitle(url)
        mAndroidWebView.loadUrl(url)
    }

    override fun searchContent(content: String) {
        loadUrl("${ComponentProvider.provideSearchEngine().queryUrl}${content}")
    }

    override fun refreshWeb() {
        mAndroidWebView.reload()
    }

    override fun hasLoadHistory(): Boolean {
        return mAndroidWebView.copyBackForwardList().size > 0
    }

    override fun getWebCapture(): Bitmap {
        return mAndroidWebView.getDrawingCache(true)
    }

    override fun getLoadingUrl(): String? {
        return mAndroidWebView.url
    }

    override fun setWebLoadMode(isIncognitoMode: Boolean) {
        this.isIncognitoMode = isIncognitoMode
        with(mAndroidWebView.settings) {
            if (isIncognitoMode) {
                domStorageEnabled = false
                setAppCacheEnabled(false)
                databaseEnabled = false
                cacheMode = WebSettings.LOAD_NO_CACHE
            } else {
                domStorageEnabled = true
                setAppCacheEnabled(true)
                databaseEnabled = true
                cacheMode = WebSettings.LOAD_DEFAULT
            }
        }
    }

    override fun getTitle(): String? {
        return mAndroidWebView.title
    }

    override fun onCreate(host: ITabHost) {
        super.onCreate(host)
        initWebView()
    }

    override fun onDestroy() {
        super.onDestroy()
        with(mAndroidWebView) {
            stopLoading()
            clearHistory()
            clearFormData()
            clearCache(true)
            clearSslPreferences()
            clearMatches()
            destroy()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        with(mAndroidWebView) {
            webChromeClient = mChromeClient
            webViewClient = mViewClient
            with(settings) {
                javaScriptEnabled = true
                mediaPlaybackRequiresUserGesture = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                if (isIncognitoMode) {
                    domStorageEnabled = false
                    setAppCacheEnabled(false)
                    databaseEnabled = false
                    cacheMode = WebSettings.LOAD_NO_CACHE
                } else {
                    domStorageEnabled = true
                    setAppCacheEnabled(true)
                    databaseEnabled = true
                    cacheMode = WebSettings.LOAD_DEFAULT
                }
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                allowContentAccess = true
                allowFileAccess = true
                allowFileAccessFromFileURLs = false
                allowUniversalAccessFromFileURLs = false
            }
        }
        loadUrl(HOME_PLACEHOLDER_URL)
    }

    override fun snapshot(): Bitmap? {
        return mAndroidWebView.captureByDrawCache()
    }
}