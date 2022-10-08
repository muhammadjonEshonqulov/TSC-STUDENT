package uz.jbnuu.tsc.student.model.subjects.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import uz.jbnuu.tsc.student.model.subjects.FilesData

class Converter {

    @TypeConverter
    fun dataFilesDataToJson(dataFilesData: FilesData?): String = Gson().toJson(dataFilesData)

    @TypeConverter
    fun jsonToFilesData(dataFilesData: String): FilesData? = Gson().fromJson(dataFilesData, FilesData::class.java)
}