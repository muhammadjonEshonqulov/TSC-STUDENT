package uz.jbnuu.tsc.student.model.subjects

import androidx.room.TypeConverters
import uz.jbnuu.tsc.student.model.subjects.converters.FilesDataConverter

data class StudentTaskActivity(
    val id: Int?,
    val comment: String?,
    val _task: Int?,
    val send_date: Int?,
    @TypeConverters(FilesDataConverter::class)
    val files: List<FilesData>?,
    val mark: Float?,
    val marked_comment: String?,
    val marked_date: Long?,
)
