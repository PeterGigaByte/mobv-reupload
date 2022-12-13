package fei.stu.mobv.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fei.stu.mobv.database.items.BarItem
import fei.stu.mobv.database.items.FriendItem
import fei.stu.mobv.database.items.FriendLocationItem

@Database(
    entities = [BarItem::class, FriendItem::class, FriendLocationItem::class],
    version = 8,
    exportSchema = false
)
abstract class AppRoomDatabase: RoomDatabase() {
    abstract fun appDao(): DbDao

    companion object{
        @Volatile
        private var INSTANCE: AppRoomDatabase? = null

        fun getInstance(context: Context): AppRoomDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also {  INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppRoomDatabase::class.java, "barsDatabase"
            ).fallbackToDestructiveMigration()
                .build()
    }
}