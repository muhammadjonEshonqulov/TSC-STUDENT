package uz.jbnuu.tsc.student.model.subjects.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import uz.jbnuu.tsc.student.model.schedule.Subject

class SubjectConverter {
    
    @TypeConverter
    fun dataTeacherToJson(dataTeacher: Subject?) : String = Gson().toJson(dataTeacher)
    
    @TypeConverter
    fun jsonToTeacher(dataTeacher: String) : Subject? = Gson().fromJson(dataTeacher, Subject::class.java)
}