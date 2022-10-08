package uz.jbnuu.tsc.student.model.subjects

import uz.jbnuu.tsc.student.model.schedule.Subject
import uz.jbnuu.tsc.student.model.schedule.TrainingType

data class SubjectsData(
    val subject: Subject?,
    val subjectType: TrainingType?,
    val _semester: String?,
    val total_acload: Int?,
    val credit: Int?
)
