package tool.browser.fast.browser

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import tool.browser.fast.R
import tool.browser.fast.ad.admob.AdSpace
import tool.browser.fast.ad.admob.AdUtils
import tool.browser.fast.browser.dialog.DialogMore
import tool.browser.fast.browser.tab.ITabHost
import tool.browser.fast.browser.tab.IView
import tool.browser.fast.browser.tab.IWebTab
import tool.browser.fast.browser.tab.manager.TabManager
import tool.browser.fast.component.BaseActivity
import tool.browser.fast.component.BaseViewModel
import tool.browser.fast.databinding.ActivityBrowserBinding
import tool.browser.fast.setting.SettingActivity
import tool.browser.fast.utils.removeFromParent

class BrowserActivity : BaseActivity<ActivityBrowserBinding, BrowserViewModel>(
    layoutResId = R.layout.activity_browser,
    viewModelClass = BrowserViewModel::class.java
), ITabHost {
    private val mWebContentDefaultLayoutParam by lazy {
        FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TabManager.attach(this)
        withViewModel {
            curTab.observe(this@BrowserActivity) {
                addWebTabContent(it?.getCurView()?.getRoot())
            }
            curTabView.observe(this@BrowserActivity) {
                addWebTabContent(it?.getRoot())
            }
        }
        AdUtils.loadAd(AdSpace.SPACE_DISCOVER_RE_OPEN)
        AdUtils.loadAd(AdSpace.SPACE_DISCOVER_HISTORY)
    }

    private fun addWebTabContent(tabContent: View?) {
        withLayoutBinding {
            if (tabContent != null) {
                tabContent.removeFromParent()
                tabContent.layoutParams = mWebContentDefaultLayoutParam
                if (tabContentRoot.childCount > 0) {
                    tabContentRoot.removeAllViews()
                }
                tabContentRoot.addView(tabContent)
            } else {
                tabContentRoot.removeAllViews()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TabManager.detach()
    }

    override fun getLifeOwner(): LifecycleOwner {
        return this
    }

    override fun startActivityForTab(option: BaseViewModel.StartActivityOption) {
        withViewModel {
            startActivity.postValue(option)
        }
    }

    override fun showMorePopupWindow(view: View, isHomePage: Boolean) {
        withViewModel {
            DialogMore(isHomePage).show(supportFragmentManager, "moreDialog")
        }
    }

    override fun onTabContentViewChanged(view: IView?, isHomePage: Boolean) {
        withViewModel {
            curShowingHomePage.postValue(isHomePage)
            curTabView.postValue(view)
        }
    }

    override fun onBackPressed() {
        if (TabManager.mForegroundTab?.onBackPressed() == true) return
        super.onBackPressed()
    }

    override fun onTabChanged(newTab: IWebTab) {
        withViewModel {
            curTab.value = newTab
        }
    }

    override fun onTabCountChanged(count: Int) {
        withViewModel {
            totalTabCount.postValue(
                if (count > 1) {
                    if (count > 99) "99+" else count.toString()
                } else ""
            )
        }
    }

    override fun createLayoutInflater(): LayoutInflater {
        return layoutInflater
    }
}