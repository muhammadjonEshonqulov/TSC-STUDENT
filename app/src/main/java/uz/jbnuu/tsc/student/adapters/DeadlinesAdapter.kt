package uz.jbnuu.tsc.student.adapters

import android.annotation.SuppressLint
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.downloader.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.jbnuu.tsc.student.R
import uz.jbnuu.tsc.student.databinding.ItemDeadlineBinding
import uz.jbnuu.tsc.student.model.subjects.FilterData
import uz.jbnuu.tsc.student.model.subjects.Task
import uz.jbnuu.tsc.student.utils.MyDiffUtil
import uz.jbnuu.tsc.student.utils.Prefs
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class DeadlinesAdapter : RecyclerView.Adapter<DeadlinesAdapter.MyViewHolder>() {

    var dataProduct = emptyList<Task>()
    var next: Int? = null

    fun setData(newData: List<Task>) {
        val diffUtil = MyDiffUtil(dataProduct, newData)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        dataProduct = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

//    interface OnItemClickListener {
//        fun onItemClick(data: Task, view: ImageView)
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemDeadlineBinding = ItemDeadlineBinding.inflate(LayoutInflater.from(parent.context), parent, false)// DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.item_all_notification, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataProduct[position])
    }

    override fun getItemCount(): Int = dataProduct.size

    inner class MyViewHolder(private val binding: ItemDeadlineBinding) : RecyclerView.ViewHolder(binding.root), OnDownloadListener, OnProgressListener {

        var status = 0

        private val prefss = Prefs(binding.root.context)

        private fun open() {
//        val data = Content(0, "0", "0", this.data.content_uz, 0, 0)
//            var data = this.data
//            data.content_uz = fileManager.APP_FILE_DIRECTORY_PATH + "file/" + fileManager.convertUrlToStoragePath(url, true)
//            listener?.invoke(data)
        }

        private fun download(filterData: FilterData) {
            val storageMainDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/TSC DOCS")
            if (!storageMainDir.exists()) {
                storageMainDir.mkdirs()
            }
            val downloadRequest = PRDownloader.download(filterData.url, storageMainDir.absolutePath, filterData.name).build()
            downloadRequest.onProgressListener = this
            status = 1
            binding.img.setImageResource(R.drawable.ic_baseline_close_24)
            var downloadId = downloadRequest.start(this)
            prefss.save("" + filterData.url, downloadId)
        }

        private fun cancelDownload(filterData: FilterData) {
            PRDownloader.pause(prefss.get("" + filterData.url, 0))
            status = 0
            binding.img.setImageResource(R.drawable.ic_downloadsimple)
        }

        @SuppressLint("SetTextI18n")
        fun bind(data: Task) {

            binding.downloadFile.setOnClickListener {
                when (status) {
                    0 -> {
                        data.files?.get(0)?.let { it1 -> download(it1) }
                    }
                    1 -> {
                        data.files?.get(0)?.let { it1 -> cancelDownload(it1) }
                    }
                    2 -> {
                        open()
                    }
                }
            }
//            val downloadRequest = PRDownloader.download(url, "${fileManager.APP_FILE_DIRECTORY_PATH}/file/", fileManager.convertUrlToStoragePath(url, true)).build()
//               downloadId = downloadRequest.downloadId
//            downloadRequest.onProgressListener = this
//            status = 1
//       // binding.pb_file?.visibility = View.VISIBLE
//            binding.img.setImageResource(R.drawable.ic_close_24)
//            var downloadId = downloadRequest.start(this)

            if (bindingAdapterPosition % 2 == 1) {
                binding.itemBack.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.items_color_0))
            } else {
                binding.itemBack.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.items_color_1))
            }
            if (data.shake == true) {
                binding.itemBack.animate().translationX(5f).translationY(5f).scaleX(1.2f).scaleY(1.2f).setDuration(1000)
                binding.itemBack.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.items_color_changed))
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.IO) {
                        Thread.sleep(1000)
                        withContext(Dispatchers.Main) {
                            binding.itemBack.animate().translationX(0f).translationY(0f).scaleX(1f).scaleY(1f).setDuration(1000)
                            if (bindingAdapterPosition % 2 == 1) {
                                binding.itemBack.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.items_color_0))
                            } else {
                                binding.itemBack.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.items_color_1))
                            }
                        }
                    }
                }
            }
            binding.number.text = "" + (bindingAdapterPosition + 1)
            binding.titleContentFile.isSelected = true
            binding.subjectName.isSelected = true
            binding.subjectName.text = "" + data.subject?.name?.get(0)?.uppercase() + data.subject?.name?.substring(1)?.lowercase()
            binding.typeTraining.text = "" + data.name
            binding.teacher.text = "" + data.employee?.name
            if (data.files?.isNotEmpty() == true) {
                binding.titleContentFile.text = "" + data.files[0]?.name
                data.files[0]?.size?.let {
                    if (it > 1048576) {
                        binding.sizeFile.text = "" + it.toFloat() / 1024 / 1024 + " Mb"
                    } else {
                        binding.sizeFile.text = "" + String.format("%.2f", it.toFloat() / 1024) + " kb"

                    }
                }
            } else {
                binding.titleContentFile.text = "File mavjud emas"
                binding.titleContentFile.setTextColor(ContextCompat.getColor(binding.root.context, R.color.new_tab_color))
            }
            data.deadline?.let {
                binding.deadline.text = "" + getDateTime(it)
            }
            binding.number.transitionName = "DeadlinesAdapter$bindingAdapterPosition"
        }

        override fun onDownloadComplete() {

        }

        override fun onError(error: Error?) {

        }

        override fun onProgress(progress: Progress?) {

        }


    }

    private fun getDateTime(s: Long): String? {
        return try {
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")
            val netDate = Date(s * 1000)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }
}
