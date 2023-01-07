package uz.jbnuu.tsc.student.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.jbnuu.tsc.student.R
import uz.jbnuu.tsc.student.databinding.ItemAttendanceBinding
import uz.jbnuu.tsc.student.model.attendance.AttendanceData
import uz.jbnuu.tsc.student.utils.MyDiffUtil
import uz.jbnuu.tsc.student.utils.getDateTime
import java.text.SimpleDateFormat
import java.util.*


class AttendanceAdapter(val listener: OnItemClickListener) : RecyclerView.Adapter<AttendanceAdapter.MyViewHolder>() {

    var dataProduct = emptyList<AttendanceData>()
    var next: Int? = null

    fun setData(newData: List<AttendanceData>) {
        val diffUtil = MyDiffUtil(dataProduct, newData)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        dataProduct = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

    interface OnItemClickListener {
        fun onItemClick(data: AttendanceData, type: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemAttendanceBinding = ItemAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)// DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.item_all_notification, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataProduct[position])
    }

    override fun getItemCount(): Int = dataProduct.size

    inner class MyViewHolder(private val binding: ItemAttendanceBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: AttendanceData) {
            if (bindingAdapterPosition % 2 == 1) {
                binding.itemBack.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.items_color_0))
            } else {
                binding.itemBack.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.items_color_1))
            }
            binding.numberSort.text = "" + (bindingAdapterPosition + 1)
            data.semester?.name?.let {
                binding.semester.text = it
            }
            data.lesson_date?.let {
                binding.dateAttendance.text = getDateTime(it) + " " + data.lessonPair?.start_time
            }
            data.subject?.name?.let {
                binding.subject.text = it.get(0).uppercase() + it.substring(1).lowercase()
            }
            data.trainingType?.name?.let {
                binding.training.text = it
            }
            if (data.absent_on != 0) {
                binding.absent.text = "Ha"
                binding.hours.text = "" + data.absent_on
            } else if (data.absent_off != 0) {
                binding.absent.text = "Yo'q"
                binding.hours.text = "" + data.absent_off
            }
            data.employee?.name?.let {
                binding.fio.text = it
            }
        }
    }


}
