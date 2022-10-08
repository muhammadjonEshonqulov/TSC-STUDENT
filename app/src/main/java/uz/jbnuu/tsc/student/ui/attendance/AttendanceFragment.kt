package uz.jbnuu.tsc.student.ui.attendance

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.student.adapters.AttendanceAdapter
import uz.jbnuu.tsc.student.adapters.SemesterAdapter
import uz.jbnuu.tsc.student.adapters.SubjectsAdapter
import uz.jbnuu.tsc.student.base.BaseFragment
import uz.jbnuu.tsc.student.databinding.AttendanceFragmentBinding
import uz.jbnuu.tsc.student.model.attendance.AttendanceData
import uz.jbnuu.tsc.student.model.login.student.LoginStudentBody
import uz.jbnuu.tsc.student.model.schedule.Subject
import uz.jbnuu.tsc.student.model.semester.SemestersData
import uz.jbnuu.tsc.student.model.subjects.SubjectsData
import uz.jbnuu.tsc.student.utils.*
import javax.inject.Inject

@AndroidEntryPoint
class AttendanceFragment : BaseFragment<AttendanceFragmentBinding>(AttendanceFragmentBinding::inflate), SemesterAdapter.OnItemClickListener, AttendanceAdapter.OnItemClickListener {

    private val vm: AttendanceViewModel by viewModels()
    private val semesterAdapter: SemesterAdapter by lazy { SemesterAdapter(this) }
    private val attendanceAdapter: AttendanceAdapter by lazy { AttendanceAdapter(this) }
    private var subjects: ArrayList<SubjectsData>? = null
    private var attendances: ArrayList<AttendanceData>? = null


    @Inject
    lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        semesters()
        subjects()
        sharedElementEnterTransition = MaterialContainerTransform()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.listSemester.adapter = null
        binding.listAttendance.adapter = null
    }

    override fun onViewCreatedd(view: View, savedInstanceState: Bundle?) {

        setupSemestersList()
        setupScheduleList()
        binding.swipeRefreshLayout.isRefreshing = false
        binding.swipeRefreshLayout.isEnabled = false
        binding.group.text = prefs.get(prefs.group, "")
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

            attendance(data.code)
            if (lastIndex >= 0) {
                semesterAdapter.notifyItemChanged(lastIndex)
            }
        }

        val currentSubjects = ArrayList<SubjectsData>()

        currentSubjects.clear()
        subjects?.forEach { subjectsData ->
            if (data.code == subjectsData._semester) {
                currentSubjects.add(subjectsData)
            }
        }

        if (currentSubjects.isNotEmpty()) {
            val spinnerData = ArrayList<SubjectsData>()
            spinnerData.add(SubjectsData(Subject(null, "Fanni tanlang"), null, null, null, null))
            spinnerData.addAll(currentSubjects)

            val subjectsAdapter = SubjectsAdapter(requireContext(), spinnerData)
            binding.spinnerWeeks.adapter = subjectsAdapter
            binding.spinnerWeeks.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                  //  attendance(spinnerData.get(position).subject?.id, data.code?.toInt())

                    var attendances2 = ArrayList<AttendanceData>()
                    attendances2.clear()

                    attendances?.forEach {
                        if (it.subject == spinnerData.get(position).subject) {
                            attendances2.add(it)
                        }
                    }
                    if (attendances2.isNotEmpty()) {
                        attendanceAdapter.setData(attendances2)
                        binding.listAttendanceLay.visibility = View.VISIBLE
                        binding.notFoundLesson.visibility = View.GONE
                    } else {
                        binding.listAttendanceLay.visibility = View.GONE
                        binding.notFoundLesson.visibility = View.VISIBLE
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
        } else {
            val subjectsAdapter = SubjectsAdapter(requireContext(), listOf())
            binding.spinnerWeeks.adapter = subjectsAdapter
        }
    }


    private fun setupSemestersList() {
        binding.listSemester.apply {
            adapter = semesterAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupScheduleList() {
        binding.listAttendance.apply {
            adapter = attendanceAdapter
            layoutManager = LinearLayoutManager(requireContext())
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
                        loginHemis("semesters")
                    } else {
                        snackBar(binding, it.message.toString())
                    }
                }
            }
        }
    }

    private fun loginHemis(s: String) {
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

    private fun attendance(semester: String?) {
        vm.attendance(semester)
        vm.attendanceResponse.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }
                is NetworkResult.Success -> {
                    closeLoader()
                    if (it.data?.success == true) {
                        it.data.data?.let {
                            if (it.isNotEmpty()) {
                                if (attendances == null) {
                                    attendances = ArrayList()
                                }
                                attendances?.addAll(it)
                                attendances?.let {
                                    attendanceAdapter.setData(it)
                                }
                                binding.listAttendanceLay.visibility = View.VISIBLE
                                binding.notFoundLesson.visibility = View.GONE
                            } else {
                                binding.listAttendanceLay.visibility = View.GONE
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
                        loginHemis("attendance")
//                        prefs.clear()
//                        findNavController().navigateSafe(R.id.action_groupsFragment_to_loginFragment)
                    } else {
                        snackBar(binding, it.message.toString())
                    }
                }
            }
        }
    }

    private fun subjects() {
        vm.subjects()
        vm.subjectsResponse.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }
                is NetworkResult.Success -> {
                    closeLoader()
                    if (it.data?.success == true) {
                        it.data.data?.let {
                            if (subjects == null) {
                                subjects = ArrayList()
                            }
                            subjects?.clear()
                            subjects?.addAll(it)
                        }

                    } else {
                        snackBar(binding, "Hemis " + it.data?.error)
                    }
                }
                is NetworkResult.Error -> {
                    closeLoader()
                    if (it.code == 401) {
                        loginHemis("subjects")
//                        prefs.clear()
//                        findNavController().navigateSafe(R.id.action_groupsFragment_to_loginFragment)
                    } else {
                        snackBar(binding, it.message.toString())
                    }
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

    override fun onItemClick(data: AttendanceData, type: Int) {

    }
}