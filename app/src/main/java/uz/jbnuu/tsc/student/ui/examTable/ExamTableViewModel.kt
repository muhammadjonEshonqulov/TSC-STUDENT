package uz.jbnuu.tsc.student.ui.examTable

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
import uz.jbnuu.tsc.student.model.examTable.ExamTableResponse
import uz.jbnuu.tsc.student.model.login.hemis.LoginHemisResponse
import uz.jbnuu.tsc.student.model.login.student.LoginStudentBody
import uz.jbnuu.tsc.student.model.login.student.LoginStudentResponse
import uz.jbnuu.tsc.student.model.semester.SemestersResponse
import uz.jbnuu.tsc.student.utils.NetworkResult
import uz.jbnuu.tsc.student.utils.handleResponse
import uz.jbnuu.tsc.student.utils.hasInternetConnection
import javax.inject.Inject

@HiltViewModel
class ExamTableViewModel @Inject constructor(
    application: Application, private val repository: Repository
) : AndroidViewModel(application) {

    private val _examTableResponse = Channel<NetworkResult<ExamTableResponse>>()
    var examTableResponse = _examTableResponse.receiveAsFlow()

    fun examTable(semester: String?) = viewModelScope.launch {
        _examTableResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.examTable(semester)
                _examTableResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _examTableResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _examTableResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }

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

    private val _loginResponse = Channel<NetworkResult<LoginStudentResponse>>()
    var loginResponse = _loginResponse.receiveAsFlow()

    fun loginStudent(loginStudentBody: LoginStudentBody) = viewModelScope.launch {
        _loginResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.loginStudent(loginStudentBody)
                _loginResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _loginResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _loginResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }
}