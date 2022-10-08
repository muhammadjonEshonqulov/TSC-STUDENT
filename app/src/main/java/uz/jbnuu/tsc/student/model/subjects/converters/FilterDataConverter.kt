package uz.jbnuu.tsc.student.model.subjects.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import uz.jbnuu.tsc.student.model.subjects.FilterData

class FilterDataConverter {

    @TypeConverter
    fun dataFilterDataToJson(dataFilterData: List<FilterData?>?): String? = Gson().toJson(dataFilterData)

    @TypeConverter
    fun jsonToFilterData(dataFilterData: String?): List<FilterData?>? = Gson().fromJson(dataFilterData, Array<FilterData>::class.java)?.toList()
}
