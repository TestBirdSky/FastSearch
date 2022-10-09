package tool.browser.fast.component

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.BaseObservable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.lxj.xpopup.XPopup
import tool.browser.fast.BR

class BindingRvAdapter(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    @LayoutRes private val layoutResId: Int
) : RecyclerView.Adapter<BindingRvAdapter.VH>() {
    interface OnItemClick {
        fun onItemClick(item: BaseItemModel)
    }

    var onItemClick: ((item: BaseItemModel) -> Unit)? = null
    var onItemLongClick: (item: BaseItemModel, view: View) -> Unit = { item, view -> }
    var onItemSelected: ((item: CheckableItemModel) -> Unit)? = null
    var onBind: ((item: BaseItemModel, holder: VH) -> Unit)? = null

    open class BaseItemModel : BaseObservable()

    open class CheckableItemModel(
        val isSingleSelectMode: Boolean = true,
        val cancelable: Boolean = false
    ) : BaseItemModel() {
        var checked: Boolean = false
            set(value) {
                field = value
                notifyChange()
            }
    }

    class VH(
        private val binding: ViewDataBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun withLayoutBinding(receiver: ViewDataBinding.() -> Unit) {
            receiver(binding)
        }
    }

    var models: List<BaseItemModel>? = null
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            value?.let {
                data.clear()
                data.addAll(it)
            }
            notifyDataSetChanged()
        }
        get() {
            return data.toList()
        }

    private val data = mutableListOf<BaseItemModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            DataBindingUtil.inflate(
                LayoutInflater.from(context), layoutResId, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val model = data[position]
        onBind?.invoke(model, holder)
        holder.withLayoutBinding {
            root.setOnClickListener {
                onSelectMode(model)
                (model as? OnItemClick)?.onItemClick(model)
                onItemClick?.invoke(model)
            }
            root.setOnLongClickListener {
                onItemLongClick.invoke(model, it)
                true
            }
            lifecycleOwner = this@BindingRvAdapter.lifecycleOwner
            setVariable(BR.model, model)
            executePendingBindings()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(item: BaseItemModel) {
        data.remove(item)
        notifyDataSetChanged()
    }

    private fun onSelectMode(model: BaseItemModel) {
        val checkableItemModel = model as? CheckableItemModel ?: return
        if (!checkableItemModel.cancelable && checkableItemModel.checked) return

        if (checkableItemModel.isSingleSelectMode) {
            data.toList().forEach {
                (it as? CheckableItemModel)?.checked = false
            }
        }
        checkableItemModel.checked = true
        onItemSelected?.invoke(checkableItemModel)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}