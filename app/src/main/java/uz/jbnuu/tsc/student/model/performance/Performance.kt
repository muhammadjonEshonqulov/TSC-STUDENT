package uz.jbnuu.tsc.student.model.performance

import uz.jbnuu.tsc.student.model.schedule.TrainingType

data class Performance(
    val grade: Int?,
    val max_ball: Int?,
    val label: String?,
    val examType: TrainingType?
)
