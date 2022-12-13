package fei.stu.mobv.viewModels

import androidx.lifecycle.*
import fei.stu.mobv.database.DataRepository
import fei.stu.mobv.database.items.FriendLocationItem
import fei.stu.mobv.helper.Evento
import kotlinx.coroutines.launch

class FriendLocationViewModel(private val repository: DataRepository) : ViewModel() {
    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    val loading = MutableLiveData(false)

    private val _response = MutableLiveData<Boolean>(null)

    val response: LiveData<Boolean>
        get() = _response

    val friendsLocation: LiveData<List<FriendLocationItem>?> =
        liveData {
            loading.postValue(true)
            repository.apiFriendsLocationList { _message.postValue(Evento(it)) }
            loading.postValue(false)
            emitSource(repository.dbFriendsLocation())
        }

    fun refreshData() {
        viewModelScope.launch {
            loading.postValue(true)
            repository.apiFriendsLocationList { _message.postValue(Evento(it)) }
            loading.postValue(false)
        }
    }

    fun show(msg: String) {
        _message.postValue(Evento(msg))
    }
}