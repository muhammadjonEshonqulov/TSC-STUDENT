package uz.jbnuu.tsc.student.ui.schedule

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.student.base.BaseFragment
import uz.jbnuu.tsc.student.adapters.ScheduleAdapter
import uz.jbnuu.tsc.student.adapters.SemesterAdapter
import uz.jbnuu.tsc.student.adapters.WeeksAdapter
import uz.jbnuu.tsc.student.databinding.ScheduleFragmentBinding
import uz.jbnuu.tsc.student.model.schedule.ScheduleData
import uz.jbnuu.tsc.student.model.schedule.WeekDaysData
import uz.jbnuu.tsc.student.model.semester.SemestersData
import uz.jbnuu.tsc.student.utils.NetworkResult
import uz.jbnuu.tsc.student.utils.Prefs
import uz.jbnuu.tsc.student.utils.collectLA
import uz.jbnuu.tsc.student.utils.collectLatestLA
import javax.inject.Inject

@AndroidEntryPoint
class ScheduleFragment : BaseFragment<ScheduleFragmentBinding>(ScheduleFragmentBinding::inflate), ScheduleAdapter.OnItemClickListener, SemesterAdapter.OnItemClickListener {

    private val vm: ScheduleViewModel by viewModels()
    private val scheduleAdapter: ScheduleAdapter by lazy { ScheduleAdapter(this) }
    private val semesterAdapter: SemesterAdapter by lazy { SemesterAdapter(this) }
    private var _week = -1

    @Inject
    lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        semesters()
        sharedElementEnterTransition = MaterialContainerTransform()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.listSemester.adapter = null
        binding.listTimeTable.adapter = null
    }

    override fun onViewCreatedd(view: View, savedInstanceState: Bundle?) {

        binding.swipeRefreshLayout.isRefreshing = true
        binding.swipeRefreshLayout.isEnabled = false
        setupSemestersList()
        setupScheduleList()
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

//        binding.swipeRefreshLayout.setOnRefreshListener {
//            if (_week != -1) {
//                schedule(_week)
//            } else {
//                binding.swipeRefreshLayout.isRefreshing = false
//            }
//        }
    }

    private fun setupSemestersList() {
        binding.listSemester.apply {
            adapter = semesterAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupScheduleList() {
        binding.listTimeTable.apply {
            adapter = scheduleAdapter
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
//                    if (it.code == 401) {
//                        prefs.clear()
//                        findNavController().navigateSafe(R.id.action_groupsFragment_to_loginFragment)
//                    } else {
                    snackBar(binding, it.message.toString())
//                    }
                }
            }
        }
    }

    private fun schedule(week: Int) {
        vm.schedule(week)
        vm.scheduleResponse.collectLatestLA(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }
                is NetworkResult.Success -> {
                    closeLoader()
                    if (it.data?.success == true) {
                        it.data.data?.let { list ->
                            val weekDaysData = ArrayList<WeekDaysData>()
                            list.forEach {
                                val schedules = ArrayList<ScheduleData>()
                                if (weekDaysData.size == 0) {
                                    schedules.add(it)
                                    weekDaysData.add(WeekDaysData(it.lesson_date, schedules))
                                } else {
                                    if (weekDaysData.last().lesson_date == it.lesson_date) {
                                        weekDaysData.last().schedules?.add(it)
                                    } else {
                                        schedules.clear()
                                        schedules.add(it)
                                        weekDaysData.add(WeekDaysData(it.lesson_date, schedules))
                                    }
                                }
                            }
                            if (weekDaysData.isNotEmpty()) {
                                scheduleAdapter.setData(weekDaysData)
                                binding.listTimeTable.visibility = View.VISIBLE
                                binding.notFoundLesson.visibility = View.GONE
                            } else {
                                binding.listTimeTable.visibility = View.GONE
                                binding.notFoundLesson.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        snackBar(binding, "Hemis " + it.data?.error)
                    }
                }
                is NetworkResult.Error -> {
                    closeLoader()
                    snackBar(binding, it.message.toString())
                }
            }
        }
    }

    override fun onItemClick(data: WeekDaysData, type: Int) {

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
//            CoroutineScope(Dispatchers.IO).launch {
//                semesterAdapter.notifyItemChanged(position)
//            }
            if (lastIndex >= 0) {
                semesterAdapter.notifyItemChanged(lastIndex)
            }
        }

//        if (data.weeks?.isEmpty() == true) {
//            binding.listTimeTable.visibility = View.GONE
//            binding.notFoundLesson.visibility = View.VISIBLE
//        } else {
//            binding.listTimeTable.visibility = View.VISIBLE
//            binding.notFoundLesson.visibility = View.GONE
//        }
        data.weeks?.let {
            var dateIndex = -1
            it.forEachIndexed { index, weekData ->
                weekData.end_date?.let { endDate ->
                    weekData.start_date?.let { start_date ->
                        if (System.currentTimeMillis() / 1000L in start_date..endDate) {
                            dateIndex = index
                            return@forEachIndexed
                        }
                    }
                }
            }
            val weeksAdapter = WeeksAdapter(requireContext(), it)
            binding.spinnerWeeks.adapter = weeksAdapter
            if (dateIndex >= 0) {
                binding.spinnerWeeks.setSelection(dateIndex)
            }
            binding.spinnerWeeks.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    it.get(position).id?.let {
                        _week = it
                        schedule(it)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

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
}