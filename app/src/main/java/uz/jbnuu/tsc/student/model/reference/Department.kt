package uz.jbnuu.tsc.student.model.reference

import uz.jbnuu.tsc.student.model.schedule.TrainingType

data class Department(
    val id: Int?,
    val name: String?,
    val structureType: TrainingType?
)
