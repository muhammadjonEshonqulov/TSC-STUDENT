package uz.jbnuu.tsc.student.ui.login

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.student.R
import uz.jbnuu.tsc.student.base.BaseFragment
import uz.jbnuu.tsc.student.base.ProgressDialog
import uz.jbnuu.tsc.student.databinding.LoginFragmentBinding
import uz.jbnuu.tsc.student.model.login.student.LoginStudentBody
import uz.jbnuu.tsc.student.utils.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginFragmentBinding>(LoginFragmentBinding::inflate), View.OnClickListener {

    private val vm: LoginViewModel by viewModels()
    var progressDialog: ProgressDialog? = null

    @Inject
    lateinit var prefs: Prefs

    override fun onViewCreatedd(view: View, savedInstanceState: Bundle?) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding.loginBtn.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.loginBtn -> {
                hideKeyboard()
                val login = binding.loginAuth.text.toString()
                val password = binding.passwordAuth.text.toString()
                if (login.isNotEmpty() && password.isNotEmpty()) {
                    loginStudent(LoginStudentBody(login, password))
                } else {
                    if (login.isEmpty()) {
                        binding.loginAuth.error = "Loginingizni kiriting"
                    }
                    if (password.isEmpty()) {
                        binding.passwordAuth.error = "Passwordni kiriting"
                    }
                }
            }
        }
    }

    private fun loginStudent(loginStudentBody: LoginStudentBody) {
        vm.loginStudent(loginStudentBody)
        vm.loginResponse.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }
                is NetworkResult.Success -> {
                    closeLoader()
                    if (it.data?.status == 1) {
                        it.data.apply {
                            token?.let {
                                prefs.save(prefs.token, it)
                            }
                            hemins_token?.let {
                                prefs.save(prefs.hemisToken, it)
                            }
                            prefs.save(prefs.login, "${loginStudentBody.login}")
                            prefs.save(prefs.password, "${loginStudentBody.password}")
                            getme?.apply {

                                semester?.code?.let {
                                    prefs.save(prefs.semester, it)// it
                                }
                                first_name?.let {
                                    prefs.save(prefs.name, it)
                                }
                                second_name?.let {
                                    prefs.save(prefs.fam, it)
                                }
                                group?.name?.let {
                                    prefs.save(prefs.group, it)
                                }
                                image?.let {
                                    prefs.save(prefs.image, it)
                                }
                            }

                            findNavController().navigateSafe(R.id.action_loginFragment_to_studentMainFragment)
                        }
                    } else {
                        snackBar(binding, "status " + it.data?.status)
                    }
                }
                is NetworkResult.Error -> {
                    closeLoader()
                    snackBar(binding, it.message.toString())
                }
            }
        }
    }

    private fun showLoader() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(binding.root.context, "Iltimos kuting...")
        }
        progressDialog?.show()
    }

    private fun closeLoader() {
        progressDialog?.dismiss()
    }
}