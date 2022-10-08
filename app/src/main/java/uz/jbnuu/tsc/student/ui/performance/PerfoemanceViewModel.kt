package uz.jbnuu.tsc.student.ui.performance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import uz.jbnuu.tsc.student.R
import uz.jbnuu.tsc.student.app.App
import uz.jbnuu.tsc.student.data.Repository
import uz.jbnuu.tsc.student.model.login.hemis.LoginHemisResponse
import uz.jbnuu.tsc.student.model.login.student.LoginStudentBody
import uz.jbnuu.tsc.student.model.performance.PerformanceResponse
import uz.jbnuu.tsc.student.model.semester.SemestersResponse
import uz.jbnuu.tsc.student.utils.NetworkResult
import uz.jbnuu.tsc.student.utils.handleResponse
import uz.jbnuu.tsc.student.utils.hasInternetConnection
import javax.inject.Inject

@HiltViewModel
class PerfoemanceViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {
    var landscape = false

    private val _semestersResponse = Channel<NetworkResult<SemestersResponse>>()
    var semestersResponse = _semestersResponse.receiveAsFlow()

    fun semesters() = viewModelScope.launch {
        _semestersResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.semesters()
                _semestersResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _semestersResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _semestersResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }

    private val _performanceResponse = Channel<NetworkResult<PerformanceResponse>>()
    var performanceResponse = _performanceResponse.receiveAsFlow()

    fun performance() = viewModelScope.launch {
        _performanceResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.performance()
                _performanceResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _performanceResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _performanceResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }

    private val _loginHemisResponse = Channel<NetworkResult<LoginHemisResponse>>()
    var loginHemisResponse = _loginHemisResponse.receiveAsFlow()

    fun loginHemis(loginHemisBody: LoginStudentBody) = viewModelScope.launch {
        _loginHemisResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.loginHemis(loginHemisBody)
                _loginHemisResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _loginHemisResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _loginHemisResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }
}