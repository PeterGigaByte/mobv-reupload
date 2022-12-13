package fei.stu.mobv.helper

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import fei.stu.mobv.api.RestApi
import fei.stu.mobv.database.AppRoomDatabase
import fei.stu.mobv.database.DataRepository
import fei.stu.mobv.database.LocalCache

object Injection {
    private fun provideCache(context: Context): LocalCache {
        val database = AppRoomDatabase.getInstance(context)
        return LocalCache(database.appDao())
    }

    fun provideDataRepository(context: Context): DataRepository {
        return DataRepository.getInstance(RestApi.create(context), provideCache(context))
    }

    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(
            provideDataRepository(
                context
            )
        )
    }
}