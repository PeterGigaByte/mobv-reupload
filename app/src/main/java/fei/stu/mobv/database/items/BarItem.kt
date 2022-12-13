package fei.stu.mobv.database.items

import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey
import fei.stu.mobv.enums.BarType
import fei.stu.mobv.viewModels.items.MyLocation

@Entity(tableName = "bars")
class BarItem(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val lat: Double,
    val lon: Double,
    var users: Int
) {

    fun distanceTo(location: MyLocation): Double {
        return Location("").apply {
            latitude = lat
            longitude = lon
        }.distanceTo(Location("").apply {
            latitude = location.lat
            longitude = location.lon
        }).toDouble()
    }

    fun getBarType(): BarType {
        return try {
            BarType.valueOf(type.uppercase())
        } catch (e: IllegalArgumentException) {
            BarType.OTHER
        }
    }

    override fun toString(): String {
        return "BarItem(id='$id', name='$name', type='$type', lat=$lat, lon=$lon, users=$users)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BarItem) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (type != other.type) return false
        if (lat != other.lat) return false
        if (lon != other.lon) return false
        if (users != other.users) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + lat.hashCode()
        result = 31 * result + lon.hashCode()
        result = 31 * result + users
        return result
    }
}