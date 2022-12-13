package fei.stu.mobv.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fei.stu.mobv.database.items.BarItem
import fei.stu.mobv.database.items.FriendItem
import fei.stu.mobv.database.items.FriendLocationItem

@Dao
interface DbDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBars(bars: List<BarItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriends(friends: List<FriendItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendsLocation(friends: List<FriendLocationItem>)

    @Query("DELETE FROM bars")
    suspend fun deleteBars()

    @Query("DELETE FROM friends")
    suspend fun deleteFriends()

    @Query("DELETE FROM friendsLocation")
    suspend fun deleteFriendsLocation()

    @Query("SELECT * FROM bars order by users DESC, name ASC")
    fun getBarsByUsersCountDesc(): LiveData<List<BarItem>?>

    @Query("SELECT * FROM bars order by users ASC, name ASC")
    fun getBarsByUsersCountAsc(): LiveData<List<BarItem>?>

    @Query("SELECT * FROM bars order by name ASC, users DESC")
    fun getBarsByNameAsc(): LiveData<List<BarItem>?>

    @Query("SELECT * FROM bars order by name DESC, users DESC")
    fun getBarsByNameDesc(): LiveData<List<BarItem>?>

    @Query("SELECT * FROM friends order by name ASC")
    fun getFriends(): LiveData<List<FriendItem>?>

    @Query("SELECT * FROM friendsLocation order by userName ASC")
    fun getFriendsLocation(): LiveData<List<FriendLocationItem>?>
}