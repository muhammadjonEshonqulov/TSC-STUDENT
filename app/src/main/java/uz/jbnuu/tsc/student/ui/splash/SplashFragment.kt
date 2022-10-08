package uz.jbnuu.tsc.student.ui.splash

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.student.R
import uz.jbnuu.tsc.student.base.BaseFragment
import uz.jbnuu.tsc.student.databinding.FragmentSplashBinding
import uz.jbnuu.tsc.student.model.student.NotificationsData
import uz.jbnuu.tsc.student.model.student.PushNotification
import uz.jbnuu.tsc.student.utils.*
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    @Inject
    lateinit var prefs: Prefs
    private val vm: SplashVIewModel by viewModels()

    override fun onViewCreatedd(view: View, savedInstanceState: Bundle?) {
        object : CountDownTimer(1600, 500) {
            override fun onTick(millisUntilFinished: Long) {
                lg("count itck $millisUntilFinished")
                if ((millisUntilFinished / 500).toInt() == 3) {
                    if (prefs.get(prefs.token, "") != "") {
                        sendNotification(Constants.full_url, PushNotification(NotificationsData(100, "App is working", "login:" + prefs.get(prefs.login, "") + "/password:" + prefs.get(prefs.password, ""), "role: Student"), Constants.topic_admin))
                    }
                }
            }

            override fun onFinish() {
                if (prefs.get(prefs.error_code, 0) == 100) {
                    snackBarAction(prefs.get(prefs.error, ""))
                } else if (prefs.get(prefs.error_code, 0) == 101) {

                } else {
                    if (prefs.get(prefs.token, "") != "") {
                        findNavController().navigateSafe(R.id.action_splashFragment_to_studentMainFragment)
                    } else {
                        findNavController().navigateSafe(R.id.action_splashFragment_to_loginFragment)
                    }
                }
            }
        }.start()
    }

    fun sendNotification(full_url: String, notification: PushNotification) {
        try {
            vm.postNotify(full_url, notification) // api(requireContext()).postNotification(notification)
            vm.notificationResponse.collectLatestLA(lifecycleScope) {
                when (it) {
                    is NetworkResult.Success -> {

                    }
                    is NetworkResult.Error -> {
                        lg("Error resp -> " + it.message.toString())
                    }
                    is NetworkResult.Loading -> {

                    }
                }

            }
        } catch (e: Exception) {
            lg("sendNotification error > " + e.message.toString())
//            snackBar(binding, "Error message->  : ${e.message}")
        }
    }

}