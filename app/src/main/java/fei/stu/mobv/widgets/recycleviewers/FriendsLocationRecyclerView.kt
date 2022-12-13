package fei.stu.mobv.widgets.recycleviewers

import android.content.Context
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fei.stu.mobv.database.items.FriendLocationItem
import fei.stu.mobv.fragments.friends.FriendsLocationFragmentDirections
import fei.stu.mobv.widgets.adapters.FriendsLocationAdapter
import fei.stu.mobv.widgets.events.FriendsLocationEvents

class FriendsLocationRecyclerView : RecyclerView {
    private lateinit var friendsLocationAdapter: FriendsLocationAdapter
    var events: FriendsLocationEvents? = null

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
        friendsLocationAdapter = FriendsLocationAdapter(object : FriendsLocationEvents {
            override fun onFriendLocationClick(friendLocationItem: FriendLocationItem) {
                if (friendLocationItem.barId != null) {
                    this@FriendsLocationRecyclerView.findNavController().navigate(
                        FriendsLocationFragmentDirections.actionFriendsLocationFragmentToDetailFragment(
                            friendLocationItem.barId
                        )
                    )
                }
            }
        })
        adapter = friendsLocationAdapter

    }
}

@BindingAdapter(value = ["friendLocationItems"])
fun FriendsLocationRecyclerView.applyItems(
    friendsLocation: List<FriendLocationItem>?
) {
    (adapter as FriendsLocationAdapter).items = friendsLocation ?: emptyList()
}