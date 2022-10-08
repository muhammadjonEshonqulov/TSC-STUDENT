package uz.jbnuu.tsc.student.model

import uz.jbnuu.tsc.student.model.subjects.SubjectData

data class SubjectResponse(
    val success: Boolean?,
    val error: String?,
    val data: SubjectData?,
    val code: Int?,
)
