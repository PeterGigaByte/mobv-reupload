package fei.stu.mobv.widgets.events

import fei.stu.mobv.database.items.FriendLocationItem

interface FriendsLocationEvents {
    fun onFriendLocationClick(friendLocationItem: FriendLocationItem)
}