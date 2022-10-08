package uz.jbnuu.tsc.student.ui.splash

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
import uz.jbnuu.tsc.student.model.me.MeResponse
import uz.jbnuu.tsc.student.model.student.PushNotification
import uz.jbnuu.tsc.student.utils.NetworkResult
import uz.jbnuu.tsc.student.utils.handleResponse
import uz.jbnuu.tsc.student.utils.hasInternetConnection
import javax.inject.Inject

@HiltViewModel
class SplashVIewModel @Inject constructor(
    val repository: Repository,
    application: Application,
) : AndroidViewModel(application) {

    private val _meResponse = Channel<NetworkResult<MeResponse>>()
    var meResponse = _meResponse.receiveAsFlow()

    fun me() = viewModelScope.launch {
        _meResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.me()
                _meResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _meResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _meResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }

    private val _notificationResponse = Channel<NetworkResult<ResponseBody>>()
    var notificationResponse = _notificationResponse.receiveAsFlow()

    fun postNotify(full_url: String, notification: PushNotification) = viewModelScope.launch {
        _notificationResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.postNotification(full_url, notification)
                _notificationResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _notificationResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _notificationResponse.send(NetworkResult.Error("Server bilan aloqa yo'q"))
        }
    }

}