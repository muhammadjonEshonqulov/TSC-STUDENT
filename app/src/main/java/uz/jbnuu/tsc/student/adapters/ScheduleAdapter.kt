package uz.jbnuu.tsc.student.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import uz.jbnuu.tsc.student.databinding.ItemTimeTableBinding
import uz.jbnuu.tsc.student.model.schedule.WeekDaysData
import uz.jbnuu.tsc.student.utils.MyDiffUtil
import java.text.SimpleDateFormat
import java.util.*


class ScheduleAdapter(val listener: OnItemClickListener) : RecyclerView.Adapter<ScheduleAdapter.MyViewHolder>() {

    var dataProduct = emptyList<WeekDaysData>()
    var next: Int? = null

    fun setData(newData: List<WeekDaysData>) {
        val diffUtil = MyDiffUtil(dataProduct, newData)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        dataProduct = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

    interface OnItemClickListener {
        fun onItemClick(data: WeekDaysData, type: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemTimeTableBinding = ItemTimeTableBinding.inflate(LayoutInflater.from(parent.context), parent, false)// DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.item_all_notification, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataProduct[position])
    }

    override fun getItemCount(): Int = dataProduct.size

    inner class MyViewHolder(private val binding: ItemTimeTableBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(data: WeekDaysData) {
            val lessonsAdapter = LessonsAdapter()
            binding.listLessons.adapter = lessonsAdapter
            binding.listLessons.layoutManager = LinearLayoutManager(binding.root.context)
            data.schedules?.let {
                lessonsAdapter.setData(it)
            }

            data.lesson_date?.let {
                binding.dateWeek.text = getDateTime(it)
                binding.weekDay.text = getDayOfWeek(it)
            }
        }
    }


    private fun getDateTime(s: Long): String? {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val netDate = Date(s * 1000)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    private fun getDayOfWeekUzbek(day: String): String {
        return when (day) {
            "Monday" -> "Dushanba"
            "Tuesday" -> "Seshanba"
            "Wednesday" -> "Chorshanba"
            "Thursday" -> "Payshanba"
            "Friday" -> "Juma"
            "Saturday" -> "Shanba"
            else -> "Yakshanba"
        }
    }

    fun getDayOfWeek(timestamp: Long): String {
        return getDayOfWeekUzbek(SimpleDateFormat("EEEE", Locale.ENGLISH).format(timestamp * 1000))
    }
}
