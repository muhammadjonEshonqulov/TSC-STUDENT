package uz.jbnuu.tsc.student.ui.deadlines

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.jbnuu.tsc.student.BuildConfig
import uz.jbnuu.tsc.student.R
import uz.jbnuu.tsc.student.adapters.DeadlinesAdapter
import uz.jbnuu.tsc.student.base.BaseFragment
import uz.jbnuu.tsc.student.databinding.DeadlinesFragmentBinding
import uz.jbnuu.tsc.student.model.subjects.Task
import uz.jbnuu.tsc.student.ui.MainActivity
import uz.jbnuu.tsc.student.utils.Prefs
import uz.jbnuu.tsc.student.utils.collectLA
import uz.jbnuu.tsc.student.utils.lg
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class DeadlineFragment : BaseFragment<DeadlinesFragmentBinding>(DeadlinesFragmentBinding::inflate) {

    private val vm: DeadlinesViewModel by viewModels()
    private val deadlinesAdapter: DeadlinesAdapter by lazy { DeadlinesAdapter() }

    lateinit var tasks: ArrayList<Task>

    var taskId = -1

    @Inject
    lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()
        arguments?.getInt("task_id")?.let {
            taskId = it
            lg("taskId->$taskId")
        }
        tasks = ArrayList()
        getDeadlines()
    }

    private fun setupRecycler() {
        binding.listDeadlines.apply {
            adapter = deadlinesAdapter
            layoutManager = LinearLayoutManager(binding.root.context)
        }
    }

    private fun openFile(url: String) {

        val intent = Intent(
            Intent.ACTION_VIEW
        )
//            val targetUri = Uri.fromFile(File(url))
        val targetUri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".provider", File(url))

        if (targetUri.toString().contains(".doc") || targetUri.toString().contains(".docx")) {
            intent.setDataAndType(targetUri, "application/msword");
        } else if (targetUri.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(targetUri, "application/pdf")
        } else if (targetUri.toString().contains(".ppt") || targetUri.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(targetUri, "application/vnd.ms-powerpoint");
        } else if (targetUri.toString().contains(".xls") || targetUri.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(targetUri, "application/vnd.ms-excel")
        } else
            intent.setDataAndType(targetUri, "application/*")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

    override fun onViewCreatedd(view: View, savedInstanceState: Bundle?) {

        var arraySpinner = arrayOf("Deadlinelar", "Vaqti o'tib ketganlar", "Topshirilganlar", "Hammasi")
        val organizationAdapter = ArrayAdapter(binding.root.context, R.layout.simple_spinner_item, arraySpinner)
        organizationAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerOrganization.adapter = organizationAdapter
        binding.spinnerOrganization.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                prefs.save("spinner_id", p2)
                setTasks()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }
        binding.backBtn.setOnClickListener { finish() }
        binding.titleDeadline.isSelected = true
        setupRecycler()
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

    private fun setTasks() {
        if (tasks.isEmpty()) {
            binding.notFoundLesson.visibility = View.VISIBLE
            binding.listPerformanceLay.visibility = View.GONE
        } else {
            binding.notFoundLesson.visibility = View.GONE
            binding.listPerformanceLay.visibility = View.VISIBLE
            val data = ArrayList<Task>()
            data.clear()
            val currentTimeStamp = System.currentTimeMillis() / 1000L
            when (prefs.get("spinner_id", 0)) {
                0 -> {
                    tasks.forEach { task ->
                        task.deadline?.let {
                            if (currentTimeStamp <= it) {
                                data.add(task)
                            }
                        }

                    }
                }
                1 -> {
                    tasks.forEach { task ->
                        task.deadline?.let {
                            if (currentTimeStamp > it && task.studentTaskActivity == null) {
                                data.add(task)
                            }
                        }
                    }
                }
                2 -> {
                    tasks.forEach {
                        if (it.studentTaskActivity != null) {
                            data.add(it)
                        }
                    }
                }
                3 -> {
                    data.addAll(tasks)
                }
            }
            deadlinesAdapter.setData(listOf())
            deadlinesAdapter.setData(data)
            if (taskId > 0) {
                var pos = -1
                tasks.forEachIndexed { index, task ->
                    if (task.id == taskId) {
                        task.shake = true
                        pos = index
                    } else {
                        task.shake = false

                    }
                }
                if (pos > 0) {
                    binding.listDeadlines.layoutManager?.scrollToPosition(pos)
                    deadlinesAdapter.notifyItemChanged(pos)
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.IO) {
                            Thread.sleep(1000)
                        }
                        deadlinesAdapter.dataProduct.get(pos).shake = false
                    }
                }
            }
        }
    }

    private fun getDeadlines() {
        vm.getTaskData()
        vm.taskDataResponse.collectLA(lifecycleScope) {
            tasks.clear()
            tasks.addAll(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.listDeadlines.adapter = null
    }
}