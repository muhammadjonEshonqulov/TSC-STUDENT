package uz.jbnuu.tsc.student.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.jbnuu.tsc.student.R
import uz.jbnuu.tsc.student.databinding.ItemPerformanceBinding
import uz.jbnuu.tsc.student.model.performance.PerformanceData
import uz.jbnuu.tsc.student.utils.MyDiffUtil


class PerformanceAdapter(val listener: OnItemClickListener) : RecyclerView.Adapter<PerformanceAdapter.MyViewHolder>() {

    var dataProduct = emptyList<PerformanceData>()
    var next: Int? = null

    fun setData(newData: List<PerformanceData>) {
        val diffUtil = MyDiffUtil(dataProduct, newData)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        dataProduct = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

    interface OnItemClickListener {
        fun onItemClick(data: PerformanceData, type: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemPerformanceBinding = ItemPerformanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)// DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.item_all_notification, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataProduct[position])
    }

    override fun getItemCount(): Int = dataProduct.size

    inner class MyViewHolder(private val binding: ItemPerformanceBinding) : RecyclerView.ViewHolder(binding.root) {


        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bind(data: PerformanceData) {
            if (bindingAdapterPosition % 2 == 1) {
                binding.itemBack.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.items_color_0))
            } else {
                binding.itemBack.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.items_color_1))
            }
            data.subject?.name?.let {
                binding.subject.text = it
            }
            binding.number.text = "" + (bindingAdapterPosition + 1)
            data.performances?.forEach {
                if (it.examType?.name == "Joriy nazorat") {
                    binding.jn.text = "" + it.grade?.toDouble()
                }
                if (it.examType?.name == "Oraliq nazorat") {
                    binding.on.text = "" + it.grade?.toDouble()
                }
                if (it.examType?.name == "Yakuniy nazorat") {
                    binding.yn.text = "" + it.grade?.toDouble()
                }
                if (it.examType?.name == "Umumiy") {
                    binding.total.text = "" + it.grade?.toDouble()
                }
            }
        }
    }
}
