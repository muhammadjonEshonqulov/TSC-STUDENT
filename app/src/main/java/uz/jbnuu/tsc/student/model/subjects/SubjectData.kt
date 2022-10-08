package uz.jbnuu.tsc.student.model.subjects

import uz.jbnuu.tsc.student.model.schedule.Subject
import uz.jbnuu.tsc.student.model.schedule.TrainingType

data class SubjectData(
    val subject: Subject?,
    val subjectType: TrainingType?,
    val _semester: String?,
    val total_acload: Int?,
    val credit: Int?,
    val tasks_count: Int?,
    val submits_count: Int?,
    val marked_count: Int?,
    val resources_count: Int?,
    val absent_count: Int?,
    val tasks: List<Task>?,
)
