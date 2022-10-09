package tool.browser.fast.browser.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import tool.browser.fast.R
import tool.browser.fast.databinding.DialogItemBinding

class AdapterMore() : RecyclerView.Adapter<AdapterMore.ViewHolder>() {
    var data = arrayListOf<MoreItemUiBean>()

    inner class ViewHolder(val databinding: DialogItemBinding) : RecyclerView.ViewHolder(databinding.root) {
        fun bind(item: MoreItemUiBean) {
            if (!item.isCanClick) {
                databinding.item.alpha = 0.4f
                databinding.item.isEnabled = false
            } else {
                databinding.item.alpha = 1f
                databinding.item.isEnabled = true
            }
            databinding.iv.setImageResource(item.icon)
            databinding.tvName.text = item.name
            databinding.item.setOnClickListener {
                item.click.invoke()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.dialog_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}