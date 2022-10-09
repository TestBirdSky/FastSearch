package tool.browser.fast.browser.bookmark

import tool.browser.fast.R
import tool.browser.fast.utils.asDrawable

object Bookmarks {
    val Ins by lazy {
        Bookmark(
            icon = R.mipmap.instagram.asDrawable(),
            name = "Instagram",
            url = "https://www.instagram.com/"
        )
    }
    val Twitter by lazy {
        Bookmark(
            icon = R.mipmap.twitter.asDrawable(),
            name = "Twitter",
            url = "https://twitter.com/"
        )
    }
    val Tiktok by lazy {
        Bookmark(
            icon = R.mipmap.tiktok.asDrawable(),
            name = "Tiktok",
            url = "https://www.tiktok.com/"
        )
    }
    val Facebook by lazy {
        Bookmark(
            icon = R.mipmap.facebook.asDrawable(),
            name = "Facebook",
            url = "https://www.facebook.com/"
        )
    }
    val BookMark by lazy {
        Bookmark(
            icon = R.drawable.ic_bookmark.asDrawable(),
            name = "Bookmark",
            url = "",
            isRecommendApp = false
        )
    }
    val ReadList by lazy {
        Bookmark(
            icon = R.drawable.ic_read_list.asDrawable(),
            name = "ReadList",
            url = "",
            isRecommendApp = false
        )
    }
    val RecentlyOpened by lazy {
        Bookmark(
            icon = R.drawable.ic_recently_opened.asDrawable(),
            name = "Recently opened",
            url = "",
            isRecommendApp = false
        )
    }
    val History by lazy {
        Bookmark(
            icon = R.drawable.ic_history.asDrawable(),
            name = "History",
            url = "",
            isRecommendApp = false
        )
    }

}