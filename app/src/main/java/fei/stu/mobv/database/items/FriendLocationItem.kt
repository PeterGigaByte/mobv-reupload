package fei.stu.mobv.database.items

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friendsLocation")
class FriendLocationItem(
    @PrimaryKey val userId: String,
    val userName: String,
    val dateTime: String?,
    val barId: String?,
    barName: String?,
    val lat: Double?,
    val lon: Double?
) {
    val barName: String = barName ?: "Nenachádza sa v žiadnom podniku!"
    override fun toString(): String {
        return "FriendLocationItem(userId='$userId', userName='$userName', dateTime='$dateTime', barId='$barId', barName='$barName', lat='$lat', lon='$lon')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FriendLocationItem) return false

        if (userId != other.userId) return false
        if (userName != other.userName) return false
        if (dateTime != other.dateTime) return false
        if (barId != other.barId) return false
        if (barName != other.barName) return false
        if (lat != other.lat) return false
        if (lon != other.lon) return false
        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + userName.hashCode()
        result = 31 * result + dateTime.hashCode()
        result = 31 * result + barId.hashCode()
        result = 31 * result + barName.hashCode()
        result = 31 * result + lat.hashCode()
        result = 31 * result + lon.hashCode()
        return result
    }
}