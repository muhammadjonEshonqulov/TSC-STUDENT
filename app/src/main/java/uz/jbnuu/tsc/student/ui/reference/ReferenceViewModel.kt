package uz.jbnuu.tsc.student.ui.reference

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import uz.jbnuu.tsc.student.R
import uz.jbnuu.tsc.student.app.App
import uz.jbnuu.tsc.student.data.Repository
import uz.jbnuu.tsc.student.model.login.hemis.LoginHemisResponse
import uz.jbnuu.tsc.student.model.login.student.LoginStudentBody
import uz.jbnuu.tsc.student.model.reference.ReferenceResponse
import uz.jbnuu.tsc.student.utils.NetworkResult
import uz.jbnuu.tsc.student.utils.handleResponse
import uz.jbnuu.tsc.student.utils.hasInternetConnection
import javax.inject.Inject

@HiltViewModel
class ReferenceViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    private val _referenceResponse = Channel<NetworkResult<ReferenceResponse>>()
    var referenceResponse = _referenceResponse.receiveAsFlow()

    fun reference() = viewModelScope.launch {
        _referenceResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.studentReference()
                _referenceResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _referenceResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _referenceResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }

    private val _referenceDownloadResponse = Channel<NetworkResult<ResponseBody>>()
    var referenceDownloadResponse = _referenceDownloadResponse.receiveAsFlow()

    fun referenceDownload(url: String, token: String) = viewModelScope.launch {
        _referenceDownloadResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.studentReferenceDownload(url, token)
                _referenceDownloadResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _referenceDownloadResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _referenceDownloadResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
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