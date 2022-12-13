package fei.stu.mobv.viewModels

import androidx.lifecycle.*
import fei.stu.mobv.database.DataRepository
import fei.stu.mobv.helper.Evento
import fei.stu.mobv.viewModels.items.NearbyBar
import fei.stu.mobv.widgets.items.BarDetailItem
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: DataRepository) : ViewModel() {
    private val _message = MutableLiveData<Evento<String>>()
    val message: LiveData<Evento<String>>
        get() = _message

    val loading = MutableLiveData(false)

    val bar = MutableLiveData<NearbyBar>(null)
    val type = bar.map { it?.tags?.getOrDefault("amenity", "") ?: "" }
    val details: LiveData<List<BarDetailItem>> = bar.switchMap {
        liveData {
            it?.let { it ->
                emit(it.tags.map {
                    BarDetailItem(it.key, it.value)
                })
            } ?: emit(emptyList())
        }
    }

    fun loadBar(id: String) {
        if (id.isBlank())
            return
        viewModelScope.launch {
            loading.postValue(true)
            bar.postValue(repository.apiBarDetail(id) { _message.postValue(Evento(it)) })
            loading.postValue(false)
        }
    }
}