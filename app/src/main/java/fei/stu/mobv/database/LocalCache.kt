package fei.stu.mobv.database

import androidx.lifecycle.LiveData
import fei.stu.mobv.database.items.BarItem
import fei.stu.mobv.database.items.FriendItem
import fei.stu.mobv.database.items.FriendLocationItem

class LocalCache(private val dao: DbDao) {
    suspend fun insertBars(bars: List<BarItem>) {
        dao.insertBars(bars)
    }

    suspend fun insertFriends(friends: List<FriendItem>) {
        dao.insertFriends(friends)
    }

    suspend fun insertFriendsLocation(friendsLocation: List<FriendLocationItem>) {
        dao.insertFriendsLocation(friendsLocation)
    }

    suspend fun deleteBars() {
        dao.deleteBars()
    }

    suspend fun deleteFriends() {
        dao.deleteFriends()
    }

    suspend fun deleteFriendsLocation() {
        dao.deleteFriendsLocation()
    }

    fun getBarsByUsersCountDesc(): LiveData<List<BarItem>?> = dao.getBarsByUsersCountDesc()

    fun getBarsByUsersCountAsc(): LiveData<List<BarItem>?> = dao.getBarsByUsersCountAsc()

    fun getBarsByNameAsc(): LiveData<List<BarItem>?> = dao.getBarsByNameAsc()

    fun getBarsByNameDesc(): LiveData<List<BarItem>?> = dao.getBarsByNameDesc()

    fun getFriends(): LiveData<List<FriendItem>?> = dao.getFriends()

    fun getFriendsLocation(): LiveData<List<FriendLocationItem>?> = dao.getFriendsLocation()
}