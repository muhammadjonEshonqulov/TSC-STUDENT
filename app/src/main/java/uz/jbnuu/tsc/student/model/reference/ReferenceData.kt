package uz.jbnuu.tsc.student.model.reference

import uz.jbnuu.tsc.student.model.schedule.TrainingType
import uz.jbnuu.tsc.student.model.semester.SemestersData

data class ReferenceData(
    val id: Int?,
    val reference_number: String?,
    val department: Department?,
    val semester: SemestersData?,
    val level: TrainingType?,
    val file: String?,
    val reference_date: Long?
)
