package fei.stu.mobv.api.data

import com.google.gson.annotations.SerializedName

data class UserResponse(
    val uid: String,
    val access: String,
    val refresh: String
)

data class BarListResponse(
    @SerializedName("bar_id")
    val barId: String,
    @SerializedName("bar_name")
    val barName: String,
    @SerializedName("bar_type")
    val barType: String,
    val lat: Double,
    var lon: Double,
    var users: Int
)

data class BarDetailItemResponse(
    val type: String,
    val id: String,
    val lat: Double,
    val lon: Double,
    val tags: Map<String, String>
)

data class BarDetailResponse(
    val elements: List<BarDetailItemResponse>
)

data class FriendItemResponse(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("user_name")
    val userName: String,
)

data class FriendLocationItemResponse(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("time")
    val dateTime: String,
    @SerializedName("bar_id")
    val barId: String,
    @SerializedName("bar_name")
    val barName: String,
    @SerializedName("bar_lat")
    val lat: Double,
    @SerializedName("bar_lon")
    val lon: Double


)