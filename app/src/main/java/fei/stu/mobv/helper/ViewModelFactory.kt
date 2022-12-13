package fei.stu.mobv.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fei.stu.mobv.database.DataRepository
import fei.stu.mobv.viewModels.*


class ViewModelFactory(private val repository: DataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(BarsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BarsViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(LocateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LocateViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(FriendViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendViewModel(repository) as T
        }

        if (modelClass.isAssignableFrom(FriendLocationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendLocationViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}