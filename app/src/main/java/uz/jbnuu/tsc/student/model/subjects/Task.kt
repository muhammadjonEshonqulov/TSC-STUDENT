package uz.jbnuu.tsc.student.model.subjects

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import uz.jbnuu.tsc.student.model.schedule.Subject
import uz.jbnuu.tsc.student.model.schedule.TrainingType
import uz.jbnuu.tsc.student.model.subjects.converters.FilterDataConverter
import uz.jbnuu.tsc.student.model.subjects.converters.StudentTaskActivityConverter
import uz.jbnuu.tsc.student.model.subjects.converters.SubjectConverter
import uz.jbnuu.tsc.student.model.subjects.converters.TrainingTypeConverter

@Entity
data class Task(
    @PrimaryKey
    val id: Int,
    @TypeConverters(SubjectConverter::class)
    val subject: Subject?,
    val name: String?,
    val comment: String?,
    val max_ball: Int?,
    val deadline: Long?,
    @TypeConverters(TrainingTypeConverter::class)
    val trainingType: TrainingType?,
    val attempt_limit: Int?,
    val attempt_count: Int?,
    @TypeConverters(FilterDataConverter::class)
    val files: List<FilterData?>?,
    @TypeConverters(TrainingTypeConverter::class)
    val taskType: TrainingType?,
    @TypeConverters(TrainingTypeConverter::class)
    val taskStatus: TrainingType?,
    @TypeConverters(SubjectConverter::class)
    val employee: Subject?,
    val updated_at: Int?,
    var shake: Boolean? = false,
    @TypeConverters(StudentTaskActivityConverter::class)
    val studentTaskActivity: StudentTaskActivity?,
)
