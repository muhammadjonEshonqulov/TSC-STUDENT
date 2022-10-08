package uz.jbnuu.tsc.student.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import uz.jbnuu.tsc.student.R
import uz.jbnuu.tsc.student.databinding.CustomSpinnerItemBinding
import uz.jbnuu.tsc.student.model.semester.WeekData

class WeeksAdapter(val context: Context, var dataSource: List<WeekData>) : BaseAdapter() {

    lateinit var binding: CustomSpinnerItemBinding

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.custom_spinner_item, parent, false)
        binding = CustomSpinnerItemBinding.bind(view)
        var vh = ItemHolder(binding.root)
        vh.label.text = dataSource[position].start_date_f + " / " + dataSource[position].end_date_f
        return binding.root
    }

    private class ItemHolder(row: View?) {
        val label: TextView = row?.findViewById(R.id.name_week) as TextView
    }
}
