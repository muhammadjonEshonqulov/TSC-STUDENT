package uz.jbnuu.tsc.student.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.student.databinding.AdminFragmentBinding
import uz.jbnuu.tsc.student.model.student.NotificationsData
import uz.jbnuu.tsc.student.model.student.PushNotification
import uz.jbnuu.tsc.student.utils.Constants
import uz.jbnuu.tsc.student.utils.NetworkResult
import uz.jbnuu.tsc.student.utils.collectLatestLA
import uz.jbnuu.tsc.student.utils.snackBar

@AndroidEntryPoint
class AdminFragment : Fragment() {

    private val vm: AdminViewModel by viewModels()
    lateinit var binding: AdminFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = AdminFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.send.setOnClickListener {
            sendNotification(Constants.full_url, PushNotification(NotificationsData(99, "My tittle", "My own text", "My own sub text"), Constants.topic))
        }
    }


    private fun sendNotification(full_url: String, notification: PushNotification) {
        try {
            vm.postNotify(full_url, notification) // api(requireContext()).postNotification(notification)
            vm.notificationResponse.collectLatestLA(lifecycleScope) {
                when (it) {
                    is NetworkResult.Success -> {

                    }
                    is NetworkResult.Error -> {

                        snackBar(binding, it.message.toString())
                    }
                    is NetworkResult.Loading -> {

                    }
                }

            }
        } catch (e: Exception) {
            snackBar(binding, "Error message->  : ${e.message}")
        }
    }

}