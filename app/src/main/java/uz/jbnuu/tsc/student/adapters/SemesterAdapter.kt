package uz.jbnuu.tsc.student.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.jbnuu.tsc.student.R
import uz.jbnuu.tsc.student.databinding.ItemSemesterBinding
import uz.jbnuu.tsc.student.model.semester.SemestersData
import uz.jbnuu.tsc.student.utils.MyDiffUtil
import uz.jbnuu.tsc.student.utils.Prefs
import java.text.SimpleDateFormat
import java.util.*


class SemesterAdapter(val listener: OnItemClickListener) : RecyclerView.Adapter<SemesterAdapter.MyViewHolder>() {

    var dataProduct = emptyList<SemestersData>()
    var next: Int? = null

    fun setData(newData: List<SemestersData>) {
        val diffUtil = MyDiffUtil(dataProduct, newData)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        dataProduct = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

    interface OnItemClickListener {
        fun onItemClick(data: SemestersData, position: Int, type: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemSemesterBinding = ItemSemesterBinding.inflate(LayoutInflater.from(parent.context), parent, false)// DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.item_all_notification, parent, false)
        return MyViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataProduct[position])
    }

    override fun getItemCount(): Int = dataProduct.size

    inner class MyViewHolder(private val binding: ItemSemesterBinding) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.N)
        @SuppressLint("SetTextI18n")
        fun bind(data: SemestersData) {
//            lg("semester -> $data")
//            val now :Date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
//            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//            val currentDateAndTime = sdf.format(Date()).getTime()
//
//            val timeStamp = Timestamp(System.currentTimeMillis())
            val timeStamp = System.currentTimeMillis() / 1000L

//            if (data.currentExtra == null) {
//                data.weeks?.forEach {
//                    it.start_date?.let { start_date ->
//                        it.end_date?.let { end_date ->
//                            if (timeStamp in start_date..end_date) {
//                                binding.semesterName.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.green))
//                                data.currentExtra = true
//                                listener.onItemClick(data, bindingAdapterPosition, 0)
//                                return@forEach
//                            }
//                        }
//                    }
//
//                }
//            }
            if (data.currentExtra == true) {
                binding.semesterName.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.cl_color_primary))
                binding.semesterName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
            } else if (data.currentExtra == false) {
                binding.semesterName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                binding.semesterName.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white))
            } else {
                val prefs = Prefs(binding.root.context)
                if (data.code == prefs.get(prefs.semester, "")) {
                    listener.onItemClick(data, bindingAdapterPosition, 1)
                }
            }

            binding.semesterName.text = data.name?.split("-")?.first()
            binding.semesterName.setOnClickListener {
                listener.onItemClick(data, bindingAdapterPosition, 1)
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
}
