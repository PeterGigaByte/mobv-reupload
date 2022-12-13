package fei.stu.mobv.viewModels

import androidx.lifecycle.*
import fei.stu.mobv.database.DataRepository
import fei.stu.mobv.helper.Evento
import fei.stu.mobv.viewModels.items.MyLocation
import fei.stu.mobv.viewModels.items.NearbyBar
import kotlinx.coroutines.launch

class LocateViewModel(private val repository: DataRepository): ViewModel() {
    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    val loading = MutableLiveData(false)

    val myLocation = MutableLiveData<MyLocation>(null)
    val myBar= MutableLiveData<NearbyBar>(null)

    private val _checkedIn = MutableLiveData<Evento<Boolean>>()
    val checkedIn: LiveData<Evento<Boolean>>
        get() = _checkedIn


    val bars : LiveData<List<NearbyBar>> = myLocation.switchMap {
        liveData {
            loading.postValue(true)
            it?.let { it ->
                val b = repository.apiNearbyBars(it.lat, it.lon) { _message.postValue(Evento(it)) }
                emit(b)
                if (myBar.value == null) {
                    myBar.postValue(b.firstOrNull())
                }
            } ?: emit(listOf())
            loading.postValue(false)
        }
    }


    fun checkMe(){
        viewModelScope.launch {
            loading.postValue(true)
            myBar.value?.let { it ->
                repository.apiCheckIn(
                    it,
                    { _message.postValue(Evento(it)) },
                    { _checkedIn.postValue(Evento(it)) })
            }
            loading.postValue(false)
        }
    }

    fun show(msg: String){ _message.postValue(Evento(msg))}
}