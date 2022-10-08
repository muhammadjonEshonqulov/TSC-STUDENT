package uz.jbnuu.tsc.student.model.performance

import uz.jbnuu.tsc.student.model.schedule.Subject
import uz.jbnuu.tsc.student.model.schedule.TrainingType

data class PerformanceData(
    val _semester: String?,
    val subject: Subject?,
    val subjectType: TrainingType?,
    val total_acload: Int?,
    val credit: Int?,
    val overall: Int?,
    val performances: List<Performance>?,
)
