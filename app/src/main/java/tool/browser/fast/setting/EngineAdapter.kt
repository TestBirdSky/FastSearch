package tool.browser.fast.setting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import tool.browser.fast.R
import tool.browser.fast.browser.search.BingSearch.BING_NAME_ENGINE_URL
import tool.browser.fast.browser.search.DuckSearch.Duck_NAME_ENGINE_URL
import tool.browser.fast.browser.search.GoogleSearch.GOOGLE_NAME_ENGINE_URL
import tool.browser.fast.browser.search.YahooSearch.YAHOO_NAME_ENGINE_URL
import tool.browser.fast.databinding.SearchEngineItemBinding
import tool.browser.fast.utils.StringCacheDelegate

var curSelectedEngine by StringCacheDelegate(def = GOOGLE_NAME_ENGINE_URL) { "cur_engine_url" }
class EngineAdapter : RecyclerView.Adapter<EngineAdapter.ViewHolder>() {
    var data = arrayListOf<ItemUiData>(
        ItemUiData(R.drawable.ic_engine_google, "Google", "www.google.com", GOOGLE_NAME_ENGINE_URL),
        ItemUiData(R.drawable.ic_engine_bing, "Bing", "www.bing.com", BING_NAME_ENGINE_URL),
        ItemUiData(R.drawable.ic_engine_duck, "Duck", "search.yahoo.com", YAHOO_NAME_ENGINE_URL),
        ItemUiData(R.drawable.ic_engine_yahoo, "Yahoo", "duckduckgo.com", Duck_NAME_ENGINE_URL),
    )

    var itemClick: () -> Unit = {}

    inner class ViewHolder(val databinding: SearchEngineItemBinding) : RecyclerView.ViewHolder(databinding.root) {
        fun bind(item: ItemUiData) {
            databinding.ivLogo.setImageResource(item.icon)
            databinding.tvName.text = item.name
            databinding.tvHost.text = item.host
            val isChecked = item.url == curSelectedEngine
            databinding.ivChecked.visibility = if (isChecked) View.VISIBLE else View.INVISIBLE
            databinding.root.setOnClickListener {
                if (item.url != curSelectedEngine) {
                    curSelectedEngine = item.url
                    itemClick.invoke()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.search_engine_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

data class ItemUiData(val icon: Int, val name: String, val host: String, val url: String)