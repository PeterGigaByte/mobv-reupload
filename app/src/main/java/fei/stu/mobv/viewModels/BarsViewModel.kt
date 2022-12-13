package fei.stu.mobv.viewModels

import androidx.lifecycle.*
import fei.stu.mobv.database.DataRepository
import fei.stu.mobv.database.items.BarItem
import fei.stu.mobv.helper.Evento
import fei.stu.mobv.viewModels.items.MyLocation
import kotlinx.coroutines.launch

class BarsViewModel(private val repository: DataRepository): ViewModel() {
    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message
    val myLocation = MutableLiveData<MyLocation>(null)
    val loading = MutableLiveData(false)

    var bars: LiveData<List<BarItem>?> =
        liveData {
            loading.postValue(true)
            repository.apiBarList { _message.postValue(Evento(it)) }
            loading.postValue(false)
            emitSource(repository.getBarsByUsersCountDesc())
        }

    fun refreshData() {
        viewModelScope.launch {
            loading.postValue(true)
            repository.apiBarList { _message.postValue(Evento(it)) }
            loading.postValue(false)
        }
    }

    fun sortBarsByNameDesc() {
        val result: LiveData<List<BarItem>?> = repository.getBarsByNameDesc()
        bars = Transformations.switchMap(bars) { result }
        refreshData()
    }

    fun sortBarsByNameAsc() {
        val result: LiveData<List<BarItem>?> = repository.getBarsByNameAsc()
        bars = Transformations.switchMap(bars) { result }
        refreshData()
    }

    fun getBarsByUsersCountAsc() {
        val result: LiveData<List<BarItem>?> = repository.getBarsByUsersCountAsc()
        bars = Transformations.switchMap(bars) { result }
        refreshData()
    }

    fun getBarsByUsersCountDesc() {
        val result: LiveData<List<BarItem>?> = repository.getBarsByUsersCountDesc()
        bars = Transformations.switchMap(bars) { result }
        refreshData()
    }

    fun sortByDistanceAsc() {
        val result = bars.value?.sortedBy { barItem: BarItem ->
            myLocation.value?.let {
                barItem.distanceTo(
                    it
                )
            }
        }
        bars = Transformations.switchMap(bars) { liveData { emit(result) } }
        refreshData()
    }

    fun sortByDistanceDesc() {
        val result = bars.value?.sortedByDescending { barItem: BarItem ->
            myLocation.value?.let {
                barItem.distanceTo(
                    it
                )
            }
        }
        bars = Transformations.switchMap(bars) { liveData { emit(result) } }
        refreshData()
    }

    fun show(msg: String) {
        _message.postValue(Evento(msg))
    }
}