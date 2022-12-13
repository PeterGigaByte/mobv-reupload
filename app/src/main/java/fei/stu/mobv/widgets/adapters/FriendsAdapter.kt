package fei.stu.mobv.widgets.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fei.stu.mobv.R
import fei.stu.mobv.database.items.FriendItem
import fei.stu.mobv.helper.autoNotify
import fei.stu.mobv.widgets.events.FriendsEvents
import kotlin.properties.Delegates

class FriendsAdapter(val events: FriendsEvents? = null) :
    RecyclerView.Adapter<FriendsAdapter.FriendItemViewHolder>() {
    var items: List<FriendItem> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { o, n -> o.id.compareTo(n.id) == 0 }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendItemViewHolder {
        return FriendItemViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: FriendItemViewHolder, position: Int) {
        holder.bind(items[position], events)
    }

    class FriendItemViewHolder(
        private val parent: ViewGroup,
        itemView: View = LayoutInflater.from(parent.context).inflate(
            R.layout.friend_item,
            parent,
            false
        )
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: FriendItem, events: FriendsEvents?) {
            itemView.findViewById<TextView>(R.id.name).text = item.name
            itemView.findViewById<Button>(R.id.button2).setOnClickListener {
                events?.deleteFriend(item)
            }

            //itemView.setOnClickListener { events?.onFriendClick(item) }
        }
    }
}