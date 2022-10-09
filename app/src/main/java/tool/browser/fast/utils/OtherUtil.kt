package tool.browser.fast.utils

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import tool.browser.fast.BuildConfig
import tool.browser.fast.component.FastSearch
import android.content.ClipData
import android.content.ClipboardManager

import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toUri
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import okhttp3.internal.format
import java.text.SimpleDateFormat
import java.util.*


fun ValueAnimator.dismiss() {
    removeAllUpdateListeners()
    cancel()
}

fun Long?.toTimeUnit(): String {
    val time = this ?: 0L
    val s: Long = (time % (1000 * 60)) / 1000
    val m: Long = (time % (1000 * 60 * 60)) / (1000 * 60)
    val h: Long = (time % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)

    fun fill(s: String): String {
        return if (s.length == 2) s else "0$s"
    }

    return "${fill(h.toString())}:${fill(m.toString())}:${fill(s.toString())}"
}

fun <E> MutableList<E>.addIf(e: E, condition: () -> Boolean) {
    if (condition()) {
        add(e)
    }
}

fun createEmailIntent(string: String): Intent {
    return Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, string)
    }
}

fun createShareIntent(context: Context, string: String): Intent {
    return ShareCompat.IntentBuilder(context)
        .setType("text/plain")
        .setText(string)
        .intent
}

fun createViewIntent(string: String): Intent {
    return Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(string)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
}

fun Intent.startSafely(context: Context, errorHandler: () -> Unit = {}) {
    try {
        context.startActivity(this)
    } catch (e: Exception) {
        errorHandler()
    }
}

fun generateConsKey(key: String): String {
    return "${BuildConfig.APPLICATION_ID}_${key}"
}

fun <E> MutableList<E>.addIfNotNull(e: E?) {
    e?.let {
        add(it)
    }
}

fun Int.asDrawable(): Drawable? {
    return ContextCompat.getDrawable(FastSearch.mSelf, this)
}

fun Context.dp2px(dp: Float): Int {
    val scale = resources.displayMetrics.density
    return (dp * scale * 0.5f).toInt()
}

fun Context.copyText(text: String) {
    val cm: ClipboardManager? = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val mClipData = ClipData.newPlainText("Label", text)

    cm?.let {
        it.setPrimaryClip(mClipData)
        ToastUtils.showShort("Copy Successfully")
    }
}

fun timeToString(systemTime: Long): String {
    val format = SimpleDateFormat("MM, dd, yyyy")
    return format.format(Date(systemTime))
}

fun getDate(systemTime: Long): String {
    return "${getWeek(systemTime)}, ${timeToString(systemTime)}"
}

fun getTime(systemTime: Long): String {
    val format = SimpleDateFormat("HH:mm aa",Locale.ENGLISH)
    return format.format(Date(systemTime))
}

fun webIconUrlToLoadUrl(url: String?): String {
    val uri = url?.toUri()
    return "${uri?.scheme}://${uri?.host}/favicon.ico"
}

fun Context.shareText(str: String) {
    startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, str)
        setType("text/plain")
    }, "Share"))
}

fun getWeek(time: Long): String {
    val cd = Calendar.getInstance()
    cd.time = Date(time)
    val year = cd[Calendar.YEAR] //获取年份
    val month = cd[Calendar.MONTH] //获取月份
    val day = cd[Calendar.DAY_OF_MONTH] //获取日期
    val week = cd[Calendar.DAY_OF_WEEK] //获取星期
    val weekString = when (week) {
        Calendar.SUNDAY -> "Sunday"
        Calendar.MONDAY -> "Monday"
        Calendar.TUESDAY -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY -> "Thursday"
        Calendar.FRIDAY -> "Friday"
        else -> "Saturday"
    }
    return weekString
}

fun Long.toSimpleDateStr(): String {
    return TimeUtils.date2String(Date(this), "yyyy-MM-dd")
}