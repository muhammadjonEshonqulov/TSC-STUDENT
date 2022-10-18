package uz.jbnuu.tsc.student.ui.examTable

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.student.adapters.ExamsAdapter
import uz.jbnuu.tsc.student.adapters.SemesterAdapter
import uz.jbnuu.tsc.student.base.BaseFragment
import uz.jbnuu.tsc.student.databinding.ExamTableFragmentBinding
import uz.jbnuu.tsc.student.model.login.student.LoginStudentBody
import uz.jbnuu.tsc.student.model.semester.SemestersData
import uz.jbnuu.tsc.student.utils.NetworkResult
import uz.jbnuu.tsc.student.utils.Prefs
import uz.jbnuu.tsc.student.utils.collectLA
import uz.jbnuu.tsc.student.utils.collectLatestLA
import javax.inject.Inject

@AndroidEntryPoint
class ExamTableFragment : BaseFragment<ExamTableFragmentBinding>(ExamTableFragmentBinding::inflate), SemesterAdapter.OnItemClickListener {

    private val vm: ExamTableViewModel by viewModels()
    private val examsAdapter: ExamsAdapter by lazy { ExamsAdapter() }
    private val semesterAdapter: SemesterAdapter by lazy { SemesterAdapter(this) }

    @Inject
    lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()
        semesters()
    }

    override fun onViewCreatedd(view: View, savedInstanceState: Bundle?) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setupRecycler()
        binding.swipeRefreshLayout.isRefreshing = false
        binding.swipeRefreshLayout.isEnabled = false
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun setupRecycler() {
        binding.listExamTable.apply {
            adapter = examsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        binding.listSemester.apply {
            adapter = semesterAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun semesters() {
        vm.semesters()
        vm.semestersResponse.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }
                is NetworkResult.Success -> {
                    closeLoader()
                    if (it.data?.success == true) {
                        it.data.data?.let {
                            semesterAdapter.setData(it)
                        }

                    } else {
                        snackBar(binding, "Hemis " + it.data?.error)
                    }
                }
                is NetworkResult.Error -> {
                    closeLoader()
                    if (it.code == 401) {
                        loginHemis()
                    } else {
                        snackBar(binding, it.message.toString())
                    }
                }
            }
        }
    }

    private fun examTable(semester: String?) {
        vm.examTable(semester)
        vm.examTableResponse.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }
                is NetworkResult.Success -> {
                    closeLoader()
                    if (it.data?.success == true) {
                        it.data.data?.let {
                            if (it.isNotEmpty()){
                                examsAdapter.setData(it)
                                binding.listExamTable.visibility = View.VISIBLE
                                binding.notFoundLesson.visibility = View.GONE
                            } else {
                                binding.listExamTable.visibility = View.GONE
                                binding.notFoundLesson.visibility = View.VISIBLE
                            }
                        }

                    } else {
                        snackBar(binding, "Hemis " + it.data?.error)
                    }
                }
                is NetworkResult.Error -> {
                    closeLoader()
                    if (it.code == 401) {
                        loginHemis()
                    } else {
                        snackBar(binding, it.message.toString())
                    }
                }
            }
        }
    }

    private fun loginHemis() {
        vm.loginHemis(LoginStudentBody(prefs.get(prefs.login, ""), prefs.get(prefs.password, "")))
        vm.loginHemisResponse.collectLatestLA(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }
                is NetworkResult.Success -> {
                    if (it.data?.success == true) {
                        it.data.apply {
                            data?.token?.let {
                                prefs.save(prefs.hemisToken, it)
                                semesters()
                            }
                        }
                    } else {
                        it.data?.error?.let {
                            snackBar(binding, " " + it)
                        }
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
        binding.swipeRefreshLayout.isRefreshing = true
    }

    private fun closeLoader() {
        binding.swipeRefreshLayout.isRefreshing = false
    }

    override fun onItemClick(data: SemestersData, position: Int, type: Int) {
        if (type == 1) {
            var lastIndex = -1
            semesterAdapter.dataProduct.forEachIndexed { index, semestersData ->
                if (semestersData.currentExtra == true) {
                    lastIndex = index
                    semestersData.currentExtra = false
                }
                if (index == position) {
                    semestersData.currentExtra = true
                }
            }
            examTable(data.code)
            binding.listSemester.post { semesterAdapter.notifyItemChanged(position) }

            if (lastIndex >= 0) {
                semesterAdapter.notifyItemChanged(lastIndex)
            }
        }
    }


}