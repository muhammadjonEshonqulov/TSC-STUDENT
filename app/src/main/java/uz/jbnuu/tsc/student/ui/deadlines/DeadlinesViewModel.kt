package uz.jbnuu.tsc.student.ui.deadlines

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.jbnuu.tsc.student.data.Repository
import uz.jbnuu.tsc.student.model.subjects.Task
import javax.inject.Inject

@HiltViewModel
class DeadlinesViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {
    var landscape = false


    private val _taskDataResponse = Channel<List<Task>>()
    var taskDataResponse = _taskDataResponse.receiveAsFlow()

    fun getTaskData() = viewModelScope.launch {
        _taskDataResponse.send(repository.local.getTaskData().stateIn(this).value)
    }

//    val tasks = repository.local.getTaskData()

}