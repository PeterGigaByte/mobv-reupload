package fei.stu.mobv.widgets.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import fei.stu.mobv.R
import fei.stu.mobv.enums.BarType
import fei.stu.mobv.helper.autoNotify
import fei.stu.mobv.widgets.items.BarDetailItem
import kotlin.properties.Delegates

class DetailAdapter : RecyclerView.Adapter<DetailAdapter.BarDetailItemViewHolder>() {
    var items: List<BarDetailItem> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { o, n -> o.key.compareTo(n.key) == 0 }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarDetailItemViewHolder {
        return BarDetailItemViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BarDetailItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class BarDetailItemViewHolder(
        private val parent: ViewGroup,
        itemView: View = LayoutInflater.from(parent.context).inflate(
            R.layout.bar_detail_item,
            parent,
            false
        )
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: BarDetailItem) {
            itemView.findViewById<TextView>(R.id.name).text = item.key
            itemView.findViewById<TextView>(R.id.value).text = item.value
            if (item.key == "amenity") {
                when (item.getBarType()) {
                    BarType.FAST_FOOD -> itemView.findViewById<LottieAnimationView>(R.id.bar_animation)
                        .setAnimation(R.raw.fastfood)
                    BarType.RESTAURANT -> itemView.findViewById<LottieAnimationView>(R.id.bar_animation)
                        .setAnimation(R.raw.cateringforkknife)
                    BarType.CAFE -> itemView.findViewById<LottieAnimationView>(R.id.bar_animation)
                        .setAnimation(R.raw.housecaferestouranbuildingmaison005moccaanimation)
                    BarType.NODE -> itemView.findViewById<LottieAnimationView>(R.id.bar_animation)
                        .setAnimation(R.raw.eatingchips)
                    BarType.PUB -> itemView.findViewById<LottieAnimationView>(R.id.bar_animation)
                        .setAnimation(R.raw.coupleeating)
                    BarType.OTHER -> itemView.findViewById<LottieAnimationView>(R.id.bar_animation)
                        .setAnimation(R.raw.questioning)
                }
            } else {
                itemView.findViewById<LottieAnimationView>(R.id.bar_animation).isEnabled = false
            }
        }
    }
}