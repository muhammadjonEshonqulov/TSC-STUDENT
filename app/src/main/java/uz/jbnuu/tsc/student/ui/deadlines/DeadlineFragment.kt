package uz.jbnuu.tsc.student.ui.deadlines

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.jbnuu.tsc.student.BuildConfig
import uz.jbnuu.tsc.student.adapters.DeadlinesAdapter
import uz.jbnuu.tsc.student.base.BaseFragment
import uz.jbnuu.tsc.student.databinding.DeadlinesFragmentBinding
import uz.jbnuu.tsc.student.ui.MainActivity
import uz.jbnuu.tsc.student.utils.collectLatestLA
import uz.jbnuu.tsc.student.utils.lg
import java.io.File

@AndroidEntryPoint
class DeadlineFragment : BaseFragment<DeadlinesFragmentBinding>(DeadlinesFragmentBinding::inflate) {

    private val vm: DeadlinesViewModel by viewModels()
    private val deadlinesAdapter: DeadlinesAdapter by lazy { DeadlinesAdapter() }

    var taskId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()
        arguments?.getInt("task_id")?.let {
            taskId = it
            lg("taskId->$taskId")
        }
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
        getDeadlines()
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
        lg("MainActivity().subjectTasks -> " + MainActivity().subjectTasks)
    }

    private fun getDeadlines() {
        vm.getTaskData()
        vm.taskDataResponse.collectLatestLA(lifecycleScope) {

            if (it.isEmpty()) {
                binding.notFoundLesson.visibility = View.VISIBLE
                binding.listPerformanceLay.visibility = View.GONE
            } else {
                binding.notFoundLesson.visibility = View.GONE
                binding.listPerformanceLay.visibility = View.VISIBLE
                deadlinesAdapter.setData(it)
                if (taskId > 0) {
                    var pos = -1
                    it.forEachIndexed { index, task ->
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
//                        CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.IO) {
                            Thread.sleep(1000)
                            deadlinesAdapter.dataProduct.get(pos).shake = false
                        }

//                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.listDeadlines.adapter = null
    }
}