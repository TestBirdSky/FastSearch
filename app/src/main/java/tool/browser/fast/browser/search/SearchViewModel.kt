package tool.browser.fast.browser.search

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.KeyboardUtils
import tool.browser.fast.browser.tab.IWebTab
import tool.browser.fast.browser.tab.manager.TabManager
import tool.browser.fast.component.BaseViewModel

class SearchViewModel : BaseViewModel() {
    val searchContent = MutableLiveData<String>()
    private val mCurTab: IWebTab?
        get() {
            return TabManager.mForegroundTab
        }
    var mDefSearchContent: String? = null

    fun search(view: View) {
        val searchContent = searchContent.value
        if (searchContent.isNullOrBlank()) return
        KeyboardUtils.hideSoftInput(view)
        finishActivity(view)
        if (searchContent != mDefSearchContent) {
            mCurTab?.searchContent(searchContent)
        }
    }

    fun bindEt(view: EditText) {
        view.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                this@SearchViewModel.searchContent.value = p0?.toString()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }
}