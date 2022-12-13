package fei.stu.mobv.database

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import fei.stu.mobv.api.RestApi
import fei.stu.mobv.api.data.*
import fei.stu.mobv.database.items.BarItem
import fei.stu.mobv.database.items.FriendItem
import fei.stu.mobv.database.items.FriendLocationItem
import fei.stu.mobv.viewModels.items.MyLocation
import fei.stu.mobv.viewModels.items.NearbyBar
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*

const val LOGIN_NAME_PASSWORD_ERROR = "Wrong name or password."
const val LOGIN_FAILED_TRY_AGAIN = "Failed to login, try again later."
const val LOGIN_FAILED_INTERNET_ERROR = "Login failed, check internet connection."
const val LOGIN_FAILED_ERROR = "Login in failed, error."
const val SIGNUP_NAME_ALREADY_EXIST = "Name already exists. Choose another."
const val SIGNUP_FAILED_TRY_AGAIN = "Failed to sign up, try again later."
const val SIGNUP_FAILED_INTERNET_ERROR = "Sign up failed, check internet connection."
const val SIGNUP_FAILED_ERROR = "Sign up failed, error."
const val BARS_LOAD_ERROR = "Failed to load bars."
const val BARS_READ_ERROR = "Failed to read bars."
const val BARS_FAILED_INTERNET_ERROR = "Failed to load bars, check internet connection."
const val BARS_FAILED_ERROR = "Failed to load bars, error."
const val FRIENDS_LOAD_ERROR = "Failed to load friends."
const val FRIENDS_READ_ERROR = "Failed to read friends."
const val FRIENDS_FAILED_INTERNET_ERROR = "Failed to load friends, check internet connection."
const val FRIENDS_FAILED_ERROR = "Failed to load friends, error."
const val ADD_FRIEND_FAILED_TRY_AGAIN = "Failed to add friend, try again later."
const val ADD_FRIEND_FAILED_INTERNET_ERROR = "Failed to add friend, check internet connection."
const val ADD_FRIEND_FAILED_ERROR = "Failed to add friend, error."
const val DELETE_FRIEND_FAILED_TRY_AGAIN = "Failed to delete friend, try again later."
const val DELETE_FRIEND_FAILED_INTERNET_ERROR =
    "Failed to delete friend, check internet connection."
const val DELETE_FRIEND_FAILED_ERROR = "Failed to delete friend, error."

const val WRONG_REQUEST = "-1"


class DataRepository private constructor(
    private val service: RestApi,
    private val cache: LocalCache,

    ) {


    suspend fun apiUserCreate(
        name: String,
        password: String,
        onError: (error: String) -> Unit,
        onStatus: (success: UserResponse?) -> Unit
    ) {
        try {
            val resp = service.userCreate(
                UserCreateRequest(
                    name = name,
                    password = hashUserData(password)
                )
            )
            if (resp.isSuccessful) {
                resp.body()?.let { user ->
                    if (user.uid == WRONG_REQUEST) {
                        onStatus(null)
                        onError(SIGNUP_NAME_ALREADY_EXIST)
                    } else {
                        onStatus(user)
                    }
                }
            } else {
                onError(SIGNUP_FAILED_TRY_AGAIN)
                onStatus(null)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError(SIGNUP_FAILED_INTERNET_ERROR)
            onStatus(null)
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError(SIGNUP_FAILED_ERROR)
            onStatus(null)
        }
    }

    suspend fun apiUserLogin(
        name: String,
        password: String,
        onError: (error: String) -> Unit,
        onStatus: (success: UserResponse?) -> Unit
    ) {
        try {
            val resp =
                service.userLogin(UserLoginRequest(name = name, password = hashUserData(password)))
            if (resp.isSuccessful) {
                resp.body()?.let { user ->
                    if (user.uid == WRONG_REQUEST) {
                        onStatus(null)
                        onError(LOGIN_NAME_PASSWORD_ERROR)
                    } else {
                        onStatus(user)
                    }
                }
            } else {
                onError(LOGIN_FAILED_TRY_AGAIN)
                onStatus(null)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError(LOGIN_FAILED_INTERNET_ERROR)
            onStatus(null)
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError(LOGIN_FAILED_ERROR)
            onStatus(null)
        }
    }

    suspend fun apiCheckIn(
        bar: NearbyBar,
        onError: (error: String) -> Unit,
        onSuccess: (success: Boolean) -> Unit
    ) {
        try {
            val resp =
                service.barMessage(BarMessageRequest(bar.id, bar.name, bar.type, bar.lat, bar.lon))
            if (resp.isSuccessful) {
                resp.body()?.let { _ ->
                    onSuccess(true)
                }
            } else {
                onError(LOGIN_FAILED_TRY_AGAIN)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError(LOGIN_FAILED_INTERNET_ERROR)
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError(LOGIN_FAILED_ERROR)
        }
    }

    suspend fun apiAddFriend(
        userName: String,
        onError: (error: String) -> Unit,
        onSuccess: (success: Boolean) -> Unit
    ) {
        try {
            val resp = service.addFriendMessage(UserFriendRequest(userName))
            if (resp.isSuccessful) {
                onSuccess(true)
            } else {
                onError(ADD_FRIEND_FAILED_TRY_AGAIN)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError(ADD_FRIEND_FAILED_INTERNET_ERROR)
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError(ADD_FRIEND_FAILED_ERROR)
        }
    }

    suspend fun apiDeleteFriend(
        userName: String,
        onError: (error: String) -> Unit,
        onSuccess: (success: Boolean) -> Unit
    ) {
        try {
            val resp = service.deleteFriendMessage(UserFriendRequest(userName))
            if (resp.isSuccessful) {
                onSuccess(true)
            } else {
                onError(DELETE_FRIEND_FAILED_TRY_AGAIN)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError(DELETE_FRIEND_FAILED_INTERNET_ERROR)
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError(DELETE_FRIEND_FAILED_ERROR)
        }
    }

    suspend fun apiBarList(
        onError: (error: String) -> Unit
    ) {
        try {
            val resp = service.barList()
            if (resp.isSuccessful) {
                resp.body()?.let { bars ->

                    val b = bars.map {
                        BarItem(
                            it.barId,
                            it.barName,
                            it.barType,
                            it.lat,
                            it.lon,
                            it.users
                        )
                    }
                    cache.deleteBars()
                    cache.insertBars(b)
                } ?: onError(BARS_LOAD_ERROR)
            } else {
                onError(BARS_READ_ERROR)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError(BARS_FAILED_INTERNET_ERROR)
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError(BARS_FAILED_ERROR)
        }
    }

    suspend fun apiFriendsList(
        onError: (error: String) -> Unit
    ) {
        try {
            val resp = service.friendsList()
            if (resp.isSuccessful) {
                resp.body()?.let { friends ->

                    val f = friends.map {
                        FriendItem(
                            it.userId,
                            it.userName,
                        )
                    }
                    cache.deleteFriends()
                    cache.insertFriends(f)
                } ?: onError(FRIENDS_LOAD_ERROR)
            } else {
                onError(FRIENDS_READ_ERROR)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError(FRIENDS_FAILED_INTERNET_ERROR)
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError(FRIENDS_FAILED_ERROR)
        }
    }

    suspend fun apiFriendsLocationList(
        onError: (error: String) -> Unit
    ) {
        try {
            val resp = service.friendsLocationList()
            if (resp.isSuccessful) {
                resp.body()?.let { friends ->

                    val f = friends.map {
                        FriendLocationItem(
                            it.userId,
                            it.userName,
                            it.dateTime,
                            it.barId,
                            it.barName,
                            it.lat,
                            it.lon
                        )
                    }
                    cache.deleteFriendsLocation()
                    cache.insertFriendsLocation(f)
                } ?: onError(FRIENDS_LOAD_ERROR)
            } else {
                onError(FRIENDS_READ_ERROR)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError(FRIENDS_FAILED_INTERNET_ERROR)
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError(FRIENDS_FAILED_ERROR)
        }
    }

    suspend fun apiNearbyBars(
        lat: Double, lon: Double,
        onError: (error: String) -> Unit
    ): List<NearbyBar> {
        var nearby = listOf<NearbyBar>()
        try {
            val q =
                "[out:json];node(around:250,$lat,$lon);(node(around:250)[\"amenity\"~\"^pub$|^bar$|^restaurant$|^cafe$|^fast_food$|^stripclub$|^nightclub$\"];);out body;>;out skel;"
            val resp = service.barNearby(q)
            if (resp.isSuccessful) {
                resp.body()?.let { bars ->
                    nearby = bars.elements.map {
                        NearbyBar(
                            it.id,
                            it.tags.getOrDefault("name", ""),
                            it.tags.getOrDefault("amenity", ""),
                            it.lat,
                            it.lon,
                            it.tags
                        ).apply {
                            distance = distanceTo(MyLocation(lat, lon))
                        }
                    }
                    nearby = nearby.filter { it.name.isNotBlank() }.sortedBy { it.distance }
                } ?: onError(BARS_LOAD_ERROR)
            } else {
                onError(BARS_READ_ERROR)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError(BARS_FAILED_INTERNET_ERROR)
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError(BARS_FAILED_ERROR)
        }
        return nearby
    }

    suspend fun apiBarDetail(
        id: String,
        onError: (error: String) -> Unit
    ) : NearbyBar? {
        var nearby:NearbyBar? = null
        try {
            val q = "[out:json];node($id);out body;>;out skel;"
            val resp = service.barDetail(q)
            if (resp.isSuccessful) {
                resp.body()?.let { bars ->
                    if (bars.elements.isNotEmpty()) {
                        val b = bars.elements[0]
                        nearby = NearbyBar(
                            b.id,
                            b.tags.getOrDefault("name", ""),
                            b.tags.getOrDefault("amenity", ""),
                            b.lat,
                            b.lon,
                            b.tags
                        )
                    }
                } ?: onError(BARS_LOAD_ERROR)
            } else {
                onError(BARS_READ_ERROR)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            onError(BARS_FAILED_INTERNET_ERROR)
        } catch (ex: Exception) {
            ex.printStackTrace()
            onError(BARS_FAILED_ERROR)
        }
        return nearby
    }

    fun getBarsByUsersCountDesc(): LiveData<List<BarItem>?> {
        return cache.getBarsByUsersCountDesc()
    }

    fun getBarsByUsersCountAsc(): LiveData<List<BarItem>?> {
        return cache.getBarsByUsersCountAsc()
    }

    fun getBarsByNameAsc(): LiveData<List<BarItem>?> {
        return cache.getBarsByNameAsc()
    }

    fun getBarsByNameDesc(): LiveData<List<BarItem>?> {
        return cache.getBarsByNameDesc()
    }

    fun dbFriends(): LiveData<List<FriendItem>?> {
        return cache.getFriends()
    }

    fun dbFriendsLocation(): LiveData<List<FriendLocationItem>?> {
        return cache.getFriendsLocation()
    }

    companion object {
        @Volatile
        private var INSTANCE: DataRepository? = null

        fun getInstance(service: RestApi, cache: LocalCache): DataRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: DataRepository(service, cache).also { INSTANCE = it }
            }

        @SuppressLint("SimpleDateFormat")
        fun dateToTimeStamp(date: String): Long {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date)?.time ?: 0L
        }

        @SuppressLint("SimpleDateFormat")
        fun timestampToDate(time: Long): String {
            val netDate = Date(time * 1000)
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(netDate)
        }
    }

    private fun hashUserData(password: String): String {
        var digest = ""
        try {
            val crypt = MessageDigest.getInstance("MD5")
            crypt.update(password.toByteArray())
            digest = BigInteger(1, crypt.digest()).toString(16)


        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return digest
    }
}