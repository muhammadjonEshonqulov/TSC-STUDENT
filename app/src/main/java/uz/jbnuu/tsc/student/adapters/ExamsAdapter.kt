package uz.jbnuu.tsc.student.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.jbnuu.tsc.student.databinding.ItemExamTableBinding
import uz.jbnuu.tsc.student.model.examTable.ExamTableData
import uz.jbnuu.tsc.student.utils.MyDiffUtil
import java.text.SimpleDateFormat
import java.util.*


class ExamsAdapter : RecyclerView.Adapter<ExamsAdapter.MyViewHolder>() {

    var dataProduct = emptyList<ExamTableData>()
    var next: Int? = null

    fun setData(newData: List<ExamTableData>) {
        val diffUtil = MyDiffUtil(dataProduct, newData)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        dataProduct = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

//    interface OnItemClickListener {
//        fun onItemClick(data: ExamTableData, type: Int)
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemExamTableBinding = ItemExamTableBinding.inflate(LayoutInflater.from(parent.context), parent, false)// DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.item_all_notification, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataProduct[position])
    }

    override fun getItemCount(): Int = dataProduct.size

    inner class MyViewHolder(private val binding: ItemExamTableBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: ExamTableData) {
            binding.numberSort.text = "" + (bindingAdapterPosition + 1) + "."
            data.subject?.name?.let {
                binding.subjectName.text = ": " + it[0].uppercase() + it.substring(1).lowercase()
            }
            data.auditorium?.name?.let {
                binding.auditorium.text = ": $it-xona"
            }
            data.examType?.name?.let {
                binding.examType.text = ": $it"
            }
            data.examDate?.let { examDate ->
                data.lessonPair?.apply {
                    binding.dateExam.text = ": " + getDateTime(examDate) + " " + start_time + " / " + end_time
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDateTime(s: Long): String? {
        return try {
            val sdf = SimpleDateFormat("dd-MM-yyyy")
            val netDate = Date(s * 1000)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }
}
