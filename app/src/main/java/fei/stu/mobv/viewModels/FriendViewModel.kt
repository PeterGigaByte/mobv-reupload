package fei.stu.mobv.viewModels

import androidx.lifecycle.*
import fei.stu.mobv.database.DataRepository
import fei.stu.mobv.database.items.FriendItem
import fei.stu.mobv.helper.Evento
import kotlinx.coroutines.launch

class FriendViewModel(private val repository: DataRepository) : ViewModel() {
    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    val loading = MutableLiveData(false)

    private val _response = MutableLiveData<Boolean>(null)
    val response: LiveData<Boolean>
        get() = _response
    val friends: LiveData<List<FriendItem>?> =
        liveData {
            loading.postValue(true)
            repository.apiFriendsList { _message.postValue(Evento(it)) }
            loading.postValue(false)
            emitSource(repository.dbFriends())
        }

    fun addFriend(name: String) {
        viewModelScope.launch {
            loading.postValue(true)
            repository.apiAddFriend(
                name,
                { _message.postValue(Evento(it)) },
                { _response.postValue(it) }
            )
            loading.postValue(false)
        }
    }

    fun deleteFriend(name: String) {
        viewModelScope.launch {
            loading.postValue(true)
            repository.apiDeleteFriend(
                name,
                { _message.postValue(Evento(it)) },
                { _response.postValue(it) }
            )
            loading.postValue(false)
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            loading.postValue(true)
            repository.apiFriendsList { _message.postValue(Evento(it)) }
            loading.postValue(false)
        }
    }

    fun show(msg: String) {
        _message.postValue(Evento(msg))
    }
}