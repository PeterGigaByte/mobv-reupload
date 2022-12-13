package fei.stu.mobv.widgets.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fei.stu.mobv.R
import fei.stu.mobv.database.items.BarItem
import fei.stu.mobv.enums.BarType
import fei.stu.mobv.helper.autoNotify
import fei.stu.mobv.widgets.events.BarsEvents
import kotlin.properties.Delegates

class BarsAdapter(val events: BarsEvents? = null) :
    RecyclerView.Adapter<BarsAdapter.BarItemViewHolder>() {
    var items: List<BarItem> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { o, n -> o.id.compareTo(n.id) == 0 }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarItemViewHolder {
        return BarItemViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BarItemViewHolder, position: Int) {
        holder.bind(items[position], events)
    }

    class BarItemViewHolder(
        private val parent: ViewGroup,
        itemView: View = LayoutInflater.from(parent.context).inflate(
            R.layout.bar_item,
            parent,
            false
        )
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: BarItem, events: BarsEvents?) {
            itemView.findViewById<TextView>(R.id.name).text = item.name
            itemView.findViewById<TextView>(R.id.count).text = item.users.toString()
            itemView.setOnClickListener { events?.onBarClick(item) }
            when (item.getBarType()) {
                BarType.FAST_FOOD -> itemView.findViewById<ImageView>(R.id.barItemImage)
                    .setImageResource(R.drawable.fastfood)
                BarType.RESTAURANT -> itemView.findViewById<ImageView>(R.id.barItemImage)
                    .setImageResource(R.drawable.restaurant)
                BarType.CAFE -> itemView.findViewById<ImageView>(R.id.barItemImage)
                    .setImageResource(R.drawable.cafe)
                BarType.NODE -> itemView.findViewById<ImageView>(R.id.barItemImage)
                    .setImageResource(R.drawable.bar)
                BarType.PUB -> itemView.findViewById<ImageView>(R.id.barItemImage)
                    .setImageResource(R.drawable.pub)
                BarType.OTHER -> itemView.findViewById<ImageView>(R.id.barItemImage)
                    .setImageResource(R.drawable.other)
            }
        }
    }
}