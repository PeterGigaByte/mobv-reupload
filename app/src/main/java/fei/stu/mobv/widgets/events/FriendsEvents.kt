package fei.stu.mobv.widgets.events

import fei.stu.mobv.database.items.FriendItem


interface FriendsEvents {
    fun deleteFriend(friend: FriendItem)
}