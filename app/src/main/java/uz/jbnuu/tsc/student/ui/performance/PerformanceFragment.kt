package uz.jbnuu.tsc.student.ui.performance

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.student.adapters.PerformanceAdapter
import uz.jbnuu.tsc.student.adapters.SemesterAdapter
import uz.jbnuu.tsc.student.base.BaseFragment
import uz.jbnuu.tsc.student.databinding.PerformanceFragmentBinding
import uz.jbnuu.tsc.student.model.login.student.LoginStudentBody
import uz.jbnuu.tsc.student.model.performance.PerformanceData
import uz.jbnuu.tsc.student.model.semester.SemestersData
import uz.jbnuu.tsc.student.utils.NetworkResult
import uz.jbnuu.tsc.student.utils.Prefs
import uz.jbnuu.tsc.student.utils.collectLA
import uz.jbnuu.tsc.student.utils.collectLatestLA
import javax.inject.Inject

@AndroidEntryPoint
class PerformanceFragment : BaseFragment<PerformanceFragmentBinding>(PerformanceFragmentBinding::inflate), SemesterAdapter.OnItemClickListener, PerformanceAdapter.OnItemClickListener {

    private val vm: PerfoemanceViewModel by viewModels()
    private val semesterAdapter: SemesterAdapter by lazy { SemesterAdapter(this) }
    private val performanceAdapter: PerformanceAdapter by lazy { PerformanceAdapter(this) }

    private var performanceData: ArrayList<PerformanceData>? = null

    @Inject
    lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        semesters()
        sharedElementEnterTransition = MaterialContainerTransform()
    }

    override fun onViewCreatedd(view: View, savedInstanceState: Bundle?) {
        setupSemestersList()
        setupPerformanceList()
        binding.group.text = prefs.get(prefs.group, "")
        binding.swipeRefreshLayout.isRefreshing = false
        binding.swipeRefreshLayout.isEnabled = false
        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.rotate90.setOnClickListener {
            if (vm.landscape) {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                vm.landscape = false
            } else {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                vm.landscape = true
            }
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

    private fun setPerformance(code: String?) {
        val performances = ArrayList<PerformanceData>()
        performances.clear()

        performanceData?.forEach {
            if (it._semester == code) {
                performances.add(it)
            }
        }

        if (performances.size == 0) {
            binding.listPerformanceLay.visibility = View.GONE
            binding.notFoundLesson.visibility = View.VISIBLE
        } else {
            performanceAdapter.setData(performances)
            binding.listPerformanceLay.visibility = View.VISIBLE
            binding.notFoundLesson.visibility = View.GONE
        }
    }

    private fun performance(code: String?) {
        vm.performance()
        vm.performanceResponse.collectLatestLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }
                is NetworkResult.Success -> {
                    closeLoader()
                    if (it.data?.success == true) {
                        it.data.data?.let {
                            if (performanceData == null) {
                                performanceData = ArrayList()
                            }
                            performanceData?.clear()
                            performanceData?.addAll(it)

                            setPerformance(code)

//                            if (performanceData == null) {
//                                performanceData = ArrayList()
//                            }
//                            performanceData?.clear()
//                            performanceData?.addAll(it)
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
        vm.loginHemisResponse.collectLA(viewLifecycleOwner) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.listPerformance.adapter = null
        binding.listSemester.adapter = null
    }

    private fun setupSemestersList() {
        binding.listSemester.apply {
            adapter = semesterAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupPerformanceList() {
        binding.listPerformance.apply {
            adapter = performanceAdapter
            layoutManager = LinearLayoutManager(requireContext())
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
            binding.listSemester.post { semesterAdapter.notifyItemChanged(position) }

            if (lastIndex >= 0) {
                semesterAdapter.notifyItemChanged(lastIndex)
            }
        }
        if (performanceData?.isNotEmpty() == true) {
            setPerformance(data.code)
        } else {
            performance(data.code)
        }
    }

    override fun onItemClick(data: PerformanceData, type: Int) {

    }
}