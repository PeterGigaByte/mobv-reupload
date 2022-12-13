package fei.stu.mobv.widgets.recycleviewers

import android.content.Context
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fei.stu.mobv.database.items.FriendItem
import fei.stu.mobv.widgets.adapters.FriendsAdapter
import fei.stu.mobv.widgets.events.FriendsEvents

class FriendsRecyclerView : RecyclerView {
    private lateinit var friendsAdapter: FriendsAdapter
    var events: FriendsEvents? = null

    /**
     * Default constructor
     *
     * @param context context for the activity
     */
    constructor(context: Context) : super(context) {
        init(context)
    }

    /**
     * Constructor for XML layout
     *
     * @param context activity context
     * @param attrs   xml attributes
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        friendsAdapter = FriendsAdapter(object : FriendsEvents {
            override fun deleteFriend(friend: FriendItem) {
                events?.deleteFriend(friend)
            }

        })
        adapter = friendsAdapter

    }
}

@BindingAdapter(value = ["friendItems"])
fun FriendsRecyclerView.applyItems(
    friends: List<FriendItem>?
) {
    (adapter as FriendsAdapter).items = friends ?: emptyList()
}