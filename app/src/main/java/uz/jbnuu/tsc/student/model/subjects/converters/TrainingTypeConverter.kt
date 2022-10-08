package uz.jbnuu.tsc.student.model.subjects.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import uz.jbnuu.tsc.student.model.schedule.TrainingType

class TrainingTypeConverter {
    
    @TypeConverter
    fun dataTrainingTypeToJson(dataTrainingType: TrainingType?) : String = Gson().toJson(dataTrainingType)
    
    @TypeConverter
    fun jsonToTrainingType(dataTrainingType: String) : TrainingType? = Gson().fromJson(dataTrainingType, TrainingType::class.java)
}