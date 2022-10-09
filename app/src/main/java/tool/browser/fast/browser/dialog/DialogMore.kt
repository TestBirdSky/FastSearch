package tool.browser.fast.browser.dialog

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils.dp2px
import tool.browser.fast.R
import tool.browser.fast.browser.bookmarkpage.BookmarkActivity
import tool.browser.fast.browser.history.BrowserHistoryActivity
import tool.browser.fast.browser.readlistpage.ReadListActivity
import tool.browser.fast.browser.recentlyopenpage.RecentlyOpenActivity
import tool.browser.fast.browser.tab.manager.TabManager
import tool.browser.fast.databinding.MoreDialogBinding
import tool.browser.fast.setting.SettingActivity

class DialogMore(val isHomePage:Boolean) : DialogFragment() {
    lateinit var binding: MoreDialogBinding
    private val adapterMore = AdapterMore()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.more_dialog, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val window = dialog.window
        window?.run {
            setGravity(Gravity.BOTTOM)
            attributes.y = dp2px(80f)
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.run {
            isCancelable = true
            setCanceledOnTouchOutside(true)
        }
        setData()
        binding.rv.run {
            context?.let { layoutManager = LinearLayoutManager(it) }
            adapter = adapterMore
        }
    }

    private fun setData() {
        adapterMore.data.addAll(
            arrayListOf<MoreItemUiBean>(
                MoreItemUiBean(R.drawable.ic_more1, "Reload", isCanClick = !isHomePage) {
                    TabManager.reload()
                    dismiss()
                },
                MoreItemUiBean(R.drawable.ic_more2, "Open new tab", isCanClick = !isHomePage) {
                    TabManager.addTab()
                    dismiss()
                },
                MoreItemUiBean(R.drawable.ic_more3, "Open a new incognito tab", isCanClick = !isHomePage) {
                    TabManager.addIncognitoTab()
                    dismiss()
                },
                MoreItemUiBean(R.drawable.ic_add_read_later, "Add read later", isCanClick = !isHomePage) {
                    TabManager.addReadLater()
                    dismiss()
                },
                MoreItemUiBean(R.drawable.ic_more5, "Add bookmark", isCanClick = !isHomePage) {
                    TabManager.addBookmark()
                    dismiss()
                },
                MoreItemUiBean(R.drawable.ic_more6, "Bookmark") {
                    context?.startActivity(Intent(context, BookmarkActivity::class.java))
                    dismiss()
                },
                MoreItemUiBean(R.drawable.ic_more7, "Read list") {
                    context?.startActivity(Intent(context, ReadListActivity::class.java))
                    dismiss()
                },
                MoreItemUiBean(R.drawable.ic_more8, "Recently opened") {
                    context?.startActivity(Intent(context, RecentlyOpenActivity::class.java))
                    dismiss()
                },
                MoreItemUiBean(R.drawable.ic_more9, "History") {
                    context?.startActivity(Intent(context, BrowserHistoryActivity::class.java))
                    dismiss()
                },
                MoreItemUiBean(R.drawable.ic_more10, "Setting") {
                    context?.startActivity(Intent(context, SettingActivity::class.java))
                    dismiss()
                }
            )
        )
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(dp2px(320f), dp2px(400f))
    }
}