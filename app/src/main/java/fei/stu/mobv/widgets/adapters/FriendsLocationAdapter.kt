package fei.stu.mobv.widgets.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fei.stu.mobv.R
import fei.stu.mobv.database.items.FriendLocationItem
import fei.stu.mobv.helper.autoNotify
import fei.stu.mobv.widgets.events.FriendsLocationEvents
import kotlin.properties.Delegates

class FriendsLocationAdapter(val events: FriendsLocationEvents? = null) :
    RecyclerView.Adapter<FriendsLocationAdapter.FriendLocationItemViewHolder>() {
    var items: List<FriendLocationItem> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { o, n -> o.userId.compareTo(n.userId) == 0 }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendLocationItemViewHolder {
        return FriendLocationItemViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: FriendLocationItemViewHolder, position: Int) {
        holder.bind(items[position], events)
    }

    class FriendLocationItemViewHolder(
        private val parent: ViewGroup,
        itemView: View = LayoutInflater.from(parent.context).inflate(
            R.layout.friend_location_item,
            parent,
            false
        )
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: FriendLocationItem, events: FriendsLocationEvents?) {
            itemView.findViewById<TextView>(R.id.name).text = item.userName
            itemView.findViewById<TextView>(R.id.time).text = item.dateTime
            itemView.findViewById<TextView>(R.id.bar).text = item.barName
            itemView.setOnClickListener { events?.onFriendLocationClick(item) }
        }
    }
}