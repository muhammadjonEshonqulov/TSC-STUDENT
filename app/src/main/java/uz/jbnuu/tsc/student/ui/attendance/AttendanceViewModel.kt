package uz.jbnuu.tsc.student.ui.attendance

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
import uz.jbnuu.tsc.student.model.attendance.AttendanceResponse
import uz.jbnuu.tsc.student.model.login.hemis.LoginHemisResponse
import uz.jbnuu.tsc.student.model.login.student.LoginStudentBody
import uz.jbnuu.tsc.student.model.semester.SemestersResponse
import uz.jbnuu.tsc.student.model.subjects.SubjectsResponse
import uz.jbnuu.tsc.student.utils.NetworkResult
import uz.jbnuu.tsc.student.utils.handleResponse
import uz.jbnuu.tsc.student.utils.hasInternetConnection
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
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

    private val _attendanceResponse = Channel<NetworkResult<AttendanceResponse>>()
    var attendanceResponse = _attendanceResponse.receiveAsFlow()

    fun attendance(semester: String?) = viewModelScope.launch {
        _attendanceResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.attendance(semester)
                _attendanceResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _attendanceResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _attendanceResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }

    private val _subjectsResponse = Channel<NetworkResult<SubjectsResponse>>()
    var subjectsResponse = _subjectsResponse.receiveAsFlow()

    fun subjects() = viewModelScope.launch {
        _subjectsResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.subjects()
                _subjectsResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _subjectsResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _subjectsResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }

//    private val _subjectResponse = Channel<NetworkResult<SubjectResponse>>()
//    var subjectResponse = _subjectResponse.receiveAsFlow()
//
//    fun subject() = viewModelScope.launch {
//        _subjectResponse.send(NetworkResult.Loading())
//        if (hasInternetConnection(getApplication())) {
//            try {
//                val response = repository.remote.subject()
//                _subjectResponse.send(handleResponse(response))
//            } catch (e: Exception) {
//                _subjectResponse.send(NetworkResult.Error("Xatolik : " + e.message))
//            }
//        } else {
//            _subjectResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
//        }
//    }

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